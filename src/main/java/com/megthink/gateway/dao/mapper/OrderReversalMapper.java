package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.megthink.gateway.model.PortMe;

public class OrderReversalMapper implements RowMapper<PortMe> {

	public PortMe mapRow(ResultSet rs, int rowNum) throws SQLException {
		PortMe portMe = new PortMe();
		portMe.setSource(rs.getInt("source"));
		portMe.setBillingUID1(rs.getString("billingUID1"));
		portMe.setInstanceID(rs.getString("instanceid"));
		portMe.setRno(rs.getString("rno"));
		portMe.setDno(rs.getString("dno"));
		portMe.setArea(rs.getString("area"));
		portMe.setRn(rs.getString("rn"));
		portMe.setCompanyCode(rs.getString("company_code"));
		portMe.setService(rs.getString("service"));
		portMe.setDataType(rs.getInt("data_type"));
		portMe.setOrderType(rs.getInt("order_type"));
		portMe.setPartnerID(rs.getString("partnerid"));
		portMe.setLast_area(rs.getString("last_area"));
		portMe.setRemark(rs.getString("remark"));
		portMe.setOriginalCarrier(rs.getString("original_carrier"));
		return portMe;
	}
}