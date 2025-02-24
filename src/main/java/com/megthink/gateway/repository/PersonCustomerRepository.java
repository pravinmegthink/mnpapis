package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.PersonCustomer;

@Repository("personCustomerRepository")
public interface PersonCustomerRepository extends JpaRepository<PersonCustomer, Integer> {

}
