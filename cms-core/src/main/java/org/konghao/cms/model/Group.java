package org.konghao.cms.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户组对象，使用该对象来获取可以发布文章的栏目信息
 * @author Home
 *
 */
@Entity
@Table(name="t_group")
public class Group {

	public Group() {
		super();
	}
	
	public Group(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public Group(int id, String name, String descr) {
		super();
		this.id = id;
		this.name = name;
		this.descr = descr;
	}

	/**
	 * 用户组id
	 */
	private int id;
	/**
	 * 用户组名称
	 */
	private String name;
	/**
	 * 用户组描述
	 */
	private String descr;
	

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
}
