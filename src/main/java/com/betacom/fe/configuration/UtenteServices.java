package com.betacom.fe.configuration;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.betacom.fe.requests.UtenteReq;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UtenteServices {
	
	private PasswordEncoder getPasswordEncoder;
	private InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	public UtenteServices(PasswordEncoder getPasswordEncoder, InMemoryUserDetailsManager inMemoryUserDetailsManager) {
		this.getPasswordEncoder = getPasswordEncoder;
		this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
	}
	
	
	public void updateUtente(UtenteReq req) {
		
		if (inMemoryUserDetailsManager.userExists(req.getUserName())) {
			inMemoryUserDetailsManager.deleteUser(req.getUserName());  // utente deleted
			log.debug("Delete " + req.getUserName());
		}	
		
		inMemoryUserDetailsManager.createUser(
					User
						.withUsername(req.getUserName())
						.password(getPasswordEncoder.encode(req.getPwd()).toString())
						.roles(req.getRole())
						.build()
					);
			
		log.debug("Create " + req.getUserName());
		
	}

	public void removeUtente(String username) {
		
		if (inMemoryUserDetailsManager.userExists(username)) {
			inMemoryUserDetailsManager.deleteUser(username);  // utente deleted
			log.debug("Delete " + username);
		}	
	}

}
