package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.megthink.gateway.model.NumberPlan;

public class NumberPlanMapper implements RowMapper<NumberPlan> {

	public NumberPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
		NumberPlan item = new NumberPlan();
		item.setSlno(rs.getString("slno"));
		item.setArea(rs.getString("area"));
		item.setOp_id(rs.getString("op_id"));
		item.setOp_name(rs.getString("op_name"));
		item.setStart_range(rs.getString("start_range"));
		item.setEnd_range(rs.getString("end_range"));
		item.setTechnology(rs.getString("technology"));
		item.setType(rs.getString("type"));
		item.setRouting_info(rs.getString("routing_info"));
		return item;
	}
}