package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.TerminateSimMT;


public class TerminateSimMTMapper implements RowMapper<TerminateSimMT> {

	public TerminateSimMT mapRow(ResultSet rs, int rowNum) throws SQLException {
		TerminateSimMT mapper = new TerminateSimMT();
		mapper.setSubscriberNumber(rs.getString("msisdn"));
		mapper.setStatus(rs.getInt("status"));
		mapper.setResultCode(rs.getInt("result_code"));
		
		return mapper;
	}
}