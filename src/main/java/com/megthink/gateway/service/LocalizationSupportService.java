package com.megthink.gateway.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.LocalizationSupport;
import com.megthink.gateway.repository.LocalizationSupportRepository;


@Service("languageSupportService")
public class LocalizationSupportService {

	private LocalizationSupportRepository repository;

	@Autowired
	public LocalizationSupportService(LocalizationSupportRepository repository) {
		this.repository = repository;
	}
	
	public List<LocalizationSupport> findLocalizationSupport() {
        return repository.findAll();
    }
	
	public LocalizationSupport findLocalizationSupportByConstKey(String constKey) {
		return repository.findByConstKey(constKey);
	}
}