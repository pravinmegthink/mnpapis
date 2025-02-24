package com.megthink.gateway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.megthink.gateway.model.SubscriberArrType;

public class SubscriberMapper implements RowMapper<SubscriberArrType> {

	public SubscriberArrType mapRow(ResultSet rs, int rowNum) throws SQLException {
		SubscriberArrType subscriber = new SubscriberArrType();
		subscriber.setId(rs.getInt("id"));
		subscriber.setMsisdn(rs.getString("msisdn"));
		subscriber.setHlr(rs.getString("hlr"));
		subscriber.setDummyMSISDN(rs.getString("dummymsisdn"));
		subscriber.setImsi("imsi");
		subscriber.setSim("sim");
		subscriber.setResultCode(rs.getInt("result_code"));
		return subscriber;
	}
}