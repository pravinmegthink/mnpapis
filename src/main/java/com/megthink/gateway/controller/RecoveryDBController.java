package com.megthink.gateway.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.megthink.gateway.form.RecoveryDBForm;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.RecoveryDBService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RecoveryDBController {

	private static final Logger _logger = LoggerFactory.getLogger(RecoveryDBController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private RecoveryDBService recoveryDBService;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@RequestMapping(value = { "/recovery.html" }, method = RequestMethod.GET)
	public ModelAndView getRecoveryDB(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("recovery.html");
		logTrace.setDesc("Load Recovery db view page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("recoveryForm", new RecoveryDBForm());
		modelAndView.addObject("listoflsa",addStateAbbreviations());
		modelAndView.setViewName("recoverydb");
		return modelAndView;
	}

	@RequestMapping(value = { "/recovery.html" }, method = RequestMethod.POST)
	public ModelAndView getRecoveryDBByFilters(@ModelAttribute RecoveryDBForm recoveryDBForm) {
		_logger.info(
				"RecoveryDBController.getRecoveryDBByFilters(RecoveryDBForm recoveryDBForm)-trying to get recoverydb details");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("recovery.html");
		logTrace.setDesc("Load Recovery db view page after submit filter");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("recoveryForm", recoveryDBForm);
		modelAndView.addObject("listoflsa",addStateAbbreviations());
		modelAndView.setViewName("recoverydb");
		return modelAndView;
	}

	@RequestMapping(value = { "/recoverystatus.html" }, method = RequestMethod.GET)
	public ModelAndView getRecoveryDBStatus(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("recoverystatus.html");
		logTrace.setDesc("Load Recovery db status view page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("status", 0);
		modelAndView.addObject("recoveryForm", new RecoveryDBForm());
		modelAndView.setViewName("recoverydbstatus");
		return modelAndView;
	}

	@RequestMapping(value = { "/recoverystatus.html" }, method = RequestMethod.POST)
	public ModelAndView getRecoveryDBStatusByFilters(@ModelAttribute RecoveryDBForm recoveryDBForm) {
		_logger.info(
				"RecoveryDBController.getRecoveryDBStatusByFilters(RecoveryDBForm recoveryDBForm)-trying to get recoverydb status");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("recoverystatus.html");
		logTrace.setDesc("Load Recovery db status view page after submit filters");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("status", 1);
		String startDate = null;
		String endDate = null;
		if (recoveryDBForm.getDateRange() != null) {
			String[] split = recoveryDBForm.getDateRange().split("\\-");
			if (split.length > 1) {
				startDate = split[0];
				startDate = startDate.replace("/", "-");
				endDate = split[1];
				endDate = endDate.replace("/", "-");
			}
		}
		modelAndView.addObject("recoveryForm", recoveryDBForm);
		modelAndView.addObject("listofrecovery",
				recoveryDBService.getRecoveryStatusByRequestId(recoveryDBForm.getRequestId(), startDate, endDate));
		modelAndView.setViewName("recoverydbstatus");
		return modelAndView;
	}

	public void saveWebAccessTrace(WebAccessTrace logTrace) {
		try {
			webAccessTraceService.saveWebAccessTrace(logTrace);
		} catch (Exception e) {
			_logger.error("Exception occurs while inserting WebAccessTrace" + e);
		}
	}

	private static List<String> addStateAbbreviations() {
		ArrayList<String> stateAbbreviations = new ArrayList<>();
		stateAbbreviations.addAll(Arrays.asList("AS", "BR", "NE", "WB", "KO", "KL", "AP", "KA", "MP", "OR", "TN"));
		return stateAbbreviations;
	}
}