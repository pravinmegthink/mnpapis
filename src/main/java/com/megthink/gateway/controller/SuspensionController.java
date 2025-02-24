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
import com.megthink.gateway.form.SuspensionForm;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.service.BillingResolutionService;
import com.megthink.gateway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SuspensionController {

	private static final Logger _logger = LoggerFactory.getLogger(SuspensionController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private BillingResolutionService billingResolutionService;

	@RequestMapping(value = { "/suspension-request.html" }, method = RequestMethod.GET)
	public ModelAndView getSuspensionRequestPage(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension request page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.setViewName("suspension_request");
		return modelAndView;
	}

	@RequestMapping(value = { "/suspension-cancel.html" }, method = RequestMethod.GET)
	public ModelAndView getSuspensionCancelPage(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension cancel page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofitem", "null");
		modelAndView.addObject("filterForm", new SuspensionForm());
		modelAndView.setViewName("suspension_cancel");
		return modelAndView;
	}

	@RequestMapping(value = { "/suspension-cancel.html" }, method = RequestMethod.POST)
	public ModelAndView getSuspensionDetailsByFilter(@ModelAttribute SuspensionForm filterForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		if ((!filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService
					.findByMsisdnAndTransactionId(filterForm.getMsisdn(), filterForm.getRequestId()));
		} else if ((!filterForm.getMsisdn().equals("")) && (filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService.findByMsisdn(filterForm.getMsisdn()));
		} else if ((filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem",
					billingResolutionService.findByTransactionId(filterForm.getRequestId()));
		}
		modelAndView.addObject("filterForm", filterForm);
		modelAndView.setViewName("suspension_cancel");
		return modelAndView;
	}

	@RequestMapping(value = { "/suspension-acknowledgment.html" }, method = RequestMethod.GET)
	public ModelAndView acknowledgeSuspension(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension acknowledgment page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofitem", "null");
		modelAndView.addObject("filterForm", new SuspensionForm());
		modelAndView.setViewName("suspension_acknowledgment");
		return modelAndView;
	}
	
	@RequestMapping(value = { "/suspension-acknowledgment.html" }, method = RequestMethod.POST)
	public ModelAndView getSuspensionDNOAckByFilter(@ModelAttribute SuspensionForm filterForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		if ((!filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService
					.findByMsisdnAndTransactionId(filterForm.getMsisdn(), filterForm.getRequestId()));
		} else if ((!filterForm.getMsisdn().equals("")) && (filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService.findByMsisdn(filterForm.getMsisdn()));
		} else if ((filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem",
					billingResolutionService.findByTransactionId(filterForm.getRequestId()));
		}
		modelAndView.addObject("filterForm", filterForm);
		modelAndView.setViewName("suspension_acknowledgment");
		return modelAndView;
	}

	@RequestMapping(value = { "/barring-request.html" }, method = RequestMethod.GET)
	public ModelAndView getBarringRequestPage(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension request page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.setViewName("barring-request");
		return modelAndView;
	}

	@RequestMapping(value = { "/barring-cancel.html" }, method = RequestMethod.GET)
	public ModelAndView getBarringCancelPage(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension cancel page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofitem", "null");
		modelAndView.addObject("filterForm", new SuspensionForm());
		modelAndView.setViewName("barring-cancel");
		return modelAndView;
	}

	@RequestMapping(value = { "/barring-cancel.html" }, method = RequestMethod.POST)
	public ModelAndView getBarringDetailsByFilter(@ModelAttribute SuspensionForm filterForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		if ((!filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService
					.findByMsisdnAndTransactionId(filterForm.getMsisdn(), filterForm.getRequestId()));
		} else if ((!filterForm.getMsisdn().equals("")) && (filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService.findByMsisdn(filterForm.getMsisdn()));
		} else if ((filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem",
					billingResolutionService.findByTransactionId(filterForm.getRequestId()));
		}
		modelAndView.addObject("filterForm", filterForm);
		modelAndView.setViewName("barring-cancel");
		return modelAndView;
	}

	@RequestMapping(value = { "/barring-receipt-confirmation.html" }, method = RequestMethod.GET)
	public ModelAndView getBarringReceiptConfirmation(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: SuspensionController.getSuspensionProcess() - Trying to access the suspension acknowledgment page using the username : ["
				+ user.getUsername() + "]");
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("listofitem", "null");
		modelAndView.addObject("filterForm", new SuspensionForm());
		modelAndView.setViewName("barring-receipt-confirmation");
		return modelAndView;
	}
	
	@RequestMapping(value = { "/barring-receipt-confirmation.html" }, method = RequestMethod.POST)
	public ModelAndView getBarringReceiptConfirmationDetails(@ModelAttribute SuspensionForm filterForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		if ((!filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService
					.findByMsisdnAndTransactionId(filterForm.getMsisdn(), filterForm.getRequestId()));
		} else if ((!filterForm.getMsisdn().equals("")) && (filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem", billingResolutionService.findByMsisdn(filterForm.getMsisdn()));
		} else if ((filterForm.getMsisdn().equals("")) && (!filterForm.getRequestId().equals(""))) {
			modelAndView.addObject("listofitem",
					billingResolutionService.findByTransactionId(filterForm.getRequestId()));
		}
		modelAndView.addObject("filterForm", filterForm);
		modelAndView.setViewName("barring-receipt-confirmation");
		return modelAndView;
	}

}