package com.apidocprotector.config.oauth2.security;

import com.apidocprotector.config.oauth2.repository.OperatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomOperatorDetailsService implements UserDetailsService {

	@Autowired
	OperatorRepository operatorRepository;

	private String usernameCred;
	private String passwordCred;
	private List<GrantedAuthority> authorities;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		LoggedOperator loggedOperator = new LoggedOperator(this.usernameCred, this.passwordCred, this.authorities);

		UserDetails userDetails = new User(
				username,
				loggedOperator.getPassword(),
				true,
				false,
				false,
				false,
				this.authorities);

		if (!loggedOperator.isAccountNonExpired() || !loggedOperator.isCredentialsNonExpired() || !loggedOperator.isAccountNonLocked()) {

			return new User(
					this.usernameCred,
					this.passwordCred,
					false,
					true,
					true,
					true,
					AuthorityUtils.NO_AUTHORITIES);

		}

		return userDetails;

	}

	public void setUserCredentialsFromDatabase(String userRole, String usernameCred, String passwordCred) {
		this.usernameCred = usernameCred;
		this.passwordCred = passwordCred;
		this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(userRole);
	}

	public void setUserCredentialsFromProps(int userLevel, String usernameCred, String passwordCred) {

		String roles;

		switch (userLevel) {
			case 0:
				roles = RoleOperator.ROLE_ADMIN.name();
				break;
			case 1:
				roles = RoleOperator.ROLE_USER.name();
				break;
			case 2:
				roles = RoleOperator.ROLE_CLIENT.name();
				break;
			case 3:
				roles = RoleOperator.ROLE_OPERATOR.name();
				break;
			case 4:
				roles = RoleOperator.ROLE_MODERATOR.name();
				break;
			default:
				roles = "";

		}

		this.usernameCred = usernameCred;
		this.passwordCred = passwordCred;
		this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
	}

}

