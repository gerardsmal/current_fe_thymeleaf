package com.betacom.fe.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.SocioDTO;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AbbonamentoController {
	private WebClient clientWeb;

	public AbbonamentoController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}
	
	@GetMapping("listAbbonamenti")
	public ModelAndView listAbbonamenti(@RequestParam Integer id) {
		log.debug("listAbbonamenti:" + id);
		ModelAndView mav = new ModelAndView("listAbbonamenti");
		
		
		SocioDTO soc = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("socio/getSocio")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
				.block()
				.getDati();
		log.debug("socio abbo:" + soc.getAbbonamento().size() );
		
		soc.getAbbonamento().forEach(a -> log.debug(a.toString()));
		
		return mav;
	}
	
	
}
