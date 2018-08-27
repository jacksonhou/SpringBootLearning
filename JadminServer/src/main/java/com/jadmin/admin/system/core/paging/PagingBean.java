package com.jadmin.admin.system.core.paging;

/**
 * 分页类
 * @Title: PagingBean.java 
 * @Package com.tinno.gamecenter.core.paging 
 * @author 侯永波
 * @date 2016-04-20 09:55:02 
 * @version V1.0
 */
public class PagingBean
{
	public static Integer DEFAULT_PAGE_SIZE = 25;

	/**
	 * 每页开始的索引号
	 */
	public Integer start;

	/**
	 * 每页结束的索引号
	 */
	private Integer limit;

	// 总记录数
	private Integer totalItems = 0;

	// 页号（如果pageNo为0表示不进行分页查询）
	private Integer pageNo;

	private Integer pageSize;

	public PagingBean()
	{}

	/**
	 * 分页构造
	 * @param start 开始行
	 * @param limit 结束行
	 * @param pageNo 页号
	 */
	public PagingBean(int start, int limit, int pageNo, int pageSize)
	{
		this.limit = limit;
		this.start = start;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public int getTotalItems()
	{
		return totalItems;
	}

	public Integer getStart()
	{
		return start;
	}

	public void setStart(Integer start)
	{
		this.start = start;
	}

	public void setTotalItems(Integer totalItems)
	{
		this.totalItems = totalItems;
	}

	public void setTotalItems(int totalItems)
	{
		this.totalItems = totalItems;
	}

	public int getFirstResult()
	{
		return start;
	}

	public Integer getLimit()
	{
		return limit;
	}

	public void setLimit(Integer limit)
	{
		this.limit = limit;
	}

	public Integer getPageNo()
	{
		return pageNo;
	}

	/**
	 * 设置页号
	 * @author 侯永波
	 * @date 2013-3-4 下午03:16:37
	 * @param pageNo 如果pageNo为0表示不进行分页查询
	 */
	public void setPageNo(Integer pageNo)
	{
		this.pageNo = pageNo;
	}

	public Integer getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}
}
