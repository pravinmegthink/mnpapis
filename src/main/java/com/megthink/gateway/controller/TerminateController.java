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
import com.megthink.gateway.dao.TerminateSimDao;
import com.megthink.gateway.form.PortMeForm;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.service.TerminateSimService;
import com.megthink.gateway.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TerminateController {

	private static final Logger _logger = LoggerFactory.getLogger(TerminateController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private TerminateSimDao terminateDao;
	@Autowired
	private TerminateSimService terminateService;

//	@Autowired
//	private LocalizationSupportService localizationService;

	@RequestMapping(value = { "/terminate.html" }, method = RequestMethod.GET)
	public ModelAndView getOrderReversal(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
//		modelAndView.addObject("localization", localizationService.findLocalizationSupport());
//		Locale locale = LocaleContextHolder.getLocale();
//		modelAndView.addObject("locale",
//				localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("terminate", "null");
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("terminate");
		return modelAndView;
	}

	@RequestMapping(value = { "/terminate.html" }, method = RequestMethod.POST)
	public ModelAndView getOrderReversalByFilters(@ModelAttribute PortMeForm portMeForm) {
		_logger.info("get terminatation details for sim number - " + portMeForm.getMsisdn());
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
//		modelAndView.addObject("localization", localizationService.findLocalizationSupport());
//		Locale locale = LocaleContextHolder.getLocale();
//		modelAndView.addObject("locale",
//				localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		//modelAndView.addObject("terminate", terminateService.getMSISDNDetails(portMeForm.getMsisdn()));
		modelAndView.addObject("terminate", terminateDao.getMSISDNDetails(portMeForm.getMsisdn()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("terminate");
		return modelAndView;
	}
	
	@RequestMapping(value = { "/nrhconfirmation.html" }, method = RequestMethod.GET)
	public ModelAndView getNRHConfirmation(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
//		modelAndView.addObject("localization", localizationService.findLocalizationSupport());
//		Locale locale = LocaleContextHolder.getLocale();
//		modelAndView.addObject("locale",
//				localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("terminate", terminateDao.getNRHDetails());
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("nrh_confirmation.html");
		return modelAndView;
	}
}