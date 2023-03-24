
CREATE TABLE IF NOT EXISTS  `oauth2_authorization_server_client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `client` varchar(255) DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `accessTokenValiditySeconds` int DEFAULT NULL,
  `refreshTokenValiditySeconds` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `oauth2_authorization_server_client` VALUES (1,'client_id','Y2JmY2M3NGItMDdjZC00YWJiLTkwNmItYWJkZGQ4ZmExYmVj','read-write',3600,7200);

CREATE TABLE IF NOT EXISTS `oauth2_operator_server_client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `deleted` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `oauth2_operator_server_client` VALUES
(1,'OAUTH2DEMO_ADMIN','1234567890','ROLE_ADMIN',null,0,1),
(2,'OAUTH2DEMO_USER','1234567890','ROLE_USER',null,0,1),
(3,'OAUTH2DEMO_CLIENT','1234567890','ROLE_CLIENT',null,0,1);
