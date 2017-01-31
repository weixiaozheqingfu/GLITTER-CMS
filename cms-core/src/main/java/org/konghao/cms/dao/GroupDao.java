package org.konghao.cms.dao;

import org.konghao.basic.dao.BaseDao;
import org.konghao.cms.model.Group;
import org.springframework.stereotype.Repository;

@Repository("groupDao")
public class GroupDao extends BaseDao<Group> implements IGroupDao {

}
