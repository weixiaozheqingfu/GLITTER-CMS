package org.konghao.cms.dao;

import org.konghao.basic.dao.BaseDao;
import org.konghao.cms.model.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class RoleDao extends BaseDao<Role> implements IRoleDao {

}
