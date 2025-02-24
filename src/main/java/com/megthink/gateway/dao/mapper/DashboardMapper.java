package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.megthink.gateway.model.DashboardChart;


public class DashboardMapper implements RowMapper<DashboardChart> {

	public DashboardChart mapRow(ResultSet rs , int rowNum) throws SQLException{
		DashboardChart dashboard = new DashboardChart();
		dashboard.setCount(rs.getInt("count"));
		dashboard.setDates(rs.getString("date"));
		return dashboard;
	}
}
