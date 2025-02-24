package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.CustomerData;

@Repository("customerDataRepository")
public interface CustomerDataRepository extends JpaRepository<CustomerData, Integer> {

}
