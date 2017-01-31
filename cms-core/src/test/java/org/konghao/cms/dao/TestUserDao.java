package org.konghao.cms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.konghao.basic.model.Pager;
import org.konghao.basic.model.SystemContext;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.RoleType;
import org.konghao.cms.model.User;
import org.konghao.cms.model.UserGroup;
import org.konghao.cms.model.UserRole;
import org.konghao.cms.test.util.AbstractDbUnitTestCase;
import org.konghao.cms.test.util.EntitiesHelper;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
public class TestUserDao extends AbstractDbUnitTestCase{
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private IUserDao userDao;
	@Inject
	private IRoleDao roleDao;
	@Inject
	private IGroupDao groupDao;

	@Before
	public void setUp() throws SQLException, IOException, DatabaseUnitException {
		Session s = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));
		this.backupAllTable();
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
	}
	
	@Test
	public void testListUserRoles(){
		List<Role> actuals = Arrays.asList(new Role(2,"文章发布人员",RoleType.ROLE_PUBLISH),new Role(3,"文章审核人员",RoleType.ROLE_AUDIT));
		List<Role> roles = userDao.listUserRoles(2);
		EntitiesHelper.assertRoles(roles, actuals);
	}
	
	@Test
	public void testListUserRoleIds(){
		List<Integer> actuals = Arrays.asList(2,3);
		List<Integer> expected = userDao.listUserRoleIds(2);
		EntitiesHelper.assertObjects(expected, actuals);
	}
	
	@Test
	public void testListUserGroups(){
		List<Group> actuals = Arrays.asList(new Group(1,"财务处"),new Group(3,"宣传部"));
		List<Group> roles = userDao.listUserGroups(3);
		EntitiesHelper.assertGroups(roles, actuals);
	}
	
	@Test
	public void testListUserGroupIds(){
		List<Integer> actuals = Arrays.asList(1,3);
		List<Integer> expected = userDao.listUserGroupIds(3);
		EntitiesHelper.assertObjects(expected, actuals);
	}
	
	@Test
	public void testLoadUserRole(){
		int uid = 1;
		int rid = 1;
		UserRole ur = userDao.loadUserRole(uid, rid);
		User au = new User(1,"admin1","123","admin1","admin1@admin.com","110",1,null);
		Role ar = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		EntitiesHelper.assertUser(ur.getUser(), au);
		EntitiesHelper.assertRole(ur.getRole(), ar);
	}
	
	@Test 
	public void testLoadUserGroup(){
		int uid = 2;
		int gid = 1;
		UserGroup ug = userDao.loadUserGroup(uid, gid);
		User au = new User(2,"admin2","123","admin2","admin2@admin.com","120",1,null);
		Group ag = new Group(1,"财务处");
		EntitiesHelper.assertUser(ug.getUser(), au);
		EntitiesHelper.assertGroup(ug.getGroup(), ag);
	}
	
	@Test
	public void testListRoleUsers(){
		int rid = 2;
		List<User> aus = Arrays.asList(new User(2,"admin2","123","admin2","admin2@admin.com","120",1,null),
									   new User(3,"admin3","123","admin3","admin3@admin.com","130",1,null));
		List<User> eus = userDao.listRoleUsers(rid);
		EntitiesHelper.assertUsers(eus, aus);
	}
	
	@Test
	public void testListRoleUsersByRoleType(){
		List<User> aus = Arrays.asList(new User(2,"admin2","123","admin2","admin2@admin.com","120",1,null),
									   new User(3,"admin3","123","admin3","admin3@admin.com","130",1,null));
		List<User> eus = userDao.listRoleUsers(RoleType.ROLE_PUBLISH);
		EntitiesHelper.assertUsers(eus, aus);
	}
	
	@Test
	public void testListGroupUsers(){
		List<User> aus = Arrays.asList(new User(2,"admin2","123","admin2","admin2@admin.com","120",1,null),
				   new User(3,"admin3","123","admin3","admin3@admin.com","130",1,null));
		List<User> eus = userDao.listGroupUsers(1);
		EntitiesHelper.assertUsers(eus, aus);
	}
	
	@Test	
	public void testAddUserRole(){
		User user = userDao.load(1);
		Role role = roleDao.load(1);
		userDao.addUserRole(user, role);
		UserRole userRole = userDao.loadUserRole(1, 1);
		assertNotNull(userRole);
		assertEquals(userRole.getRole().getId(),1);
		assertEquals(userRole.getUser().getId(),1);
	}
	
	@Test	
	public void testAddUserGroup(){
		User user = userDao.load(1);
		Group group = groupDao.load(1);
		userDao.addUserGroup(user, group);
		UserGroup userGroup = userDao.loadUserGroup(1, 1);
		assertNotNull(userGroup);
		assertEquals(userGroup.getGroup().getId(),1);
		assertEquals(userGroup.getUser().getId(),1); 
	}
	
	@Test
	public void testDeleteUserRole(){
		userDao.deleteUserRole(1);
		UserRole ur = userDao.loadUserRole(1, 1);
		assertNull(ur);
	}
	
	@Test
	public void testDeleteUserGroup(){
		userDao.deleteUserGroup(3);
		List<Group> groups = userDao.listUserGroups(3);
		assertTrue(groups.size()<=0);
	}
	
	@Test
	public void testFindUser(){
		List<User> aus = Arrays.asList(new User(1,"admin1","123","admin1","admin1@admin.com","110",1,null),
									   new User(2,"admin2","123","admin2","admin2@admin.com","120",1,null),
				   					   new User(3,"admin3","123","admin3","admin3@admin.com","130",1,null));
		SystemContext.setPageOffset(0);
		SystemContext.setPageSize(15);
		Pager<User> pager = userDao.findUser();
		assertNotNull(pager);
		assertNotNull(pager.getDatas());
		assertEquals(pager.getTotal(),Long.valueOf(3));
		EntitiesHelper.assertUsers(pager.getDatas(), aus);
	}

	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException {
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session s = holder.getSession(); 
		s.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
//		this.resumeTable();
	}
}
