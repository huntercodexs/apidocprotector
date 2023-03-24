package com.apidocprotector.config.oauth2.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class LoggedOperator extends User {

	public LoggedOperator(String usernameCred, String passwordCred, List<GrantedAuthority> authorities) {
		super(usernameCred, passwordCred, authorities);
	}

}
