package com.betacom.fe.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.betacom.fe.dto.UtenteDTO;
import com.betacom.fe.response.ResponseList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsServices {
	
	private WebClient clientWeb;
	private PasswordEncoder getPasswordEncoder;
	
	public CustomUserDetailsServices(WebClient clientWeb, PasswordEncoder getPasswordEncoder) {
		this.clientWeb = clientWeb;
		this.getPasswordEncoder = getPasswordEncoder;
	}
	
	public InMemoryUserDetailsManager lodUsers() {
		log.debug("lodUsers....");
		
		ResponseList<UtenteDTO> ut = clientWeb.get()
				.uri("utente/list")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseList<UtenteDTO>>() {})
				.block();
		
		List<UserDetails> userDetailsList = ut.getDati().stream()
				.map (usr ->  User.withUsername(usr.getUserName())
						.password(getPasswordEncoder.encode(usr.getPwd().toString()))
						.roles(usr.getRole())
						.build())
				.toList();
				
		return new InMemoryUserDetailsManager(userDetailsList);
	}
	
}
