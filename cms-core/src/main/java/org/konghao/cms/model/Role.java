package org.konghao.cms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色对象，用来对应可以访问的功能，系统中为了简单，只定义了管理员，发布人员和审核人员
 * 
 * @author Home
 * 
 */
@Entity
@Table(name = "t_role")
public class Role {

	public Role() {
	}

	public Role(int id, String name, RoleType roleType) {
		super();
		this.id = id;
		this.name = name;
		this.roleType = roleType;
	}

	/**
	 * 角色id
	 */
	private int id;
	/**
	 * 角色名称
	 */
	private String name;
	/**
	 * 角色编号,枚举类型
	 */
	private RoleType roleType;

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

	@Enumerated(EnumType.STRING)
	@Column(name="role_type")
	public RoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

}
