package org.konghao.basic.dao;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.konghao.basic.model.Pager;
import org.konghao.basic.model.SystemContext;

@SuppressWarnings("unchecked")
public class BaseDao<T> implements IBaseDao<T> {

	private SessionFactory sessionFactory;
	private Class<T> clazz;

	/**
	 * 创建一个Class的对象来获取泛型的class
	 * 
	 * @return
	 */
	public Class<T> getClazz() {
		if (clazz == null) {
			// 获取泛型的class
			clazz = ((Class<T>) (((ParameterizedType) (this.getClass()
					.getGenericSuperclass())).getActualTypeArguments()[0]));
		}
		return clazz;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		// 用spring管理需要使用getCurrentSession(),不能使用openSession();
		return sessionFactory.getCurrentSession();
	}

	@Override
	public T add(T t) {
		this.getSession().save(t);
		return t;
	}

	@Override
	public void update(T t) {
		this.getSession().update(t);
	}

	@Override
	public void delete(int id) {
		this.getSession().delete(this.load(id));
	}

	@Override
	public T load(int id) {
		// 注意这里千万不要大意,千万不要写成getClass()。否则就改变了该语句的初衷,就是粗心写错代码了,而结果会导致Dao实体类可能无法注入.
		return (T) getSession().load(getClazz(), id);
	}

	/**
	 * 根据hql查询一组对象
	 * @param hql
	 * @return
	 */
	public Object queryObject(String hql) {
		return this.queryObject(hql, null, null);
	}
	
	public Object queryObject(String hql, Object arg) {
		return this.queryObject(hql, new Object[]{arg});
	}
	
	public Object queryObject(String hql, Object[] args) {
		return this.queryObject(hql, null, args);
	}
	
	public Object queryObject(String hql, Map<String, Object> alias) {
		return this.queryObject(hql, alias, null);
	}

	public Object queryObject(String hql, Map<String, Object> alias, Object[] args) {
		Query query = this.getSession().createQuery(hql);
		this.setAliasParameter(query, alias);
		this.setArgsParameter(query, args);
		return query.uniqueResult();
	}
	
	/**
	 * 根据hql更新对象
	 * @param hql
	 */
	public void updateByHql(String hql) {
		this.updateByHql(hql, null);
	}
	
	public void updateByHql(String hql, Object arg) {
		this.updateByHql(hql, new Object[]{arg});
	}
	
	public void updateByHql(String hql, Object[] args) {
		Query query = this.getSession().createQuery(hql);
		this.setArgsParameter(query, args);
		query.executeUpdate();
	}

	/**
	 * 不分页列表对象
	 * @param hql 查询列表的hql语句
	 * @return 返回不分页的列表对象集合
	 */
	public List<T> list(String hql) {
		return this.list(hql, null, null);
	}

	public List<T> list(String hql, Object obj) {
		return this.list(hql, new Object[] { obj });
	}
	
	public List<T> list(String hql, Object[] args) {
		return this.list(hql, args, null);
	}

	public List<T> list(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}

	/**
	 * 获取排序hql
	 * 
	 * @param hql
	 * @return
	 */
	private String initSort(String hql) {
		String sort = SystemContext.getSort();
		String order = SystemContext.getOrder();
		if (null != sort && !"".equals(sort)) {
			hql += " order by " + order;
			if (!"desc".equals(sort)) {
				hql += " asc";
			} else {
				hql += " desc";
			}
		}
		return hql;
	}

	/**
	 * Qquey对象中设置alias别名参数
	 * 
	 * @param alias
	 * @param query
	 */
	private void setAliasParameter(Query query, Map<String, Object> alias) {
		if (null != query && null != alias && alias.size() > 0) {
			Set<String> keys = alias.keySet();
			for (String key : keys) {
				Object value = alias.get(key);
				if (value instanceof Collection) {
					// 查询条件是列表
					query.setParameterList(key, (Collection<Object>) value);
				} else {
					query.setParameter(key, value);
				}
			}
		}
	}

	/**
	 * Query对象中设定占位符参数
	 * 
	 * @param query
	 * @param args
	 */
	private void setArgsParameter(Query query, Object[] args) {
		if (null != query && null != args && args.length > 0) {
			int index = 0;
			for (Object arg : args) {
				query.setParameter(index++, arg);
			}
		}
	}

	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		// 处理排序
		hql = initSort(hql);  
		Query query = this.getSession().createQuery(hql);
		// 设定别名
		this.setAliasParameter(query,alias);
		// 设定"?"占位符参数查询
		this.setArgsParameter(query,args);
		return query.list();
	}

	/**
	 * 根据SQL查询对象列表,这里不包含关联对象
	 * @param sql 查询的SQL语句
	 * @param args 查询条件
	 * @param clazz 查询的实体对象
	 * @param hasEntity 该对象是否是hibernate管理的对象,如果不是,需要使用setResultTransform查询
	 * @return
	 */
	public <N extends Object>List<N> listBySql(String sql, Class<?> clazz, boolean hasEntity) {
		return this.listBySql(sql, null, null, clazz, hasEntity);
	}

	public <N extends Object>List<N> listBySql(String sql, Object arg, Class<?> clazz, boolean hasEntity) {
		return this.listBySql(sql, new Object[]{arg}, clazz, hasEntity);
	}
	
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Class<?> clazz, boolean hasEntity) {
		return this.listBySql(sql, args, null, clazz, hasEntity);
	}

	public <N extends Object>List<N> listBySql(String sql, Map<String, Object> alias, Class<?> clazz, boolean hasEntity) {
		return this.listBySql(sql, null, alias, clazz, hasEntity);
	}  
	
	public <N extends Object>List<N> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<?> clazz, boolean hasEntity) {
		sql = this.initSort(sql);
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		this.setAliasParameter(sqlQuery, alias);
		this.setArgsParameter(sqlQuery, args);
		if(hasEntity){
			sqlQuery.addEntity(clazz);
		}else{
			sqlQuery.setResultTransformer(Transformers.aliasToBean(clazz));
		}
		return sqlQuery.list();
	}

	/**
	 * 分页列表对象
	 * @param hql 查询列表的hql语句
	 * @return 返回分页的列表对象集合
	 */
	public Pager<T> find(String hql) {
		return this.find(hql, null, null);
	}
	
	public Pager<T> find(String hql, Object obj) {
		return this.find(hql, new Object[]{obj});
	}

	public Pager<T> find(String hql, Object[] args) {
		return this.find(hql, args, null);
	}
	
	public Pager<T> find(String hql, Map<String, Object> alias) {
		return this.find(hql, null, alias);
	}
	
	public Pager<T> find(String hql, Object[] args, Map<String, Object> alias) {
		// 处理排序
		hql = this.initSort(hql);  
		String counthql = this.getCountHql(hql,true);
		
		Query query = this.getSession().createQuery(hql);
		Query cquery = this.getSession().createQuery(counthql);
		
		// 设定别名
		this.setAliasParameter(query,alias);
		this.setAliasParameter(cquery,alias);
		// 设定"?"占位符参数查询
		this.setArgsParameter(query,args);
		this.setArgsParameter(cquery,args);
		
		// 设定分页参数
		Pager<T> pager = new Pager<T>();
		this.setPager(query,pager);
		List<T> datas = query.list();
		pager.setDatas(datas);
		Long total = (Long)(cquery.uniqueResult());
		pager.setTotal(total);
		
		return pager;
	}
	
	/**
	 * 设定分页信息
	 * @param query
	 * @param pager
	 */
	@SuppressWarnings("rawtypes")
	private void setPager(Query query,Pager pager){
		Integer pageOffset = SystemContext.getPageOffset();
		Integer pageSize = SystemContext.getPageSize();
		if(pageOffset==null || pageOffset<0){
			pageOffset = 0;
		}
		if(pageSize==null || pageSize<0){
			pageSize = 15;
		}
		pager.setOffSet(pageOffset);
		pager.setSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);
	}
	
	/**
	 * 获取分页查询时的总条数
	 * @param hql
	 * @return
	 */
	private String getCountHql(String hql,boolean isHql){
		String hqlSubFrom = hql.substring(hql.indexOf("from"));
		String counthql = "select count(*) " + hqlSubFrom;
		if(isHql){
			counthql.replace("fecth", "");
		}
		return counthql;
	}

	/**
	 * 根据SQL查询分页对象列表,这里不包含关联对象
	 * @param sql 查询的SQL语句
	 * @param args 查询条件
	 * @param clazz 查询的实体对象
	 * @param hasEntity 该对象是否是hibernate管理的对象,如果不是,需要使用setResultTransform查询
	 * @return
	 */
	public <N extends Object>Pager<N> findBySql(String sql, Class<?> clazz, boolean hasEntity) {
		return this.findBySql(sql, null, null, clazz, hasEntity);
	}

	public <N extends Object>Pager<N> findBySql(String sql, Object arg, Class<?> clazz, boolean hasEntity) {
		return this.findBySql(sql, new Object[]{arg}, clazz, hasEntity);
	}
	
	public <N extends Object>Pager<N> findBySql(String sql, Object[] args, Class<?> clazz, boolean hasEntity) {
		return this.findBySql(sql, args, null, clazz, hasEntity);
	}
	
	public <N extends Object>Pager<N> findBySql(String sql, Map<String, Object> alias, Class<?> clazz, boolean hasEntity) {
		return this.findBySql(sql, null, alias, clazz, hasEntity);
	}

	public <N extends Object>Pager<N> findBySql(String sql, Object[] args,
			Map<String, Object> alias, Class<?> clazz, boolean hasEntity) {
		// 获取查询总条目的csql语句
		String csql = this.getCountHql(sql, false);
		
		csql = this.initSort(csql);
		sql = this.initSort(sql);
		
		SQLQuery cQuery = this.getSession().createSQLQuery(csql);
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		this.setAliasParameter(cQuery, alias);
		this.setAliasParameter(query, alias);
		
		this.setArgsParameter(cQuery, args);
		this.setArgsParameter(query, args);
		
		Pager<N> pager = new Pager<N>();
		// 设定分页查询对象pager的属性值,及query对象的分页属性值
		this.setPager(query, pager);
		// hql语句查询一定是hibernate管理的,所以不需要关注clazz,而sql语句查询则需要关注返回结果对象是否是hibernate管理的,而有不同的处理,进而得到正确的查询结果
		if(hasEntity){
			query.addEntity(clazz);
		}else{
			query.setResultTransformer(Transformers.aliasToBean(clazz));
		}
		List<N> datas = query.list();
		Long total = ((BigInteger)cQuery.uniqueResult()).longValue();
		// 继续设定pager属性值
		pager.setDatas(datas);
		pager.setTotal(total);

		return pager;
	}

}
