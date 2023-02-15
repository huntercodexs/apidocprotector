DROP TABLE IF EXISTS `apidoc_protector`;
CREATE TABLE `apidoc_protector` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL,
  `sessionKey` varchar(255) DEFAULT NULL,
  `sessionVal` varchar(255) DEFAULT NULL,
  `sessionCreatedAt` varchar(255) DEFAULT NULL,
  `createdAt` varchar(255) DEFAULT NULL,
  `updatedAt` varchar(255) DEFAULT NULL,
  `deletedAt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `apidoc_protector_audit`;
CREATE TABLE `apidoc_protector_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `createdAt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--SAMPLES
INSERT INTO huntercodexs.apidoc_protector (name,username,email,`role`,password,token,active,sessionKey,sessionVal,sessionCreatedAt,createdAt,updatedAt,deletedAt) VALUES
	 ('System','system','system@email.com','system','e807f1fcf82d132f9bb018ca6738a19f','edd301e20efe270b4ce05a82abdaee5a','yes',NULL,NULL,NULL,'2022-12-02 17:33:06',NULL,NULL),
	 ('Administrator','admin','admin@email.com','admin','e10adc3949ba59abbe56e057f20f883e','69ab4c17896e692d8274fbf8322eceee','yes','076197338AE6189DA746C46DC3FA5317','B8A881477390ABB25448876DB680D6F3','2022-12-02 17:41:17','2022-12-02 17:33:24','2022-12-02 17:41:17',NULL),
	 ('Mary Smith','mary','mary@email.com','viewer','827ccb0eea8a706c4c34a16891f84e7b','e6e3f904537978738cb8ff42f485cf14','yes',NULL,NULL,NULL,'2022-12-02 17:34:03',NULL,NULL),
	 ('John Connor','john','john@email.com','user','81dc9bdb52d04dc20036dbd8313ed055','18ca27efabb304a5b1984bc5bb944741','yes',NULL,NULL,NULL,'2022-12-02 17:35:26',NULL,NULL),
	 ('Brow Wizz','brow','brow@email.com','viewer','827ccb0eea8a706c4c34a16891f84e7b','3b7f0666b49e9880eb7d81daae48fae7','no',NULL,NULL,NULL,'2022-12-02 17:39:56',NULL,NULL);
