package org.konghao.cms.dao;

import java.util.List;

import org.konghao.basic.dao.IBaseDao;
import org.konghao.basic.model.Pager;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.RoleType;
import org.konghao.cms.model.User;
import org.konghao.cms.model.UserGroup;
import org.konghao.cms.model.UserRole;

public interface IUserDao extends IBaseDao<User>{
	/**
	 * 获取用户角色实体集合
	 * @return
	 */
	public List<Role> listUserRoles(int userId);
	/**
	 * 获取用户角色id集合
	 * @return
	 */
	public List<Integer> listUserRoleIds(int userId);
	/**
	 * 获取用户组实体集合
	 * @return
	 */
	public List<Group> listUserGroups(int userId);
	/**
	 * 获取用户组id集合
	 * @param userId
	 * @return
	 */
	public List<Integer> listUserGroupIds(int userId);
	/**
	 * 根据用户id和角色id获取用户角色关联对象
	 * @param userId
	 * @param roleId
	 * @return UserRole
	 */
	public UserRole loadUserRole(int userId,int roleId);
	/**
	 * 根据用户id和组id获取用户组关联对象
	 * @param userId
	 * @param groupId
	 * @return UserGroup
	 */
	public UserGroup loadUserGroup(int userId,int groupId);
	/**
	 * 根据用户名查询用户实体对象
	 * @param userName
	 * @return
	 */
	public User loadByUserName(String userName);
	/**
	 * 根据角色id获取用户列表集合
	 * @param roleId
	 * @return
	 */
	public List<User> listRoleUsers(int roleId);
	/**
	 * 根据角色类型获取用户集合列表
	 * @param roleType
	 * @return
	 */
	public List<User> listRoleUsers(RoleType roleType);
	/**
	 * 根据用户组id获取用户列表集合
	 * @param groupId
	 * @return
	 */
	public List<User> listGroupUsers(int groupId);
	/**
	 * 添加用户角色关联对象
	 * @param user
	 * @param role
	 */
	public void addUserRole(User user,Role role);
	/**
	 * 添加用户组关联对象
	 * @param user
	 * @param group
	 */
	public void addUserGroup(User user,Group group);
	/**
	 * 删除用户角色对象
	 * @param uid
	 */
	public void deleteUserRole(int uid);
	/**
	 * 删除用户组对象
	 * @param uid
	 */
	public void deleteUserGroup(int uid);
	/**
	 * 删除用户角色对象
	 * @param uid
	 */
	public void deleteUserRole(Integer uid,Integer roleId);
	/**
	 * 删除用户组对象
	 * @param uid
	 */
	public void deleteUserGroup(Integer uid,Integer groupId);
	/**
	 * 获取用户列表
	 * @return
	 */
	public Pager<User> findUser();
} 
