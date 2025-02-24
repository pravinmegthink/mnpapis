package com.megthink.gateway.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.megthink.gateway.dao.PrivilegesDao;
import com.megthink.gateway.form.UserForm;
import com.megthink.gateway.form.UserFormMapper;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.RoleService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UserController {

	private static final Logger _logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@RequestMapping(value = { "/users.html" }, method = RequestMethod.GET)
	public ModelAndView getListOfUsers(HttpServletRequest request) throws ParseException {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info(
				"[sessionId=" + sessionId + "]: UserController.getListOfUsers()-Start processing to get list of users");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("users.html");
		logTrace.setDesc("Load user list view page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofusers", userService.getUserList());
		modelAndView.setViewName("userlist");
		_logger.info(
				"[sessionId=" + sessionId + "]: UserController.getListOfUsers()-End processing to get list of users");
		return modelAndView;
	}

	@RequestMapping(value = { "/createuser.html" }, method = RequestMethod.GET)
	public ModelAndView createUser(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("createuser.html");
		logTrace.setDesc("Load user create page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("userForm", new UserForm());
		modelAndView.addObject("roleList", roleService.findAllRole());
		modelAndView.setViewName("createuser");
		return modelAndView;
	}

	@RequestMapping(value = { "/createuser.html" }, method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> saveUser(@Valid UserForm userForm)
			throws ParseException, IllegalStateException, IOException {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info(
				"[sessionId=" + sessionId + "]: UserController.saveUser()-Start processing to save new user details");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("createuser.html");
		logTrace.setDesc("submit data to create user");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		int isExist = userService.isUserExist(userForm.getUsername());
		if (isExist == 0) {
			Date date = new Date();
			long time = date.getTime();
			Timestamp timestamp = new Timestamp(time);
			User userDetails = UserFormMapper.mapToForm(userForm, sessionId);
			userDetails.setCreatedByUserId(user.getUserId());
			userDetails.setCreatedDateTime(timestamp.toString());
			userDetails.setUpdatedDateTime(timestamp);
			userDetails = userService.saveUser(userDetails);
			if (userDetails.getUserId() != 0) {
				userService.createRoleMappingEntry(userDetails.getUserId(), userForm.getUserType());
				_logger.info(
						"[sessionId=" + sessionId + "]: UserController.saveUser()-save new user details successfully");
				Map<String, String> response = new HashMap<String, String>();
				response.put("totalCount", "Submitted Successfully");
				return ResponseEntity.ok(response);
			} else {
				_logger.info("[sessionId=" + sessionId + "]: UserController.saveUser()-save new user details failed");
				Map<String, String> response = new HashMap<String, String>();
				response.put("totalCount", "Submitted unSuccessfully");
				return ResponseEntity.ok(response);
			}
		} else {
			_logger.info("[sessionId=" + sessionId + "]: UserController.saveUser()-user details already exist");
			Map<String, String> response = new HashMap<String, String>();
			response.put("totalCount", "User Details Already exist");
			return ResponseEntity.ok(response);
		}
	}

	@RequestMapping(value = { "/profile.html" }, method = RequestMethod.GET)
	public ModelAndView getUserProfile(HttpServletRequest request) throws ParseException {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info(
				"[sessionId=" + sessionId + "]: UserController.getUserProfile()-Start processing to get user profile");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("profile.html");
		logTrace.setDesc("Load user profile page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("profileForm", user);
		modelAndView.setViewName("profile");
		_logger.info(
				"[sessionId=" + sessionId + "]: UserController.getUserProfile()-End processing to get user profile");
		return modelAndView;
	}

	public void saveWebAccessTrace(WebAccessTrace logTrace) {
		try {
			webAccessTraceService.saveWebAccessTrace(logTrace);
		} catch (Exception e) {
			_logger.error("Exception occurs while inserting WebAccessTrace" + e);
		}
	}
}