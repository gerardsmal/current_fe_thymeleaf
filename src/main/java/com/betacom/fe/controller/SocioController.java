package com.betacom.fe.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriBuilder;

import com.betacom.fe.response.ResponseList;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class SocioController {

	private WebClient clientWeb;
	
	public SocioController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}
	
	
	@GetMapping(value= {"/", "listSocio", "home" })
	public ModelAndView listSocio(
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String  nome,
			@RequestParam(required = false) String  cognome,
			@RequestParam(required = false) String  attivita
			) {
		ModelAndView mav = new ModelAndView("listSocio");
		
		ResponseList<?> lSoc = clientWeb.get()
				.uri(UriBuilder -> UriBuilder
						.path("socio/list")
						.queryParamIfPresent("id", Optional.ofNullable(id))
						.queryParamIfPresent("nome", Optional.ofNullable(nome))
						.queryParamIfPresent("cognome", Optional.ofNullable(cognome))
						.queryParamIfPresent("attivita", Optional.ofNullable(attivita))
						.build())
				.retrieve()
				.bodyToMono(ResponseList.class)
				.block();
						
		log.debug("Response rc:" + lSoc.getRc()  + " numero di socio:" + lSoc.getDati().size());
		
		mav.addObject("listSocio", lSoc.getDati());
		
		return mav;
	}



	
}
