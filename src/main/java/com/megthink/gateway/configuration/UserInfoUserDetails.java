package com.megthink.gateway.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.megthink.gateway.model.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoUserDetails implements UserDetails {

	private static final long serialVersionUID = 3395977161509645321L;
	private String name;
	private String password;
	private List<GrantedAuthority> authorities;

	public UserInfoUserDetails(User userInfo) {
		name = userInfo.getUsername();
		password = userInfo.getPassword();
		authorities = Arrays.stream("Admin".split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	
	public String getPassword() {
		return password;
	}

	
	public String getUsername() {
		return name;
	}

	
	public boolean isAccountNonExpired() {
		return true;
	}

	
	public boolean isAccountNonLocked() {
		return true;
	}

	
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
	public boolean isEnabled() {
		return true;
	}
}