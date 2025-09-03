package com.betacom.fe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	@Bean
	/*
	 * definizione delle regole di security
	 */
	SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
					 .requestMatchers("/", "/register", "/login", "/saveNuovoUtente").permitAll()
					.anyRequest().authenticated()
					)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
				);
			return http.build();
	}
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}


