package com.betacom.fe.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.configuration.UtenteServices;
import com.betacom.fe.requests.UtenteReq;
import com.betacom.fe.response.ResponseBase;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginLogoutController {

	private WebClient clientWeb;
	private UtenteServices utS;

	public LoginLogoutController(WebClient clientWeb, UtenteServices utS) {
		this.clientWeb = clientWeb;
		this.utS = utS;
	}

	
	
	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}
	
	 @GetMapping("/register")
	 public ModelAndView showRegistrationForm() {
			ModelAndView mav = new ModelAndView("registrazione");
			mav.addObject("req", new UtenteReq());
			
	        return mav; 
	 }
	 
	 @PostMapping("/saveNuovoUtente")
	 public Object saveNuovoUtente(@ModelAttribute("req") UtenteReq req) {
	
		req.setRole("USER");
	    log.debug("Nuovo utente:" + req);
		ResponseBase resp = null;
	
		resp = clientWeb.post()
				.uri("utente/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
		
		log.debug("create :" + resp.getRc());
		
		if (!resp.getRc()) {
			ModelAndView mav = new ModelAndView("register");
			mav.addObject("req", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		}
		
		utS.updateUtente(req);
	     
	    return "redirect:/login";
	 }

}
