package com.apidocprotector.config.oauth2.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class LoggedOperator extends User {

	private final Logger LOG = LoggerFactory.getLogger(LoggedOperator.class);

	public LoggedOperator(String usernameCred, String passwordCred, List<GrantedAuthority> authorities) {
		super(usernameCred, passwordCred, authorities);
	}

}
