package com.betacom.fe.controller;

import java.time.LocalDate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.SocioDTO;
import com.betacom.fe.requests.AbbonamentoReq;
import com.betacom.fe.requests.AttivitaReq;
import com.betacom.fe.response.ResponseBase;
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
		mav.addObject("socioID", id);
		mav.addObject("listAbb", soc.getAbbonamento());
		mav.addObject("addAttivita", "L");
		return mav;
	}
	
	@GetMapping("createAbbonamento")
	public String createAbbonamento(@RequestParam Integer socioID) {
		log.debug("createAbbonamento id:" + socioID);
		
		AbbonamentoReq req = new AbbonamentoReq();
		req.setSocioId(socioID);
		req.setDataIscrizione(LocalDate.now());
		
		ResponseBase  resp = clientWeb.post()
				.uri("abbonamento/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		
		log.debug("rc:" + resp.getRc());
		
		return "redirect:/listAbbonamenti?id=" + socioID;
	}
	
	@GetMapping("aggiungiAttivita")
	public ModelAndView aggiungiAttivita(@RequestParam Integer socioId,@RequestParam Integer abbonamentoId) {
		log.debug("Controller listAbbonamento abbonamentoId:" + abbonamentoId +  " socioId:" + socioId);
		
		SocioDTO soc = clientWeb.get()
			.uri(uriBuilder -> uriBuilder
					.path("socio/getSocio")
					.queryParam("id", socioId)
					.build())
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
			.block()
			.getDati();
		log.debug("socio abbo:" + soc.getAbbonamento().size() );
	
		
		ResponseList<?> atti = clientWeb.get()
		        .uri("/attivita/list") // viene aggiunto a bas
		        .retrieve()
                .bodyToMono(ResponseList.class)
                .block(); // solo in MVC sync

		ModelAndView mav = new ModelAndView("listAbbonamenti");
		
		AttivitaReq attiReq = new AttivitaReq();
		attiReq.setAbbonamentiId(abbonamentoId);
		attiReq.setSocioId(socioId);

		mav.addObject("param", attiReq);
		mav.addObject("listAttivita", atti);	
		mav.addObject("listAbb", soc.getAbbonamento());
		mav.addObject("socioID", abbonamentoId);
		mav.addObject("addAttivita", "A");
	
		return mav;
	}

	@PostMapping("saveAttivitaAbbonamento")
	public String saveAttivitaAbbonamento(@ModelAttribute("param") AttivitaReq req) {
		log.debug("saveAttivitaAbbonamento:" + req);
		
		ResponseBase  resp = clientWeb.post()
				.uri("attivita/createAttivitaAbbonamento")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		
		log.debug("rc:" + resp.getRc());
		
		
		
		return "redirect:/listAbbonamenti?id=" + req.getSocioId();
	}
	
	@GetMapping("removeAttivitaFromAbbonamento")
	public String removeAttivitaFromAbbonamento(@RequestParam Integer idAbbonamento, 
			@RequestParam Integer idAttivita,
			@RequestParam Integer socioID) {
		
		log.debug("removeAttivitaFromAbbonamento :" + idAbbonamento + "/" + idAttivita + "/" + socioID);
		
		AttivitaReq req = new AttivitaReq();
		req.setAbbonamentiId(idAbbonamento);
		req.setId(idAttivita);
		
		ResponseBase  resp = clientWeb.post()
				.uri("attivita/removeAttivitaAbbonamento")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		
		log.debug("rc:" + resp.getRc());
		
		
		return "redirect:/listAbbonamenti?id=" + socioID;
		
	}
	
	
	@GetMapping("removeAbbonamento")
	public String removeAbbonamento(@RequestParam Integer idAbbonamento, 
			@RequestParam Integer socioID) {
		
		log.debug("removeAbbonamento :" + idAbbonamento + "/"  + socioID);
		AbbonamentoReq req = new AbbonamentoReq();
		req.setId(idAbbonamento);
		req.setSocioId(socioID);
		
		ResponseBase  resp = clientWeb.post()
				.uri("abbonamento/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		
		log.debug("rc:" + resp.getRc() + " msg:" + resp.getMsg());
		
		
		return "redirect:/listAbbonamenti?id=" + socioID;
		
	}

	
}
