package com.megthink.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.CustomerData;
import com.megthink.gateway.repository.CustomerDataRepository;

import jakarta.transaction.Transactional;

@Service("customerDataService")
public class CustomerDataService {

	private CustomerDataRepository customerDataRepository;

	@Autowired
	public CustomerDataService(CustomerDataRepository customerDataRepository) {
		this.customerDataRepository = customerDataRepository;
	}
	@Transactional
	public CustomerData saveCustomerData(CustomerData data) {
		return customerDataRepository.save(data);
	}
}