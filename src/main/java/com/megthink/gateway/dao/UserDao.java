package com.megthink.gateway.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;

@Repository
public class UserDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(UserDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<String> getUserHierarchy(int loggedInUserId) {
		String sql = "SELECT user_id FROM    (SELECT * FROM users ORDER BY created_by_user_id, user_id) users_sorted, "
				+ "(SELECT @pv := :loggedInUserId) initialisation WHERE   FIND_IN_SET(created_by_user_id, @pv) AND LENGTH(@pv := CONCAT(@pv, ',', user_id))";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("loggedInUserId", loggedInUserId);

		List<String> resultList = (List<String>) getNamedParameterJdbcOperations().queryForList(sql, namedParameters,
				String.class);
		_logger.debug(" UserDao.getUserHierarchy() SQL = {} ; Returned List = {}", sql, resultList);
		return resultList;
	}
	
	public int isUserExist(String userName) {
		String sql = "SELECT COUNT(*) FROM users WHERE username = :userName";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("userName", userName);
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		} catch (Exception e) {
			_logger.error("UserDao.isUserExist() - " + e.getMessage(), e);
			return 0;
		}
	}
}