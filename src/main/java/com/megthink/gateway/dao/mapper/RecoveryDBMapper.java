package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.RecoveryDB;

public class RecoveryDBMapper implements RowMapper<RecoveryDB> {

	public RecoveryDB mapRow(ResultSet rs, int rowNum) throws SQLException {
		RecoveryDB row = new RecoveryDB();
		row.setLsa(rs.getString("lsa"));
		row.setStatus(rs.getInt("status"));
		row.setResult_code(rs.getInt("result_code"));
		row.setRequest_type(rs.getString("request_type"));
		row.setZone(rs.getString("zone"));
		row.setFile_name(rs.getString("file_name"));
		row.setPath(rs.getString("path"));
		return row;
	}
}