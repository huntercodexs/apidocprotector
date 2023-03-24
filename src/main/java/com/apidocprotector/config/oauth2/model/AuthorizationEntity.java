package com.apidocprotector.config.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "oauth2_authorization_server_client")
public class AuthorizationEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column
    private String client;

    @Column
    private String secret;

    @Column
    private String scope;

    @Column
    private int accessTokenValiditySeconds;

    @Column
    private int refreshTokenValiditySeconds;
    
}
