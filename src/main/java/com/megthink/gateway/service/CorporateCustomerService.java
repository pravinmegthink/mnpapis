package com.megthink.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.CorporateCustomer;
import com.megthink.gateway.repository.CorporateCustomerRepository;

@Service("corporateCustomerService")
public class CorporateCustomerService {
	private static final Logger _logger = LoggerFactory.getLogger(CorporateCustomerService.class);
	private final CorporateCustomerRepository corporateCustomerRepository;

	@Autowired
	public CorporateCustomerService(CorporateCustomerRepository corporateCustomerRepository) {
		this.corporateCustomerRepository = corporateCustomerRepository;
	}

	public CorporateCustomer saveCorporateCustomer(CorporateCustomer corporateCustomer) {
		try {
			return corporateCustomerRepository.save(corporateCustomer);
		} catch (DataAccessException ex) {
			_logger.error("Error saving CorporateCustomer: " + ex.getMessage(), ex);
			return null;
		}
	}
}