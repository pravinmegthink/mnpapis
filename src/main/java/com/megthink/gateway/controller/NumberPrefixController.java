package com.megthink.gateway.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.megthink.gateway.dao.NumberPlanDao;
import com.megthink.gateway.dao.PrivilegesDao;
import com.megthink.gateway.form.NumberPlanForm;
import com.megthink.gateway.model.NumberPlan;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.NumberPlanService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class NumberPrefixController {

	private static final Logger _logger = LoggerFactory.getLogger(NumberPrefixController.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private NumberPlanDao numberPlanDao;
	@Autowired
	private NumberPlanService numberPlanService;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@RequestMapping(value = { "/prefix.html" }, method = RequestMethod.GET)
	public ModelAndView getNumberPrefix(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: NumberPrefixController.getNumberPrefix() - process access by username : [" + user.getUsername()
				+ "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("numberPlanForm", new NumberPlanForm());
		modelAndView.addObject("numberPlan", numberPlanDao.getOperatorInformation());
		List<NumberPlan> list = new ArrayList<NumberPlan>();
		modelAndView.addObject("listOfNumberPlan", list);
		modelAndView.setViewName("numberprefix");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("prefix.html");
		logTrace.setDesc("Load Number Prefix View Page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/prefix.html" }, method = RequestMethod.POST)
	public ModelAndView getNumberPrefix(@ModelAttribute NumberPlanForm numberPlan) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: NumberPrefixController.getNumberPrefix() - get details by filters access by username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("numberPlanForm", numberPlan);
		modelAndView.addObject("numberPlan", numberPlanDao.getOperatorInformation());
		List<NumberPlan> list = numberPlanService.getOperatorInformationByOpId(numberPlan.getOp_id());
		modelAndView.addObject("listOfNumberPlan", list);
		modelAndView.setViewName("numberprefix");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("prefix.html");
		logTrace.setDesc("Load Number Prefix View Page after submit filter");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/createprefix.html" }, method = RequestMethod.GET)
	public ModelAndView createNumberPrifix(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("numberPlanForm", new NumberPlanForm());
		modelAndView.addObject("numberPlan", numberPlanDao.getOperatorInformation());
		modelAndView.setViewName("createnumberprefix");
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("createprefix.html");
		logTrace.setDesc("Load create Number Prefix Page");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		return modelAndView;
	}

	@RequestMapping(value = { "/createprefix" }, method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> saveNumberPrifix(@Valid NumberPlanForm numberPlanForm, BindingResult result)
			throws ParseException, IllegalStateException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("createprefix");
		logTrace.setDesc("submit data to create Number Prefix");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		Integer isExist = numberPlanService.isExist(numberPlanForm.getStart_range(), numberPlanForm.getEnd_range());
		if (isExist == null || isExist == 0) {
			String sessionId = Long.toString(System.currentTimeMillis());
			_logger.info("[sessionId=" + sessionId
					+ "]: NumberPrefixController.saveNumberPrifix() - create Number Prefix by username : ["
					+ user.getUsername() + "]");
			numberPlanForm.setChanged_by(user.getUsername());
			if (numberPlanForm.getSlno().equals("")) {
				Random random = new Random();
				int x = random.nextInt(900) + 100;
				String slno = Integer.toString(x);
				numberPlanForm.setSlno(slno);
				numberPlanForm.setUpdate_date(timestamp);
				NumberPlan numberPlan = mapToEntity(numberPlanForm);
				numberPlanService.save(numberPlan);
			} else {
				NumberPlan numberPlan = mapToEntity(numberPlanForm);
				numberPlanService.updateNumberPlan(numberPlan);
			}
			Map<String, String> response = new HashMap<String, String>();
			response.put("totalCount", "Submitted Successfully");
			return ResponseEntity.ok(response);
		} else {
			Map<String, String> response = new HashMap<String, String>();
			response.put("totalCount", "Plan number already exists");
			return ResponseEntity.ok(response);
		}
	}

	@RequestMapping(value = "/getoperatordetails", method = RequestMethod.GET, produces = { "application/xml",
			"application/json" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody NumberPlan getNumberPlanDetailByOpId(
			@RequestParam(value = "op_id", required = false) String op_id) {
		List<NumberPlan> list = numberPlanService.getOperatorInformationByOpId(op_id);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return new NumberPlan();
		}
	}

	public NumberPlan mapToEntity(NumberPlanForm form) {
		NumberPlan numberPlan = new NumberPlan();
		numberPlan.setSlno(form.getSlno());
		numberPlan.setArea(form.getArea());
		numberPlan.setOp_id(form.getOp_id());
		numberPlan.setStart_range(form.getStart_range());
		numberPlan.setEnd_range(form.getEnd_range());
		numberPlan.setTechnology(form.getTechnology());
		numberPlan.setType(form.getType());
		numberPlan.setUpdate_date(form.getUpdate_date());
		numberPlan.setRouting_info(form.getRouting_info());
		numberPlan.setChanged_by(form.getChanged_by());
		return numberPlan;
	}

	public void saveWebAccessTrace(WebAccessTrace logTrace) {
		try {
			webAccessTraceService.saveWebAccessTrace(logTrace);
		} catch (Exception e) {
			_logger.error("Exception occurs while inserting WebAccessTrace" + e);
		}
	}
}