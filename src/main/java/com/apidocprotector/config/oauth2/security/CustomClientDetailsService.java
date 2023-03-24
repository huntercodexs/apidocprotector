package com.apidocprotector.config.oauth2.security;

import com.apidocprotector.config.oauth2.model.AuthorizationEntity;
import com.apidocprotector.config.oauth2.repository.Oauth2ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.apidocprotector.config.oauth2.security.RoleOperator.ROLE_CLIENT;

@Service
public class CustomClientDetailsService implements ClientDetailsService {

	@Autowired
    Oauth2ClientRepository oauth2ClientRepository;

    @Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        BaseClientDetails details = new BaseClientDetails();
        AuthorizationEntity authorizationEntity = oauth2ClientRepository.findByClient(clientId);
        String secret = new String(Base64.getDecoder().decode(authorizationEntity.getSecret()));

        details.setClientId(clientId);
        details.setClientSecret(secret);
        details.setAuthorizedGrantTypes(Arrays.asList("password", "authorization_code", "refresh_token"));
        details.setScope(Arrays.asList("read", "write")); //trust
        details.setResourceIds(Collections.singletonList("oauth2-resource"));
        details.setAccessTokenValiditySeconds(authorizationEntity.getAccessTokenValiditySeconds());
        details.setRefreshTokenValiditySeconds(authorizationEntity.getRefreshTokenValiditySeconds());

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_CLIENT.name()));

        details.setAuthorities(authorities);

        return details;
	}
	
}
