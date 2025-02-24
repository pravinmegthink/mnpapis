package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.TerminateSimMT;

@Repository("terminateSimMTRepository")
public interface TerminateSimMTRepository extends JpaRepository<TerminateSimMT, Integer> {

}
