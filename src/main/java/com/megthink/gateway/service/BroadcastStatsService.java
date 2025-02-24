package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.BroadcastStats;
import com.megthink.gateway.repository.BroadcastStatsRepository;

@Service("broadcastStatsService")
public class BroadcastStatsService {

	private BroadcastStatsRepository statsRepository;

	@Autowired
	public BroadcastStatsService(BroadcastStatsRepository statsRepository) {
		this.statsRepository = statsRepository;
	}

	public BroadcastStats saveBroadcastStats(BroadcastStats item) {
		Date date = new Date();
		long time = date.getTime();
		Timestamp timestamp = new Timestamp(time);
		item.setCreated_date(timestamp);
		// return statsRepository.save(item);
		return new BroadcastStats();
	}
}