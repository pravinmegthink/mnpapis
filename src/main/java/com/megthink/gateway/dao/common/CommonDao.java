package com.megthink.gateway.dao.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@Configuration
public class CommonDao {

	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;

	protected List<?> executeQuery(String sql, RowMapper<?> mapper) {
		return getNamedParameterJdbcOperations().query(sql, mapper);
	}

	protected List<?> executeQuery(String query, SqlParameterSource namedParamters, RowMapper<?> mapper) {
		return getNamedParameterJdbcOperations().query(query, namedParamters, mapper);
	}

	protected int executeUpdate(String query, SqlParameterSource namedParamters) {
		return getNamedParameterJdbcOperations().update(query, namedParamters);
	}

	protected String executeSingleStringColumnQuery(String query, SqlParameterSource namedParamters) {
		return getNamedParameterJdbcOperations().queryForObject(query, namedParamters, String.class);
	}

	protected int executeSingleIntColumnQuery(String query, SqlParameterSource namedParamters) {
		return getNamedParameterJdbcOperations().queryForObject(query, namedParamters, Integer.class);
	}

	// protected int executeUpdate(String query, SqlParameterSource namedParamters,
	// String [] keyName){
	// KeyHolder keyHolder = new GeneratedKeyHolder();
	// getNamedParameterJdbcOperations().update(query,
	// namedParamters,keyHolder,keyName);
	// return keyHolder.getKey().intValue();
	// }

	public NamedParameterJdbcOperations getNamedParameterJdbcOperations() {
		return namedParameterJdbcOperations;
	}

	public void setNamedParameterJdbcOperations(NamedParameterJdbcOperations namedParameterJdbcOperations) {
		this.namedParameterJdbcOperations = namedParameterJdbcOperations;
	}
}
