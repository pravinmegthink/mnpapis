package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.RecoveryDB;

@Repository("recoveryDBRepository")
public interface RecoveryDBRepository extends JpaRepository<RecoveryDB, Integer> {
}
