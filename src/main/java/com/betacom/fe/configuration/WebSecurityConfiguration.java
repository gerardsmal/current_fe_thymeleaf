package com.betacom.fe.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	/*
	 * definizione delle regole di security
	 */
	@Bean
	/*
	 * Questo WebSecurity permetter a accedere al sito senza filtro
	 */
	
//	SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
//		http
//			.authorizeHttpRequests((authorize) -> authorize
//					.anyRequest().permitAll()
//					)
//			.formLogin((form) -> form.disable());
//			return http.build();
//	}
	/*
	 * Questo WebSecurity utilizza il form di login pero prende user/pwd predefinito
	 */
//	SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
//		http
//			.authorizeHttpRequests((authorize) -> authorize
//					.requestMatchers("/").permitAll()
//					.anyRequest().authenticated()
//					)
//			.formLogin((form) -> form
//				.loginPage("/login")
//				.permitAll()
//				);
//			return http.build();
//	}
	/*
	 * definizione delle regole di security
	 */
	SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
					.requestMatchers("/").permitAll()
					.anyRequest().authenticated()
					)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
				);
			return http.build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		
		List<UserDetails> ud = new ArrayList<UserDetails>();
		
		UserDetails admin =
				User.withUsername("admin")
				.password(getPasswordEncoder().encode("pwd").toString())
				.roles("ADMIN")
				.build();
		
		UserDetails user =
				User.withUsername("user")
				.password(getPasswordEncoder().encode("user").toString())
				.roles("USER")
				.build();
		
		ud.add(user);
		ud.add(admin);
		
		return new InMemoryUserDetailsManager(ud);
	}
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}


