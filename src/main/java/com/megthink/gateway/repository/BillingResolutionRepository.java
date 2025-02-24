package com.megthink.gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.BillingResolution;

@Repository("billingResolutionRepository")
public interface BillingResolutionRepository extends JpaRepository<BillingResolution, Integer> {

	List<BillingResolution> findByMsisdn(String msisdn);

	List<BillingResolution> findByTransactionId(String transactionId);

	List<BillingResolution> findByMsisdnAndTransactionId(String msisdn, String transactionId);
}
