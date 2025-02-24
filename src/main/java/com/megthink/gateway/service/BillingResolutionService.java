package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.megthink.gateway.model.BillingResolution;
import com.megthink.gateway.repository.BillingResolutionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("billingResolutionService")
public class BillingResolutionService {

	@PersistenceContext
	private EntityManager entityManager;

	private BillingResolutionRepository repository;

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public BillingResolutionService(BillingResolutionRepository repository) {
		this.repository = repository;
	}

	public BillingResolution saveBillingResolution(BillingResolution data) {
		return repository.save(data);
	}

	public List<BillingResolution> findByMsisdn(String msisdn) {
		return repository.findByMsisdn(msisdn);
	}

	public List<BillingResolution> findByTransactionId(String transactionId) {
		return repository.findByTransactionId(transactionId);
	}

	public List<BillingResolution> findByMsisdnAndTransactionId(String msisdn, String transactionId) {
		return repository.findByMsisdnAndTransactionId(msisdn, transactionId);
	}
}