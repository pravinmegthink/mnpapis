package com.megthink.gateway.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class APIGateway {

	private static final Logger logger = LoggerFactory.getLogger(APIGateway.class);

	@RequestMapping(value = { "/ping", }, method = RequestMethod.GET)
	public String ping() {
		logger.info("MNP Gateway application is up");
		logger.debug("Debug message");
        logger.info("Info message");
        logger.warn("Warning message");
        logger.error("Error message");
		return "MNP Gateway application is up";
	}
}
