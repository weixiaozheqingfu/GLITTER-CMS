package org.konghao.cms.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.konghao.basic.model.Pager;
import org.konghao.cms.dao.IGroupDao;
import org.konghao.cms.dao.IRoleDao;
import org.konghao.cms.dao.IUserDao;
import org.konghao.cms.model.CmsException;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.User;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService implements IUserService {

	@Inject
	private IUserDao userDao;
	@Inject
	private IRoleDao roleDao;
	@Inject
	private IGroupDao groupDao;
	
	public IUserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(IUserDao userDao) {
		this.userDao = userDao;
	}
	public IRoleDao getRoleDao() {
		return roleDao;
	}
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}
	public IGroupDao getGroupDao() {
		return groupDao;
	}
	public void setGroupDao(IGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
	@Override
	public void add(User user, Integer[] roleIds, Integer[] groupIds) {
		// 1.1检测用户对象是否在库中已经存在
		User tu = userDao.loadByUserName(user.getUserName());
		if(tu!=null){
			throw new CmsException("添加的用户已存在，不能重复添加！");
		}
		// 1.2添加用户对象
		userDao.add(user);
		
		for(int i=0;i<roleIds.length;i++){
			addUserRole(user,roleIds[i]);
		}
		
		for(int i=0;i<groupIds.length;i++){
			addUserGroup(user,groupIds[i]);
		}
	}

	public void addUserRole(User user,Integer roleId){
		// 1.检测roleId是否存在
		Role role = roleDao.load(roleId);
		if(role == null){
			throw new CmsException("要添加的用户角色不存在");
		}
		// 2.添加用户角色关联对象
		userDao.addUserRole(user, role);
	}
	
	public void addUserGroup(User user,Integer groupId){
		// 1.检测groupId是否存在
		Group group = groupDao.load(groupId);
		if(group == null){
			throw new CmsException("要添加的用户组不存在");
		}
		// 2.添加用户组关联对象
		userDao.addUserGroup(user, group);
	}
	
	@Override
	public void delete(int id) {
		// TODO 需要进行用户是否有文章的判断
		userDao.deleteUserRole(id);
		userDao.deleteUserGroup(id);
		userDao.delete(id);
	}

	@Override
	public void update(User user, Integer[] roleIds, Integer[] groupIds) {
		// 1.校验user对象是否合法
		User userInDb = userDao.load(user.getId());
		// TODO 需要验证这里的为null判断是否合适,因为load方法返回的是代理对象
		if(userInDb==null){
			throw new CmsException("要修改的用户不存在");
		}
		// 2.更新user对象
		userDao.update(user);
		// 3.1.查询库表中当前用户已经关联的roleIds.
		List<Integer> roleIdsInDb = userDao.listUserRoleIds(user.getId());
		// 3.2.循环roleIds中的每一条记录,如果这条记录在roleIdsDb中没有包含,则应该进行入库,这一点是保证库中不存在的可以入库
		roleIds = roleIds==null?new Integer[]{}:roleIds;
		for(Integer roleId:roleIds){
			if(!roleIdsInDb.contains(roleId)){
				this.addUserRole(userInDb, roleId);
			}
		}
		// 3.3.循环roleIdsInDb中的每一条记录,如果这条记录在roleIds中没有包含,则应该进行删除,这一点是保证库中
		roleIdsInDb = roleIdsInDb==null?new ArrayList<Integer>():roleIdsInDb;
		for(Integer roleIdInDb:roleIdsInDb){
			if(!ArrayUtils.contains(roleIds, roleIdInDb)){
				userDao.deleteUserRole(user.getId(), roleIdInDb);
			}
		}
		// 4.1.查询库表中当前用户已经关联的roleIds.
		List<Integer> groupIdsInDb = userDao.listUserGroupIds(user.getId());
		// 4.2.循环groupIds中的每一条记录,如果这条记录在groupIdsDb中没有包含,则应该进行入库,这一点是保证库中不存在的可以入库
		groupIds = groupIds==null?new Integer[]{}:groupIds;
		for(Integer groupId:groupIds){
			if(!groupIdsInDb.contains(groupId)){
				this.addUserGroup(userInDb, groupId);
			}
		}
		// 4.3.循环groupIdsInDb中的每一条记录,如果这条记录在groupIds中没有包含,则应该进行删除,这一点是保证库中
		groupIdsInDb = groupIdsInDb==null?new ArrayList<Integer>():groupIdsInDb;
		for(Integer groupIdInDb:groupIdsInDb){
			if(!ArrayUtils.contains(groupIds, groupIdInDb)){
				userDao.deleteUserGroup(user.getId(), groupIdInDb);
			}
		}
	}

	@Override
	public void updateStatus(int id) {
		User u = userDao.load(id);
		if(u==null){
			throw new CmsException("修改状态的用户不存在");
		}
		if(u.getStatus()==0){
			u.setStatus(1);
		}else{
			u.setStatus(0);
		}
		userDao.update(u);
	}

	@Override
	public Pager<User> findUser() {
		return userDao.findUser();
	}

	@Override
	public User load(int id) {
		return userDao.load(id);
	}

	@Override
	public List<Role> listUserRoles(int id) {
		return userDao.listUserRoles(id);
	}

	@Override
	public List<Group> listUserGroups(int id) {
		return userDao.listUserGroups(id);
	}

}
