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
import com.megthink.gateway.service.PortMeTransactionDetailsService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.utils.ReadConfigFile;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderReversalController {

	private static final Logger _logger = LoggerFactory.getLogger(OrderReversalController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionDetailsService;
//	@Autowired
//	private LocalizationSupportService localizationService;

	@RequestMapping(value = { "/reversal.html" }, method = RequestMethod.GET)
	public ModelAndView getOrderReversal(HttpServletRequest request) throws ParseException {
		_logger.info("Trying to getting details of orderReversal");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
//		modelAndView.addObject("localization", localizationService.findLocalizationSupport());
//		Locale locale = LocaleContextHolder.getLocale();
		// String rev_days = ReadConfigFile.getProperties().getProperty("rev_days");
		// int days = Integer.parseInt(rev_days);
//		modelAndView.addObject("locale",
//				localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("reversal", "null");
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("reversal");
		return modelAndView;
	}

	@RequestMapping(value = { "/reversal.html" }, method = RequestMethod.POST)
	public ModelAndView getOrderReversalByFilters(@ModelAttribute PortMeForm filterForm) {
		_logger.info("Trying to getting details of getOrderReversalByFilters");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
//		modelAndView.addObject("localization", localizationService.findLocalizationSupport());
//		Locale locale = LocaleContextHolder.getLocale();
//		modelAndView.addObject("locale",
//				localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String rev_days = ReadConfigFile.getProperties().getProperty("rev_days");
		int days = Integer.parseInt(rev_days);
		modelAndView.addObject("portMeForm", filterForm);
		modelAndView.addObject("reversal",
				portMeTransactionDetailsService.getListOrderReversalDetails(days, filterForm.getMsisdn()));
		modelAndView.setViewName("reversal");
		return modelAndView;
	}
}