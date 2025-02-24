package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.repository.WebAccessTraceRepository;

@Service("webAccessTraceService")
public class WebAccessTraceService {

	private WebAccessTraceRepository wATRepository;

	@Autowired
	public WebAccessTraceService(WebAccessTraceRepository wATRepository) {
		this.wATRepository = wATRepository;
	}

	public WebAccessTrace saveWebAccessTrace(WebAccessTrace item) {
		Date date = new Date();
		long time = date.getTime();
		Timestamp timestamp = new Timestamp(time);
		item.setCreated_date(timestamp);
		return wATRepository.save(item);
	}
}