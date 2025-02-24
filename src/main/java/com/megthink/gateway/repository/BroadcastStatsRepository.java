package com.megthink.gateway.repository;

import com.megthink.gateway.model.BroadcastStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("broadcastStatsRepository")
public interface BroadcastStatsRepository extends JpaRepository<BroadcastStats, Integer> {
  
}
