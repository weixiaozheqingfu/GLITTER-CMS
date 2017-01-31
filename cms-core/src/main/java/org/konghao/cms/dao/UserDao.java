package org.konghao.cms.dao;

import java.util.List;

import org.konghao.basic.dao.BaseDao;
import org.konghao.basic.model.Pager;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.RoleType;
import org.konghao.cms.model.User;
import org.konghao.cms.model.UserGroup;
import org.konghao.cms.model.UserRole;
import org.springframework.stereotype.Repository;

@Repository("userDao")
@SuppressWarnings("unchecked")
public class UserDao extends BaseDao<User> implements IUserDao{
	@Override
	public List<Role> listUserRoles(int userId) {
		String hql = "select ur.role from UserRole ur where ur.user.id = ?";
		return this.getSession().createQuery(hql).setParameter(0, userId).list();
	}

	@Override
	public List<Integer> listUserRoleIds(int userId) {
		String hql = "select ur.role.id from UserRole ur where ur.user.id = ?";
		return this.getSession().createQuery(hql).setParameter(0, userId).list();
	}

	@Override
	public List<Group> listUserGroups(int userId) {
		String hql = "select ug.group from UserGroup ug where ug.user.id = ?";
		return this.getSession().createQuery(hql).setParameter(0, userId).list();
	}

	@Override
	public List<Integer> listUserGroupIds(int userId) {
		String hql = "select ug.group.id from UserGroup ug where ug.user.id = ?";
		return this.getSession().createQuery(hql).setParameter(0, userId).list();
	}

	@Override
	public UserRole loadUserRole(int userId, int roleId) {
		String hql = "select ur from UserRole ur left join fetch ur.user u left join fetch ur.role r where u.id = ? and r.id = ?";
		return (UserRole)this.getSession().createQuery(hql).setParameter(0, userId).setParameter(1, roleId).uniqueResult();
	}

	@Override
	public UserGroup loadUserGroup(int userId, int groupId) {
		String hql = "select ug from UserGroup ug left join fetch ug.user u left join fetch ug.group g where u.id = ? and g.id = ?";
		return (UserGroup)this.getSession().createQuery(hql).setParameter(0, userId).setParameter(1, groupId).uniqueResult();
	}

	@Override
	public User loadByUserName(String userName) {
		String hql = "select u from User u where u.userName = ?";
		return (User)this.queryObject(hql, userName);
	}

	@Override
	public List<User> listRoleUsers(int roleId) {
		String hql = "select ur.user from UserRole ur where ur.role.id = ?";
		return this.list(hql, roleId);
	}
 
	@Override
	public List<User> listRoleUsers(RoleType roleType) {
		String hql = "select ur.user from UserRole ur where ur.role.roleType = ?";
		return this.list(hql,roleType);
	}

	@Override
	public List<User> listGroupUsers(int groupId) {
		String hql = "select ug.user from UserGroup ug where ug.group.id = ?";
		return this.list(hql,groupId);
	}

	@Override
	public void addUserRole(User user, Role role) {
		UserRole userRole = this.loadUserRole(user.getId(), role.getId());
		if(userRole==null){
			userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRole(role);
			this.getSession().save(userRole);
		}
	}

	@Override
	public void addUserGroup(User user, Group group) {
		UserGroup userGroup = this.loadUserGroup(user.getId(), group.getId());
		if(userGroup==null){
			userGroup = new UserGroup();
			userGroup.setUser(user);
			userGroup.setGroup(group);
			this.getSession().save(userGroup);	
		}
	}

	@Override
	public void deleteUserRole(int uid) {
		String hql = "delete UserRole ur where ur.user.id = ?";
		this.updateByHql(hql, uid);
	}

	@Override
	public void deleteUserGroup(int uid) {
		String hql = "delete UserGroup ug where ug.user.id = ?";
		this.updateByHql(hql, uid);
	}
	
	@Override
	public void deleteUserRole(Integer uid, Integer roleId) {
		String hql = "delete from userRole ur where ur.user.id = ? and ur.roleId = ?";
		this.updateByHql(hql, new Object[]{uid,roleId});
	}

	@Override
	public void deleteUserGroup(Integer uid, Integer groupId) {
		String hql = "delete from groupId ug where ug.user.id = ? and ug.groupId = ?";
		this.updateByHql(hql, new Object[]{uid,groupId});
	}

	@Override
	public Pager<User> findUser() {
		return this.find("from User");
	}

}
