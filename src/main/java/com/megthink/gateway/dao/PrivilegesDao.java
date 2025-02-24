package com.megthink.gateway.dao;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.PrivilegesMapper;
import com.megthink.gateway.model.Privileges;

@Repository
public class PrivilegesDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(PrivilegesDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Privileges> getPrivilegesListByRole(int roleId) {
		String sql = "SELECT p.privilege_id,p.privilege_name FROM privileges p INNER JOIN role_privileges rp ON p.privilege_id=rp.privilege_id "
				+ "WHERE rp.role_id=:role_id";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("role_id", roleId);
		List<Privileges> list = namedParameterJdbcTemplate.query(sql, namedParameters, new PrivilegesMapper());
		_logger.debug(" PrivilegesDao.getPrivilegesList() SQL = {} ; Returned list = {}", sql, list);
		return list;
	}

	public List<Privileges> getPrivilegesListByUser(int userId) {
		String sql = "SELECT p.privilege_id,p.privilege_name FROM privileges p INNER JOIN role_privileges rp ON p.privilege_id=rp.privilege_id "
				+ "INNER JOIN user_role ur on ur.role_id = rp.role_id WHERE ur.user_id=:userId";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userId", userId);
		List<Privileges> list = namedParameterJdbcTemplate.query(sql, namedParameters, new PrivilegesMapper());
		_logger.debug(" PrivilegesDao.getPrivilegesListByUser() SQL = {} ; Returned list = {}", sql, list);
		return list;
	}

	public List<Privileges> getAllPrivilegesList() {
		String sql = "SELECT privilege_id, privilege_name FROM privileges";

		try {
			return namedParameterJdbcTemplate.query(sql, new PrivilegesMapper());
		} catch (Exception e) {
			_logger.error("PrivilegesRepository.getAllPrivilegesList() - " + e.getMessage(), e);
			return null; // Handle the error as needed
		}
	}
}
