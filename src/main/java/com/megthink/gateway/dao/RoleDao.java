package com.megthink.gateway.dao;

import java.util.List;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.RoleMapper;
import com.megthink.gateway.model.Role;

@Repository
public class RoleDao extends CommonDao {

//	private static final Logger _logger = LoggerFactory.getLogger(RoleDao.class);

	@SuppressWarnings("unchecked")
	public List<Role> getRoleListAvailableRoleForUser() {
		String sql = "SELECT r.role_id,r.role_name,r.role_description,r.user_id,r.created_date_time,r.updated_date_time "
				+ "FROM role r INNER JOIN role_privileges rp on rp.role_id = r.role_id WHERE rp.privilege_id = " + 1;
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Role> list = (List<Role>) executeQuery(sql, namedParameters, new RoleMapper());
		return list;
	}
	
	public List<Role> getRoleListAvailableRoleForDashboard() {
		String sql = "SELECT r.role_id,r.role_name,r.role_description,r.user_id,r.created_date_time,r.updated_date_time " + 
				"FROM role r INNER JOIN role_privileges rp on rp.role_id = r.role_id WHERE rp.privilege_id = "+1;
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Role> list = (List<Role>)executeQuery(sql, namedParameters, new RoleMapper());
		return list;
	}
}
