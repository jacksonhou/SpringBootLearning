package org.apache.ibatis.intercepter;

import com.alibaba.fastjson.JSON;
import com.jadmin.api.core.paging.PagingBean;
import com.jadmin.api.core.paging.QueryFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.xml.bind.PropertyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;



@Intercepts( { @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor
{
	private Log logger = LogFactory.getLog(PaginationInterceptor.class);

	private static final String MYSQL = "mysql";

	private static final String ORACLE = "oracle";

	private static String dialect = ""; // 数据库方言

	private static String pageSqlId = ""; // mapper.xml中需要拦截的ID(正则匹配)

	public Object intercept(Invocation ivk) throws Throwable
	{
		if (ivk.getTarget() instanceof RoutingStatementHandler)
		{
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");

			if (logger.isDebugEnabled())
			{
				logger.debug("Sql-ID:" + mappedStatement.getId());
				if (!mappedStatement.getId().matches(pageSqlId))
                {
                    logger.debug("Sql-String:" + delegate.getBoundSql().getSql());
                    Object parameterObject = delegate.getBoundSql().getParameterObject();
                    logger.debug("Sql-parameter:" + JSON.toJSONString(parameterObject));
                }
			}
			if (mappedStatement.getId().matches(pageSqlId))
			{
				// 拦截需要分页的SQL
				BoundSql boundSql = delegate.getBoundSql();
				Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
				if (parameterObject == null)
				{
					throw new NullPointerException("parameterObject尚未实例化！");
				}
				else
				{
					Connection connection = (Connection) ivk.getArgs()[0];
					String sql = boundSql.getSql();
					String countSql = "select count(1) from (" + sql + ") AA"; // 记录统计

					if (logger.isDebugEnabled())
					{
						logger.debug("COUNT SQL->" + countSql);
						parameterObject = delegate.getBoundSql().getParameterObject();
                        logger.debug("Sql-parameter:" + JSON.toJSONString(parameterObject));
					}

					PreparedStatement countStmt = connection.prepareStatement(countSql);
					BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);
					setParameters(countStmt, mappedStatement, countBS, parameterObject);
					long start = System.currentTimeMillis();
					ResultSet rs = countStmt.executeQuery();
					int count = 0;
					if (rs.next())
					{
						count = rs.getInt(1);
					}
					rs.close();
					countStmt.close();
					long end = System.currentTimeMillis();
					logger.debug("Cost：" + ((end - start) / 1000 / 60) + "m" + ((end - start) / 1000 % 60) + "s");
					QueryFilter filter = null;
					if (parameterObject instanceof QueryFilter)
					{
						// 参数就是Page实体
						filter = (QueryFilter) parameterObject;
						filter.getPagingBean().setTotalItems(count);
					}
					String pageSql = generatePageSql(sql, filter);

					if (logger.isDebugEnabled())
					{
						logger.debug("SQL->" + pageSql);
					}

					ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
				}
			}
		}
		return ivk.proceed();
	}

	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException
	{
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null)
		{
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++)
			{
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT)
				{
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null)
					{
						value = null;
					}
					else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass()))
					{
						value = parameterObject;
					}
					else if (boundSql.hasAdditionalParameter(propertyName))
					{
						value = boundSql.getAdditionalParameter(propertyName);
					}
					else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName()))
					{
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null)
						{
							value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
						}
					}
					else
					{
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null)
					{
						throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement "
								+ mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePageSql(String sql, QueryFilter filter)
	{
		PagingBean page = filter.getPagingBean();
		if (page != null && StringUtils.isNotEmpty(dialect))
		{
			StringBuffer pageSql = new StringBuffer(500);

			// 增加手动排序功能
			// ace-admin框架
			if (!StringUtils.isEmpty(filter.getParam().get("sidx")) && !StringUtils.isEmpty(filter.getParam().get("sord")))
			{
				sql = "select AA.* from (" + sql + ") AA order by AA." + filter.getParam().get("sidx") + " " + filter.getParam().get("sord");
			}
			
			if (MYSQL.equals(dialect))
			{
				// pageNo为0表示不对原有sql进行分页查询
				if (0 == page.getPageNo())
				{
					pageSql.append(sql);
				}
				else
				{
					pageSql.append(sql);
					pageSql.append(" limit " + (page.getStart() - 1) + "," + page.getPageSize());
				}
			}
			else if (ORACLE.equals(dialect))
			{
				// pageNo为0表示不对原有sql进行分页查询
				if (0 == page.getPageNo())
				{
					pageSql.append(sql);
				}
				else
				{
					pageSql.append("SELECT * FROM (SELECT A.*, ROWNUM RN FROM (");
					pageSql.append(sql);
					pageSql.append(") A WHERE ROWNUM <= ");
					pageSql.append(page.getLimit());
					pageSql.append(") WHERE RN >= ");
					pageSql.append(page.getStart());
				}
			}
			return String.valueOf(pageSql);
		}
		else
		{
			return sql;
		}
	}

	public Object plugin(Object arg0)
	{
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties p)
	{
		dialect = p.getProperty("dialect");
		if (!StringUtils.isNotEmpty(dialect))
		{
			try
			{
				throw new PropertyException("dialect property is not found!");
			}
			catch (PropertyException e)
			{
				logger.error("MyBatis分页插件配置中的数据库方言为空！", e);
			}
		}
		pageSqlId = p.getProperty("pageSqlId");
		if (!StringUtils.isNotEmpty(pageSqlId))
		{
			try
			{
				throw new PropertyException("pageSqlId property is not found!");
			}
			catch (PropertyException e)
			{
				logger.error("MyBatis分页插件配置中的pageSqlId为空！", e);
			}
		}
	}

	public String getDialect()
	{
		return dialect;
	}

	public void setDialect(String dialect)
	{
		PaginationInterceptor.dialect = dialect;
	}

	public String getPageSqlId()
	{
		return pageSqlId;
	}

	public void setPageSqlId(String pageSqlId)
	{
		PaginationInterceptor.pageSqlId = pageSqlId;
	}

}
