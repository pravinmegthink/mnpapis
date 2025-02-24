package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.ConfigOjbect;

@Repository("configObjectRepository")
public interface ConfigObjectRepository extends JpaRepository<ConfigOjbect, Integer> {

	ConfigOjbect findByKey(String key);

}
