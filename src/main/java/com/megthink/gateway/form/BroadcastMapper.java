package com.megthink.gateway.form;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megthink.gateway.model.BroadcastHistory;
import com.megthink.gateway.model.MasterNP;

public class BroadcastMapper {

	private static final Logger _logger = LoggerFactory.getLogger(BroadcastMapper.class);

	public static BroadcastHistory mapToForm(MasterNP item, String sessionId) {
		BroadcastHistory broadcast = new BroadcastHistory();
		try {
			broadcast.setRemark(item.getRemark());
			broadcast.setMsisdn(item.getMsisdn());
			broadcast.setArea(item.getArea());
			broadcast.setService(item.getService());
			broadcast.setRn(item.getRn());
			broadcast.setPresent_carrier(item.getPresent_carrier());
			broadcast.setCarrier_history(item.getCarrier_history());
			broadcast.setOrginal_carrier(item.getOrginal_carrier());
			broadcast.setActive(item.getActive());
			broadcast.setTransaction_date(item.getTransaction_date());
			broadcast.setAction(item.getAction());
			broadcast.setHistory_area(item.getHistory_area());
			broadcast.setOriginal_area(item.getOriginal_area());
			broadcast.setFirst_trans_date(item.getFirst_trans_date());
			broadcast.setRe_trans_date(item.getRe_trans_date());
		} catch (Exception e) {
			_logger.info("[sessionId=" + sessionId
					+ "]:Exception occurs while mapping masterNp into Broadcast  with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
		}
		return broadcast;

	}
}
