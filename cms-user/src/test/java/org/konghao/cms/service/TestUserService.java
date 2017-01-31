package org.konghao.cms.service;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.konghao.basic.model.Pager;
import org.konghao.cms.dao.IGroupDao;
import org.konghao.cms.dao.IRoleDao;
import org.konghao.cms.dao.IUserDao;
import org.konghao.cms.model.CmsException;
import org.konghao.cms.model.Group;
import org.konghao.cms.model.Role;
import org.konghao.cms.model.RoleType;
import org.konghao.cms.model.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * service方法只要测试方法执行的轨迹即可
 * @author Administrator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-beans.xml")
public class TestUserService {
	@Inject
	private IUserService userService;
	@Inject
	private IUserDao userDao;
	@Inject
	private IRoleDao roleDao;
	@Inject
	private IGroupDao groupDao;
	
	private User baseUser = new User(1,"admin","admin","test","915@qq.com","13120411529",1,new Date());
	
	@Test
	public void testDelete(){
		reset(userDao);
		int uid = 3;
		userDao.deleteUserRole(uid);
		expectLastCall();
		userDao.deleteUserGroup(uid);
		expectLastCall();
		userDao.delete(uid);
		expectLastCall();
		replay(userDao);
		
		userService.delete(3);
		verify(userDao);
	}
	
	@Test
	public void testUpdateStatus(){
		/* ----------------------1.验证正常情况开始--------------------------- */
		reset(userDao);
		int uid = 1;
		expect(userDao.load(uid)).andReturn(baseUser);
		userDao.update(baseUser);
		expectLastCall();
		// 表明在service方法执行过程中,凡是调用到userDao的地方,就按照上面模拟的进行,这里相当于对于userDao的方法进行了一次预演模拟,猜测背后应该是动态织入了模拟代码方法吧。
		replay(userDao);
		
		// 必须保证我所传入的参数跟上面模拟的参数一致,如果是别的参数easyMock框架会报错,因为他不知道该怎么执行模拟了,又不能瞎执行,所以就报错,不让你测试通过。
		// 诀窍,把模拟的过程想象成是在service方法中提前准备预演数据,然后我service方法真正测试调用执行时,假定就是返回上面的值,service方法该怎样执行,就容易理解多了,service的关注点与dao也就做到了真正的隔离
		// 这是一种非常好的测试思想,模拟了dao的操作,意为只要你dao能得到这样的数据,我service就一定能这样执行,测试不依赖于dao了.将来出了问题排查问题也会层次清晰的看出问题所在,非常方便且能说明问题。
		userService.updateStatus(uid);
		// 验证预研的方法是否都按照顺序正常调用了
		verify(userDao);
		// 完成调用后,怎么算对,
		// 1.必须是userDao等依赖其他类的调用过程是正常的,这个easymock上面的模拟可以保证,如果不正常会报错.
		// 2.必须保证userService.updateStatus方法中其他自己方法中的代码(即不依赖其他类的代码),这些代码的验证就要靠junit来验证了,具体到这个测试应该验证下面两点：
		// 2.1.必须保证状态改变了,状态改变了,说明那两行if判断没有写错.
		// 2.2.必须验证load回来的user对象如果是null的对象抛出异常.
		assertEquals(0, baseUser.getStatus());
		/* ----------------------1.验证正常情况结束--------------------------- */
		// 注意所有的情况能在一个方法中验证的就在一个方法中全部进行验证,实在不行再分开写到两个方法中,比如这个验证异常的就必须分开写了。
	}
	
	@Test(expected=CmsException.class)
	public void testUpdateStatusNoUser(){
		/* ----------------------2.验证异常情况开始--------------------------- */
		reset(userDao);
		int uid = 1;
		expect(userDao.load(uid)).andReturn(null);
		userDao.update(baseUser);
		expectLastCall();
		replay(userDao);
		
		userService.updateStatus(uid);
		verify(userDao);
		/* ----------------------2.验证异常情况结束--------------------------- */
	}
	
	@Test
	public void testFindUser(){
		reset(userDao);
		
		expect(userDao.findUser()).andReturn(new Pager<User>());
		replay(userDao);
		userService.findUser();
		
		verify(userDao);
	}
	
	/**
	 * 勾画执行的痕迹
	 */
	@Test
	public void testAdd(){
		reset(userDao,roleDao,groupDao);
		Integer[] rids = new Integer[]{1,2};
		Integer[] gids = new Integer[]{2,3};
		Role r1 = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		Role r2 = new Role(2,"文章发布人员",RoleType.ROLE_PUBLISH);
		Group g1 = new Group(2,"计科系");
		Group g2 = new Group(3,"宣传部");
		
		expect(userDao.loadByUserName("admin")).andReturn(null);
		expect(userDao.add(baseUser)).andReturn(baseUser);
		
		expect(roleDao.load(rids[0])).andReturn(r1);
		userDao.addUserRole(baseUser, r1);
		expectLastCall();
		expect(roleDao.load(rids[1])).andReturn(r2);
		userDao.addUserRole(baseUser, r2);
		expectLastCall();
		
		expect(groupDao.load(gids[0])).andReturn(g1);
		userDao.addUserGroup(baseUser, g1);
		expectLastCall();
		expect(groupDao.load(gids[1])).andReturn(g2);
		userDao.addUserGroup(baseUser, g2);
		expectLastCall();
		
		replay(userDao,roleDao,groupDao);
		
		userService.add(baseUser, rids, gids);
		
		verify(userDao,roleDao,groupDao);
	}
	
	@Test(expected=CmsException.class)
	public void testAddHasUser(){
		reset(userDao);
		Integer[] rids = new Integer[]{1,2};
		Integer[] gids = new Integer[]{2,3};
		
		expect(userDao.loadByUserName("admin")).andReturn(baseUser);
		
		replay(userDao);
		
		userService.add(baseUser, rids, gids);
		
		verify(userDao);
	}
	
	@Test(expected=CmsException.class)
	public void testAddNoRole(){
		reset(userDao,roleDao);
		Integer[] rids = new Integer[]{1,2};
		Integer[] gids = new Integer[]{2,3};
		Role r1 = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		Role r2 = new Role(2,"文章发布人员",RoleType.ROLE_PUBLISH);
		
		expect(userDao.loadByUserName("admin")).andReturn(null);
		expect(userDao.add(baseUser)).andReturn(baseUser);
		
		expect(roleDao.load(rids[0])).andReturn(null);
		userDao.addUserRole(baseUser, r1);
		expectLastCall();
		expect(roleDao.load(rids[1])).andReturn(r2);
		userDao.addUserRole(baseUser, r2);
		expectLastCall();
		
		replay(userDao,roleDao);
		
		userService.add(baseUser, rids, gids);
		
		verify(userDao,roleDao);
	}
	
	@Test(expected=CmsException.class)
	public void testAddNoGroup(){
		reset(userDao,roleDao,groupDao);
		Integer[] rids = new Integer[]{1,2};
		Integer[] gids = new Integer[]{2,3};
		Role r1 = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		Role r2 = new Role(2,"文章发布人员",RoleType.ROLE_PUBLISH);
		Group g1 = new Group(2,"计科系");
		Group g2 = new Group(3,"宣传部");
		
		expect(userDao.loadByUserName("admin")).andReturn(null);
		expect(userDao.add(baseUser)).andReturn(baseUser);
		
		expect(roleDao.load(rids[0])).andReturn(r1);
		userDao.addUserRole(baseUser, r1);
		expectLastCall();
		expect(roleDao.load(rids[1])).andReturn(r2);
		userDao.addUserRole(baseUser, r2);
		expectLastCall();
		
		expect(groupDao.load(gids[0])).andReturn(null);
		userDao.addUserGroup(baseUser, g1);
		expectLastCall();
		expect(groupDao.load(gids[1])).andReturn(g2);
		userDao.addUserGroup(baseUser, g2);
		expectLastCall();
		
		replay(userDao,roleDao,groupDao);
		
		userService.add(baseUser, rids, gids);
		
		verify(userDao,roleDao,groupDao);
	}
	
	@Test
	public void testUpdate(){
		reset(userDao,roleDao,groupDao);
		Integer[] rids = new Integer[]{1,2};
		Integer[] gids = new Integer[]{1,2};
		
		List<Integer> erids = Arrays.asList(2,3);
		List<Integer> egids = Arrays.asList(2,3);
		
		Role r1 = new Role(1,"管理员",RoleType.ROLE_ADMIN);
		Group g1 = new Group(2,"计科系");
		
		expect(userDao.load(baseUser.getId())).andReturn(baseUser);
		userDao.update(baseUser);
		expectLastCall();
		
		expect(userDao.listUserRoleIds(baseUser.getId())).andReturn(erids);
		
		expect(roleDao.load(rids[0])).andReturn(r1);
		userDao.addUserRole(baseUser, r1);
		expectLastCall();
		userDao.deleteUserRole(baseUser.getId(), erids.get(1));
		expectLastCall();
		
		expect(userDao.listUserGroupIds(baseUser.getId())).andReturn(egids);
		
		expect(groupDao.load(gids[0])).andReturn(g1);
		userDao.addUserGroup(baseUser, g1);
		expectLastCall();
		userDao.deleteUserGroup(baseUser.getId(), egids.get(1));
		expectLastCall();
		
		replay(userDao,roleDao,groupDao);
		
		userService.update(baseUser, rids, gids);
		
		verify(userDao,roleDao,groupDao);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
