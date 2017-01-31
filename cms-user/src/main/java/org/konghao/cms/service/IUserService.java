package org.konghao.cms.service;

import java.util.List;

import org.konghao.basic.model.Pager;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.User;

public interface IUserService {
	/**
	 * 添加用户，需要判断用户名是否存在，如果存在需要抛出异常
	 * @param user 用户对象
	 * @param roleIds 用户所有角色信息
	 * @param groupIds 用户所有组信息
	 */
	public void add(User user,Integer[] roleIds,Integer[] groupIds);
	
	/**
	 * 删除用户，注意需要把用户和角色和组的对应关系删除
	 * 注意，如果用户存在相应的文章不能删除
	 * @param id 用户id
	 */
	public void delete(int id);
	
	/**
	 * 更新用户
	 * 如果本次更新的用户和角色关系与原来比保持关联角色不变，在库中已经存在关系记录，则不进行操作
	 * 如果本次更新的用户和角色关系与原来比增加关联角色关系，在库中不存在关系记录，则进行现在操作
	 * 如果本次更新的用户和角色关系与原来比去除关联角色关系，在库中已存在关系记录，则进行删除操作
	 * 对于group同样道理
	 * @param user 用户对象
	 * @param roleIds 用户所有角色信息
	 * @param groupIds 用户所有组信息
	 */
	public void update(User user,Integer[] roleIds,Integer[] groupIds);
	
	/**
	 * 更新用户状态
	 * @param id
	 */
	public void updateStatus(int id);
	
	/**
	 * 列表用户
	 * @param id
	 */
	public Pager<User> findUser();
	
	/**
	 * 获取用户信息
	 * @param id
	 * @return
	 */
	public User load(int id);
	
	/**
	 * 根据用户id获取用户拥有的所有角色信息
	 * @param id
	 * @return
	 */
	public List<Role> listUserRoles(int id);
	
	/**
	 * 根据用户id获取用户拥有的所有组信息
	 * @param id
	 * @return
	 */
	public List<Group> listUserGroups(int id);
}
