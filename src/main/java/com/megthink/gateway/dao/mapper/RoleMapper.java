package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.Role;

public class RoleMapper implements RowMapper<Role> {

	public Role mapRow(ResultSet rs , int rowNum) throws SQLException{
		Role role = new Role();
		role.setRoleId(rs.getInt("role_id"));
		role.setRoleName(rs.getString("role_name"));
		role.setRoleDescription(rs.getString("role_description"));
		role.setUserId(rs.getInt("user_id"));
		role.setCreatedDateTime(rs.getString("created_date_time").substring(0,19));
		role.setUpdatedDateTime(rs.getString("updated_date_time").substring(0,19));
		return role;
	}
}
