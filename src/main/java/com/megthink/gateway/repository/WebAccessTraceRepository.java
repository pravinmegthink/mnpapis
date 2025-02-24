package com.megthink.gateway.repository;

import com.megthink.gateway.model.WebAccessTrace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("webAccessTraceRepository")
public interface WebAccessTraceRepository extends JpaRepository<WebAccessTrace, Integer> {
  
}
