package com.megthink.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.ConfigOjbect;
import com.megthink.gateway.repository.ConfigObjectRepository;

@Service("configOjbectService")
public class ConfigOjbectService {

	private ConfigObjectRepository repository;

	@Autowired
	public ConfigOjbectService(ConfigObjectRepository repository) {
		this.repository = repository;
	}
	
	public ConfigOjbect findConfigOjbectByKey(String key) {
		return repository.findByKey(key);
	}

}