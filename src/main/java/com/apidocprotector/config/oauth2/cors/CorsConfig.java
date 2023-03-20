package com.apidocprotector.config.oauth2.cors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class CorsConfig implements  WebMvcConfigurer  {

	private final Logger LOG = LoggerFactory.getLogger(CorsConfig.class);
	
	@Value("#{'${cors.allowed.origins}'.split(',')}")
	private List<String> rawOrigins;	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**")
			//.allowedOrigins(getOrigin())
			.allowedOrigins("*")
			//.allowedMethods("POST", "GET",  "PUT", "DELETE", "OPTIONS")
			.allowedMethods("*")
			//.allowedHeaders("X-Auth-Token", "Content-Type")
			.allowedHeaders("*")
			//.exposedHeaders("custom-header1", "custom-header2")
			.allowCredentials(true)
			.maxAge(4800);

	}
	
	public String[] getOrigin() {

	    int size = rawOrigins.size();
	    String[] originArray = new String[size];

		return rawOrigins.toArray(originArray);

	}	
	
}
