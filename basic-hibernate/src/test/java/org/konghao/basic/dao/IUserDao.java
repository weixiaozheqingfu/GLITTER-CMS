package org.konghao.basic.dao;

import java.util.List;
import java.util.Map;

import org.konghao.basic.dao.IBaseDao;
import org.konghao.basic.model.Pager;
import org.konghao.basic.model.User;

/**
 * 实现了IBaseDao也就有了基础的接口方法,再此基础上可以根据需要再添加自己定制的接口方法定义,这样的设计模式甚好。
 * @author Administrator
 *
 */
public interface IUserDao extends IBaseDao<User> {

	List<User> list(String string, Object[] objects);

	List<User> list(String string, Object[] objects, Map<String, Object> alias);

	Pager<User> find(String string, Object[] objects);

	Pager<User> find(String string, Object[] objects, Map<String, Object> alias);

	List<User> listUserBySql(String string, Object[] objects, Class<User> class1, boolean b);

	List<User> listUserBySql(String string, Object[] objects,Map<String, Object> alias, Class<User> class1, boolean b);

	Pager<User> findUserBySql(String string, Object[] objects, Class<User> class1, boolean b);

	Pager<User> findUserBySql(String string, Object[] objects, Map<String, Object> alias, Class<User> class1, boolean b);

}
