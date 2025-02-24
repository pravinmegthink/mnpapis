package com.megthink.gateway.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.Role;
import com.megthink.gateway.repository.RoleRepository;

@Service("roleService")
public class RoleService {

	private RoleRepository roleRepository;

	@Autowired
	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public List<Role> findAllRole() {
		return roleRepository.findAll();
	}

}