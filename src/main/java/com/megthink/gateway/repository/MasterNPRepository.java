package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.MasterNP;

@Repository("masterNPRepository")
public interface MasterNPRepository extends JpaRepository<MasterNP, Integer> {

	MasterNP findByMsisdn(String msisdn);

}
