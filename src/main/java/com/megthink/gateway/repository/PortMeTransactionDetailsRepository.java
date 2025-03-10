package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.PortMeTransactionDetails;

@Repository("portMeTransactionDetailsRepository")
public interface PortMeTransactionDetailsRepository extends JpaRepository<PortMeTransactionDetails, Integer> {

}
