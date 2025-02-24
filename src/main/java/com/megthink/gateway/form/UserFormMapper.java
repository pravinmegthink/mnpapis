package com.megthink.gateway.form;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megthink.gateway.model.User;
import com.megthink.gateway.utils.EncrytedPasswordUtils;

public class UserFormMapper {

	private static final Logger _logger = LoggerFactory.getLogger(UserFormMapper.class);

	public static User mapToForm(UserForm item, String sessionId) {
		User user = new User();
		try {
			user.setFirstName(item.getFirstName());
			user.setLastName(item.getLastName());
			user.setUsername(item.getUsername());
			user.setPassword(EncrytedPasswordUtils.encrytePassword(item.getPassword()));
			user.setContactNumber(item.getContactNumber());
			user.setEmailId(item.getEmailId());
			user.setContactPerson(item.getContactPerson());
			user.setCompanyName(item.getCompanyName());
			user.setStatus(1);
		} catch (Exception e) {
			_logger.info("[sessionId=" + sessionId
					+ "]:Exception occurs while mapping UserFormMapper.mapToForm with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
		}
		return user;
	}
}
