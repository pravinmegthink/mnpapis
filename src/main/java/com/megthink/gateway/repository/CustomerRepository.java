package com.megthink.gateway.repository;

import org.springframework.data.repository.CrudRepository;

import com.megthink.gateway.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
