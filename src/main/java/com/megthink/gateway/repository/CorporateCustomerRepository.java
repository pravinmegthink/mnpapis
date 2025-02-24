package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.CorporateCustomer;

@Repository("corporateCustomerRepository")
public interface CorporateCustomerRepository extends JpaRepository<CorporateCustomer, Integer> {

}
