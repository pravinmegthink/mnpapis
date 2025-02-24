package com.megthink.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.PersonCustomer;
import com.megthink.gateway.repository.PersonCustomerRepository;

import jakarta.transaction.Transactional;

@Service("personCustomerService")
public class PersonCustomerService {

	private PersonCustomerRepository personCustomerRepository;

	@Autowired
	public PersonCustomerService(PersonCustomerRepository personCustomerRepository) {
		this.personCustomerRepository = personCustomerRepository;
	}
	@Transactional
	public PersonCustomer savePersonCustomer(PersonCustomer data) {
		return personCustomerRepository.save(data);
	}
}