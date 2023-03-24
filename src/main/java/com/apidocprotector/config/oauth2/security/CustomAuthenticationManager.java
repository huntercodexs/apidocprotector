package com.apidocprotector.config.oauth2.security;

import com.apidocprotector.config.oauth2.model.OperatorEntity;
import com.apidocprotector.config.oauth2.repository.Oauth2ClientRepository;
import com.apidocprotector.config.oauth2.repository.OperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomAuthenticationManager implements AuthenticationManager {

    @Value("${oauth.basic-auth.credentials:db}")
    String sourceCredentials;

    @Autowired
    CustomOperatorDetailsService customOperatorDetailsService;

    @Value("${oauth.basic-auth.username:}")
    String usernameAuthenticate;

    @Value("${oauth.basic-auth.password:}")
    String passwordAuthenticate;

    @Value("${oauth.basic-auth.role:1}")
    int roleAuthenticate;

    @Autowired
    Oauth2ClientRepository oauth2ClientRepository;

    @Autowired
    OperatorRepository operatorRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        switch (sourceCredentials) {
            case "db":
                return authenticateWithDatabase(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
            case "file":
                return authenticateWithProps(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
            default:
                throw new RuntimeException("Invalid Source Credentials");
        }

    }

    private Authentication authenticateWithDatabase(String usernameCred, String passwordCred) {

        if (usernameCred.equals("") || passwordCred.equals("")) {
            throw new BadCredentialsException("Missing Credentials");
        }

        try {

            OperatorEntity operatorEntity = operatorRepository.findByUsername(usernameCred);

            if (operatorEntity == null || operatorEntity.getUsername().equals("")) {
                throw new BadCredentialsException("Not Found Operator");
            }

            if (operatorEntity.getStatus() == 0) {
                throw new BadCredentialsException("Inactive Operator");
            }

            if (operatorEntity.isDeleted()) {
                throw new BadCredentialsException("Deleted Operator");
            }

            if (!isValidCredentials(passwordCred, operatorEntity.getPassword())) {
                throw new BadCredentialsException("Invalid Credentials");
            }

            customOperatorDetailsService.setUserCredentialsFromDatabase(operatorEntity.getRole(), usernameCred, passwordCred);
            UserDetails userDetails = customOperatorDetailsService.loadUserByUsername(usernameCred);
            return new UsernamePasswordAuthenticationToken(userDetails, passwordCred, userDetails.getAuthorities());

        } catch (BadCredentialsException bc) {
            bc.printStackTrace();
            throw new BadCredentialsException("Credentials Fail: " + bc.getMessage());
        }

    }

    private Authentication authenticateWithProps(String usernameCred, String passwordCred) {

        if (usernameCred.equals(usernameAuthenticate) && isValidCredentials(passwordCred, passwordAuthenticate)) {
            customOperatorDetailsService.setUserCredentialsFromProps(roleAuthenticate, usernameCred, passwordCred);
            UserDetails userDetails = customOperatorDetailsService.loadUserByUsername(usernameCred);
            return new UsernamePasswordAuthenticationToken(userDetails, passwordCred, userDetails.getAuthorities());
        }

        throw new BadCredentialsException("Invalid Credentials");
    }

    private boolean isValidCredentials(String passwordCredentials, String passwordAuthenticate) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordBCrypt = bCryptPasswordEncoder.encode(passwordAuthenticate);
        return bCryptPasswordEncoder.matches(passwordCredentials, passwordBCrypt);
    }

}
