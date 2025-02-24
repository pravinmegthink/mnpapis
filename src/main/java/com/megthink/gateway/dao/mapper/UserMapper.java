package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.User;

public class UserMapper implements RowMapper<User> {

	public User mapRow(ResultSet rs , int rowNum) throws SQLException{
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setRoleList(rs.getString("roleList"));
		user.setContactNumber(rs.getString("contact_number"));
		user.setEmailId(rs.getString("email_id"));
		user.setContactPerson(rs.getString("contact_person"));
		user.setStatus(rs.getInt("status"));
		user.setCompanyName(rs.getString("company_name"));
		user.setCreatedDateTime(rs.getString("created_date_time").substring(0,19));
		user.setUpdatedDateTime(rs.getTimestamp("updated_date_time"));
		return user;
	}
}