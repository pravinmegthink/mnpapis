package com.megthink.gateway.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.LocalizationSupport;

@Repository("localizationSupportRepository")
public interface LocalizationSupportRepository extends JpaRepository<LocalizationSupport, Long> {

	LocalizationSupport findByConstKey(String constKey);

}
