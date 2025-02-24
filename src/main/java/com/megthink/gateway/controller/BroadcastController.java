package com.megthink.gateway.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.megthink.gateway.dao.PrivilegesDao;
import com.megthink.gateway.form.PortMeForm;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.BroadcastHistoryService;
import com.megthink.gateway.service.MasterNPService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BroadcastController {

	private static final Logger _logger = LoggerFactory.getLogger(BroadcastController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private BroadcastHistoryService broadcastHistoryService;
	@Autowired
	private MasterNPService masterNPService;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@RequestMapping(value = { "/broadcast.html" }, method = RequestMethod.GET)
	public ModelAndView getBroadcast(HttpServletRequest request) throws ParseException {
		_logger.info("getting port activation results...");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofbroadcast", "null");
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("broadcastdb");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("broadcast.html");
		logTrace.setDesc("Load Braodcast Page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/broadcast.html" }, method = RequestMethod.POST)
	public ModelAndView getBroadcastByFilter(@ModelAttribute PortMeForm filterForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		String startDate = null;
		String endDate = null;
		if (filterForm.getDateRange() != null) {
			String[] split = filterForm.getDateRange().split("\\-");
			if (split.length > 1) {
				startDate = split[0];
				startDate = startDate.replace("/", "-");
				endDate = split[1];
				endDate = endDate.replace("/", "-");
			}
		}
		modelAndView.addObject("listofbroadcast", broadcastHistoryService.getBroadcastHistory(filterForm.getMsisdn(),
				user.getUserId(), startDate, endDate));
		modelAndView.addObject("portMeForm", filterForm);
		modelAndView.setViewName("broadcastdb");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("broadcast.html");
		logTrace.setDesc("Loast Braodcast Data after submit filter");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/broadcaststatus.html" }, method = RequestMethod.GET)
	public ModelAndView getTransactionStatus(HttpServletRequest request) throws ParseException {
		_logger.info("getting port activation results...");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofitem", "null");
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("broadcast_status");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("broadcaststatus.html");
		logTrace.setDesc("Load Braodcast status page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/broadcaststatus.html" }, method = RequestMethod.POST)
	public ModelAndView getTransactionStatusByFilter(@ModelAttribute PortMeForm portMeForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		String startDate = null;
		String endDate = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				startDate = split[0];
				startDate = startDate.replace("/", "-");
				endDate = split[1];
				endDate = endDate.replace("/", "-");
			}
		}
		modelAndView.addObject("listofitem",
				masterNPService.getTransactionStatus(startDate, endDate, portMeForm.getMsisdn(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("broadcast_status");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("broadcaststatus.html");
		logTrace.setDesc("Load Braodcast status page after submit filter");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
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