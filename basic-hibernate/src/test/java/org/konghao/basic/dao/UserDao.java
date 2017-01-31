package org.konghao.basic.dao;

import java.util.List;
import java.util.Map;

import org.konghao.basic.dao.BaseDao;
import org.konghao.basic.model.Pager;
import org.konghao.basic.model.User;
import org.springframework.stereotype.Repository;

/**
 * 实现了IUserDao接口,继承了BaseDao类但不覆盖其中的方法,这样在调用IUserDao接口中base的方法时,就可以直接调用BaseDao中的方法,如此甚好。
 * 这样基础的方法所有的dao都用统一的,定制的接口方法和实现用自己的,全部搞定。
 * @author Administrator
 *
 */
@Repository("userDao")
public class UserDao extends BaseDao<User> implements IUserDao {

	public List<User> listUserBySql(String sql, Object[] args, Class<User> clazz, boolean hasEntity) {
		return super.listBySql(sql, args, clazz, hasEntity);
	}

	public List<User> listUserBySql(String sql, Object[] args, Map<String, Object> alias, Class<User> clazz, boolean hasEntity) {
		return super.listBySql(sql, args, alias, clazz, hasEntity);
	}

	public Pager<User> findUserBySql(String sql, Object[] args, Class<User> clazz, boolean hasEntity) {
		return super.findBySql(sql, args, clazz, hasEntity);
	}

	public Pager<User> findUserBySql(String sql, Object[] args, Map<String, Object> alias, Class<User> clazz, boolean hasEntity) {
		return super.findBySql(sql, args, alias, clazz, hasEntity);
	}

}
