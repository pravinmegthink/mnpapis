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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.megthink.gateway.dao.PortMeDao;
import com.megthink.gateway.dao.PortMtDao;
import com.megthink.gateway.dao.PrivilegesDao;
import com.megthink.gateway.form.PortMeForm;
import com.megthink.gateway.model.Privileges;
import com.megthink.gateway.model.User;
import com.megthink.gateway.service.ConfigOjbectService;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.utils.ReadConfigFile;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PortMeController {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegesDao privilegesDao;
	@Autowired
	private PortMeService portMeService;
	@Autowired
	private PortMeDao portMeDao;
	@Autowired
	private PortMtDao portMtDao;
	// @Autowired
	// private LocalizationSupportService localizationService;
	@Autowired
	private ConfigOjbectService configOjbectService;
	// @Autowired
	// private SCNoticeDao scNoticeDao;

	@RequestMapping(value = { "/portin.html" }, method = RequestMethod.GET)
	public ModelAndView getPortIN(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMeDao.getListPortMeDetails(reqType, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("portinview");
		return modelAndView;
	}

	@RequestMapping(value = { "/portin.html" }, method = RequestMethod.POST)
	public ModelAndView getPortINByFilters(@ModelAttribute PortMeForm portMeForm) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme", portMeService.getListPortMeDetailsByDateRange(reqType, dateRange,
				portMeForm.getRequestId(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("portinview");
		return modelAndView;
	}

	@RequestMapping(value = { "/portinrequest.html" }, method = RequestMethod.GET)
	public ModelAndView getPortinReqeust(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		modelAndView.addObject("op_id",user.getOp_id());
		modelAndView.addObject("listofarea",portMeDao.getListOfAreaByOpId(user.getOp_id()));
		modelAndView.addObject("visible", configOjbectService.findConfigOjbectByKey("upload_doc").getValue());
		modelAndView.setViewName("portinrequest");
		return modelAndView;
	}

	@RequestMapping(value = { "/portindetail.html" }, method = RequestMethod.GET)
	public ModelAndView getPortMeDetailsByPortId(@RequestParam(value = "portId", required = false) int portId) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMtDao.getListPortMtDetails(reqType, portId));
		modelAndView.setViewName("portindetail");
		return modelAndView;
	}

	@RequestMapping(value = { "/getcancellation.html" }, method = RequestMethod.GET)
	public ModelAndView getPortMeDetail() {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMtDao.getListPortMEDetails(reqType, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("cancellation");
		return modelAndView;
	}

	@RequestMapping(value = { "/getcancellation.html" }, method = RequestMethod.POST)
	public ModelAndView getPortMeDetailForCancel(@ModelAttribute PortMeForm portMeForm) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme", portMtDao.getListPortMeDetailsByDateRange(reqType, dateRange,
				portMeForm.getRequestId(), portMeForm.getMsisdn(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("cancellation");
		return modelAndView;
	}

	@RequestMapping(value = { "/portactivation.html" }, method = RequestMethod.GET)
	public ModelAndView getPortActivation(HttpServletRequest request) throws ParseException {
		_logger.info("getting port activation results...");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMeDao.getListPortMT(reqType, 12, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("activation");
		return modelAndView;
	}

	@RequestMapping(value = { "/portactivation.html" }, method = RequestMethod.POST)
	public ModelAndView getPortActivationByDateRange(@ModelAttribute PortMeForm portMeForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme", portMeService.getListPortMtByDateRange(reqType, 12, dateRange,
				portMeForm.getRequestId(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("activation");
		return modelAndView;
	}

	@RequestMapping(value = { "/getactivationdetail.html" }, method = RequestMethod.GET)
	public ModelAndView getPortActivationDetailsByPortId(
			@RequestParam(value = "id", required = false) String requestId) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme",
				portMtDao.getListPortMtDetails(reqType, requestId, 12, user.getUserId()));
		modelAndView.setViewName("activationdetail");
		return modelAndView;
	}

	@RequestMapping(value = { "/portout.html" }, method = RequestMethod.GET)
	public ModelAndView getPortOut(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMeDao.getListPortMeDetails(reqType, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("portoutview");
		return modelAndView;
	}

	@RequestMapping(value = { "/portout.html" }, method = RequestMethod.POST)
	public ModelAndView getPortOutByDateRange(@ModelAttribute PortMeForm portMeForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme",
				portMeDao.getListPortMeDetails(reqType, user.getUserId(), portMeForm.getRequestId(),dateRange));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("portoutview");
		return modelAndView;
	}

	@RequestMapping(value = { "/portoutdetail.html" }, method = RequestMethod.GET)
	public ModelAndView getPortMeOutDetailsByPortId(@RequestParam(value = "portId", required = false) int portId) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMtDao.getListPortMtDetails(reqType, portId));
		modelAndView.setViewName("portoutdetail");
		return modelAndView;
	}

	@RequestMapping(value = { "/portapproval.html" }, method = RequestMethod.GET)
	public ModelAndView getPortAproval(HttpServletRequest request) throws ParseException {
		_logger.info("getting port activation results...");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMeDao.getListPortMT(reqType, 3, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("portaproval");
		return modelAndView;
	}

	@RequestMapping(value = { "/portapproval.html" }, method = RequestMethod.POST)
	public ModelAndView getPortAprovalByDateRange(@ModelAttribute PortMeForm portMeForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme", portMeService.getListPortMtByDateRange(reqType, 3, dateRange,
				portMeForm.getRequestId(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("portaproval");
		return modelAndView;
	}

	@RequestMapping(value = { "/portapprovaldetail.html" }, method = RequestMethod.GET)
	public ModelAndView getPortAprovalByPortId(@RequestParam(value = "id", required = false) String requestId) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMtDao.getListPortMtDetails(reqType, requestId, 3, user.getUserId()));
		modelAndView.setViewName("portaprovaldetail");
		return modelAndView;
	}

	@RequestMapping(value = { "/disconnection.html" }, method = RequestMethod.GET)
	public ModelAndView getPortDisconnection(HttpServletRequest request) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme", portMeDao.getListPortMT(reqType, 12, user.getUserId()));
		modelAndView.addObject("portMeForm", new PortMeForm());
		modelAndView.setViewName("disconnection");
		return modelAndView;
	}

	@RequestMapping(value = { "/disconnection.html" }, method = RequestMethod.POST)
	public ModelAndView getPortDisconnectionByDateRange(@ModelAttribute PortMeForm portMeForm) throws ParseException {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		String dateRange = null;
		if (portMeForm.getDateRange() != null) {
			String[] split = portMeForm.getDateRange().split("\\-");
			if (split.length > 1) {
				dateRange = "'" + split[0] + "' and '" + split[1] + "'";
			}
		}
		modelAndView.addObject("listofportme", portMeService.getListPortMtByDateRange(reqType, 12, dateRange,
				portMeForm.getRequestId(), user.getUserId()));
		modelAndView.addObject("portMeForm", portMeForm);
		modelAndView.setViewName("disconnection");
		return modelAndView;
	}

	@RequestMapping(value = { "/disconnectiondetail.html" }, method = RequestMethod.GET)
	public ModelAndView getPortDisconnectionByPortId(@RequestParam(value = "id", required = false) String requestId) {
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		List<Privileges> privilegesShownList = privilegesDao.getPrivilegesListByUser(user.getUserId());
		modelAndView.addObject("leftMenuList", privilegesShownList.isEmpty() ? null : privilegesShownList);
		// modelAndView.addObject("localization",
		// localizationService.findLocalizationSupport());
		// Locale locale = LocaleContextHolder.getLocale();
		// modelAndView.addObject("locale",
		// localizationService.findLocalizationSupportByConstKey(locale.toString()).getDescription());
		modelAndView.addObject("listofportme",
				portMtDao.getListPortMtDetails(reqType, requestId, 12, user.getUserId()));
		modelAndView.setViewName("disconnectiondetail");
		return modelAndView;
	}
}