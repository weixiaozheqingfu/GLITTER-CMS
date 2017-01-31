package org.konghao.basic.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.konghao.basic.model.Pager;
import org.konghao.basic.model.SystemContext;
import org.konghao.basic.model.User;
import org.konghao.basic.test.util.AbstractDbUnitTestCase;
import org.konghao.basic.test.util.EntitiesHelper;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
@TestExecutionListeners({DbUnitTestExecutionListener.class,DependencyInjectionTestExecutionListener.class})
public class TestUserDao extends AbstractDbUnitTestCase{

	@Inject
	private SessionFactory sessionFactory;
	
	@Inject
	private IUserDao userDao;
	
	@Before
	public void setUp() throws DataSetException, SQLException, IOException{
		Session s = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));
		this.backupAllTable();
	}
	
	@Test
	public void testLoad() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		User u = userDao.load(1);
		EntitiesHelper.assertUser(u);
	}
	
	@Test(expected=ObjectNotFoundException.class)
	public void testDelete() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		userDao.delete(100);
		User tu = userDao.load(100);
		System.out.println(tu.getUsername());
	}
	
	@Test
	public void testListByArgs() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("desc");
		SystemContext.setOrder("id");
		List<User> expected = userDao.list("from User where id>? and id<?",new Object[]{1,4});
		List<User> actuals = Arrays.asList(new User(3,"admin3"),new User(2,"admin2"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected, actuals);
	}
	
	@Test
	public void testListByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("asc");
		SystemContext.setOrder("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,5,6,7,8,9));
		List<User> expected = userDao.list("from User where id>? and id<? and id in(:ids)",new Object[]{1,5},alias);
		List<User> actuals = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected, actuals);
	}
	
	@Test
	public void testFindByArgs() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("desc");
		SystemContext.setOrder("id");
		SystemContext.setPageSize(2);
		SystemContext.setPageOffset(0);
		Pager<User> expected = userDao.find("from User where id>? and id<?",new Object[]{1,11});
		List<User> actuals = Arrays.asList(new User(10,"admin10"),new User(9,"admin9"));
		assertNotNull(expected);
		assertNotNull(expected.getDatas());
		assertTrue(expected.getOffSet()==0);
		assertTrue(expected.getSize()==2);
		assertTrue(expected.getTotal()==9);
		EntitiesHelper.assertUsers(expected.getDatas(), actuals);
	}
	
	@Test
	public void testFindByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("desc");
		SystemContext.setOrder("id");
		SystemContext.setPageSize(2);
		SystemContext.setPageOffset(0);
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,4,5,6,7,9,11));
		Pager<User> expected = userDao.find("from User where id>? and id<? and id in(:ids)",new Object[]{1,11},alias);
		List<User> actuals = Arrays.asList(new User(9,"admin9"),new User(7,"admin7"));
		assertNotNull(expected);
		assertNotNull(expected.getDatas());
		assertTrue(expected.getOffSet()==0);
		assertTrue(expected.getSize()==2);
		assertTrue(expected.getTotal()==7);
		EntitiesHelper.assertUsers(expected.getDatas(), actuals);
	}
	
	@Test
	public void testListBySqlByArgs() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.removeOrder();
		SystemContext.removeSort();
		List<User> expected = userDao.listUserBySql("select * from t_user where id>? and id<?",new Object[]{1,4},User.class,true);
		List<User> actuals = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected, actuals);
	}
	
	@Test
	public void testListBySqlByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("asc");
		SystemContext.setOrder("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,5,6,7,8,9));
		List<User> expected = userDao.listUserBySql("select * from t_user where id>? and id<? and id in(:ids)",new Object[]{1,5},alias,User.class,true);
		List<User> actuals = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		assertNotNull(expected);
		assertTrue(expected.size()==2);
		EntitiesHelper.assertUsers(expected, actuals);
	}
	
	@Test
	public void testFindBySqlByArgs() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("desc");
		SystemContext.setOrder("id");
		SystemContext.setPageSize(2);
		SystemContext.setPageOffset(0);
		Pager<User> expected = userDao.findUserBySql("select * from t_user where id>? and id<?",new Object[]{1,11},User.class,true);
		List<User> actuals = Arrays.asList(new User(10,"admin10"),new User(9,"admin9"));
		assertNotNull(expected);
		assertNotNull(expected.getDatas());
		assertTrue(expected.getOffSet()==0);
		assertTrue(expected.getSize()==2);
		assertTrue(expected.getTotal()==9);
		EntitiesHelper.assertUsers(expected.getDatas(), actuals);
	}
	
	@Test
	public void testFindBySqlByArgsAndAlias() throws DatabaseUnitException, SQLException{
		IDataSet ds = this.createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		SystemContext.setSort("desc");
		SystemContext.setOrder("id");
		SystemContext.setPageSize(2);
		SystemContext.setPageOffset(0);
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,4,5,6,7,9,11));
		Pager<User> expected = userDao.findUserBySql("select * from t_user where id>? and id<? and id in(:ids)",new Object[]{1,11},alias,User.class,true);
		List<User> actuals = Arrays.asList(new User(9,"admin9"),new User(7,"admin7"));
		assertNotNull(expected);
		assertNotNull(expected.getDatas());
		assertTrue(expected.getOffSet()==0);
		assertTrue(expected.getSize()==2);
		assertTrue(expected.getTotal()==7);
		EntitiesHelper.assertUsers(expected.getDatas(), actuals);
	}
	
	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException{
		SessionHolder holder = (SessionHolder)TransactionSynchronizationManager.getResource(sessionFactory);
		Session s = holder.getSession();
		s.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		this.resumeTable();
	}
}
