package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.TerminateSim;

public class TerminateSIMMapper implements RowMapper<TerminateSim> {

	public TerminateSim mapRow(ResultSet rs, int rowNum) throws SQLException {
		TerminateSim row = new TerminateSim();
		row.setRequestId(rs.getString("request_id"));
		row.setArea(rs.getString("area"));
		row.setOriginalCarrier(rs.getString("orginal_carrier"));
		row.setDno(rs.getString("dno"));
		row.setMsisdn(rs.getString("msisdn"));
		return row;
	}
}