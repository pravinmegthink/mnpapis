package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.NumberPlan;

@Repository("numberPlanRepository")
public interface NumberPlanRepository extends JpaRepository<NumberPlan, Integer> {

}
