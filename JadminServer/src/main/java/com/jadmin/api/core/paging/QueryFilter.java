package com.jadmin.api.core.paging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用于前台的组合条件进行数据的过滤
 * @Title: QueryFilter.java
 * @Package com.tinno.weather.core.paging
 * @author 侯永波
 * @date 2016-04-20 09:55:12
 * @version V1.0
 */
public class QueryFilter
{
    public final static Log logger = LogFactory.getLog(QueryFilter.class);

    /**
     * 默认每页显示的记录数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认开始页下表
     */
    public static final Integer DEFAULT_PAGE_NO = 1;

    /**
     * 降序排序
     */
    public static final String ORDER_DESC = "desc";

    /**
     * 升序排序
     */
    public static final String ORDER_ASC = "asc";

    // 查询条件的Map
    private Map<String, String> param = new HashMap<String, String>();

    // 不方便直接放到param里面的查询条件，可以考虑放在此属性中
    private Map<String, Object> extraParam = new HashMap<String, Object>();

    private PagingBean pagingBean;

    private String servletPath = "";

    @SuppressWarnings("unchecked")
    public QueryFilter(HttpServletRequest request)
    {
        servletPath = request.getServletPath();
        Enumeration paramEnu = request.getParameterNames();
        while (paramEnu.hasMoreElements())
        {
            String paramName = (String) paramEnu.nextElement();

            String paramValue = (String) request.getParameter(paramName);
            if (null != paramValue)
            {
                paramValue = paramValue.trim();
            }
            this.param.put(paramName, paramValue);
        }

        // 取得分页的信息
        Integer start = 0; // 开始行
        Integer limit = QueryFilter.DEFAULT_PAGE_SIZE; // 结束行

        String pageNoStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("rows");
        Integer pageNo = QueryFilter.DEFAULT_PAGE_NO;
        Integer pageSize = QueryFilter.DEFAULT_PAGE_SIZE;
        if (StringUtils.isNotEmpty(pageNoStr) && StringUtils.isNotEmpty(pageSizeStr))
        {
            pageNo = Integer.parseInt(pageNoStr);
            pageSize = Integer.parseInt(pageSizeStr);

        }

        start = (pageNo - 1) * pageSize + 1;
        limit = pageNo * pageSize;

        this.param.put("start", String.valueOf(start));
        this.param.put("limit", String.valueOf(limit));

        this.pagingBean = new PagingBean(start, limit, pageNo, pageSize);

    }

    public Map<String, String> getParam()
    {
        return param;
    }

    public void setParam(Map<String, String> param)
    {
        this.param = param;
    }

    public PagingBean getPagingBean()
    {
        return pagingBean;
    }

    public void setPagingBean(PagingBean pagingBean)
    {
        this.pagingBean = pagingBean;
    }

    /**
     * 获取登录用户的用户ID
     * @author 侯永波
     * @date 2013-5-17 下午05:58:40
     * @return BigDecimal 用户ID
     */
    public BigDecimal getUserId()
    {
        if (null != this.param.get("userId") && !"null".equals(this.param.get("userId")))
        {
            return new BigDecimal(this.param.get("userId"));
        }
        else
        {
            return null;
        }
    }

    public Map<String, Object> getExtraParam()
    {
        return extraParam;
    }

    /**
     * 默认构造方法
     */
    public QueryFilter()
    {
        super();
    }

    public void setExtraParam(Map<String, Object> extraParam)
    {
        this.extraParam = extraParam;
    }

    @Override
    public String toString()
    {
        StringBuffer beanString = new StringBuffer(500);
        Iterator<Map.Entry<String, String>> it = this.param.entrySet().iterator();
        beanString.append(servletPath + ":[");
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = it.next();
            beanString.append(entry.getKey() + ":" + entry.getValue()).append(",");
        }
        beanString = beanString.deleteCharAt(beanString.length() - 1);
        beanString.append("]");

        return beanString.toString();
    }

    public String getServletPath()
    {
        return servletPath;
    }

    public void setServletPath(String servletPath)
    {
        this.servletPath = servletPath;
    }
}
