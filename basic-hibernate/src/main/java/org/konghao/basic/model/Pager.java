package org.konghao.basic.model;

import java.util.List;

/**
 * 分页对象
 * @author Home
 *
 * @param <T>
 */
public class Pager<T> {
	/**
	 * 分页的大小
	 */
	private Integer size;
	/**
	 * 分页的起始页
	 */
	private Integer offSet;
	/**
	 * 总记录数
	 */
	private Long total;
	/**
	 * 分页的数据
	 */
	private List<T> datas;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getOffSet() {
		return offSet;
	}

	public void setOffSet(Integer offSet) {
		this.offSet = offSet;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
}
