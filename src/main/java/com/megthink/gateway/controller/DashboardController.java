package com.megthink.gateway.controller;

import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.megthink.gateway.dao.PrivilegesDao;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@RequestMapping(value = { "/dashboard.html" }, method = RequestMethod.GET)
	public ModelAndView getDashboard(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.setViewName("dashboard");
		try {
			WebAccessTrace logTrace = new WebAccessTrace();
			logTrace.setAction("dashboard.html");
			logTrace.setDesc("View Dashboard Page");
			logTrace.setUser_id(user.getUserId());
			webAccessTraceService.saveWebAccessTrace(logTrace);
		} catch (Exception e) {

		}
		return modelAndView;
	}
}