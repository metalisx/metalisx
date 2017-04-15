package org.metalisx.common.cdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractUserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	public String hello(String user) {
		LOGGER.info("AbstractUserService: hello");
		return "Hello " + user;
	}

}
