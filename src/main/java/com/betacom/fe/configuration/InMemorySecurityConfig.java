package com.betacom.fe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemorySecurityConfig {

	private CustomUserDetailsServices customUserDetailsServices;

	public InMemorySecurityConfig(CustomUserDetailsServices customUserDetailsServices) {
		this.customUserDetailsServices = customUserDetailsServices;
	}
	
	@Bean
	InMemoryUserDetailsManager inMemoryUserDetailsManager() {
		return customUserDetailsServices.lodUsers();
	}
}
