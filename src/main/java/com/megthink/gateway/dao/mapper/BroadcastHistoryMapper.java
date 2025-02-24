package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.BroadcastHistory;

public class BroadcastHistoryMapper implements RowMapper<BroadcastHistory> {

	public BroadcastHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
		BroadcastHistory item = new BroadcastHistory();
		item.setArea(rs.getString("area"));
		item.setMsisdn(rs.getString("msisdn"));
		item.setService(rs.getString("service"));
		item.setHlr(rs.getString("hlr"));
		item.setRn(rs.getString("rn"));
		item.setFirst_trans_date(rs.getTimestamp("first_trans_date"));
		item.setCarrier_history(rs.getString("carrier_history"));
		item.setPresent_carrier(rs.getString("present_carrier"));
		item.setActive(rs.getString("active"));
		return item;
	}
}