package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.Privileges;


public class PrivilegesMapper implements RowMapper<Privileges> {

	public Privileges mapRow(ResultSet rs , int rowNum) throws SQLException{
		Privileges privileges = new Privileges();
		privileges.setPrivilegeId(rs.getInt("privilege_id"));
		privileges.setPrivilegeName(rs.getString("privilege_name"));
		return privileges;
	}
}
