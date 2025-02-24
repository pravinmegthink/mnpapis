package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.megthink.gateway.model.PortMeDetails;

public class PortMeDetailsMapper implements RowMapper<PortMeDetails> {

	public PortMeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		PortMeDetails portMe = new PortMeDetails();
		portMe.setPortId(rs.getInt("port_id"));
		portMe.setRequestId(rs.getString("request_id"));
		portMe.setArea(rs.getString("area"));
		portMe.setDno(rs.getString("dno"));
		portMe.setMsisdn(rs.getString("msisdn"));
		portMe.setService(rs.getString("service"));
		portMe.setStatusDesc(rs.getString("status"));
		portMe.setImsi(rs.getString("imsi"));
		portMe.setHlr(rs.getString("hlr"));
		portMe.setSim(rs.getString("sim"));
		portMe.setCreatedDate(rs.getString("created_date_time"));
		portMe.setUpdatedDate(rs.getString("updated_date_time"));
		return portMe;
	}
}