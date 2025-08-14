package com.betacom.fe.controller;

import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.SocioDTO;
import com.betacom.fe.requests.SocioReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

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
						
		log.debug("Response rc:" + lSoc.getRc() );

		ResponseList<?> atti = clientWeb.get()
				.uri("attivita/list")
				.retrieve()
				.bodyToMono(ResponseList.class)
				.block();
		
		log.debug("Response attivita rc:" + atti.getRc());
		
		mav.addObject("listAttivita", atti);
		mav.addObject("param", new SocioReq());
		mav.addObject("listSocio", lSoc.getDati());
		
		return mav;
	}

	@GetMapping("createSocio")
	public ModelAndView createSocio() {
		ModelAndView mav = new ModelAndView("createSocio");
		SocioReq r = new SocioReq();
		r.setErrrorMsg(null);
		mav.addObject("socio", r);
		
		return mav;
	}
	
	@GetMapping("updateSocio")
	public ModelAndView updateSocio(@RequestParam(required = true) Integer id) {
		log.debug("updateSocio :" + id);
		
		SocioDTO resp = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("socio/getSocio")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
				.block()
				.getDati();
		
		log.debug("Response :" + resp.getCognome() + " " + resp.getNome());
		
		SocioReq s = SocioReq.builder()
				.id(resp.getId())
				.cognome(resp.getCognome())
				.nome(resp.getNome())
				.codiceFiscale(resp.getCodiceFiscale())
				.email(resp.getEmail())
				.errrorMsg(null)
				.build();
		
		ModelAndView mav = new ModelAndView("createSocio");
		mav.addObject("socio", s);
		
		return mav;
	}
	
	
	@PostMapping("saveSocio")
	public Object saveSocio(@ModelAttribute("socio") SocioReq req) {
		log.debug("saveSocio:" + req);
		
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "socio/" + operation;		
		HttpMethod typeM = "create".equalsIgnoreCase(operation) ? HttpMethod.POST : HttpMethod.PUT;
	
			resp = clientWeb.method(typeM)
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(ResponseBase.class)
					.block();			
		
		log.debug(operation + " :" + resp.getRc());
		
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("createSocio");
			req.setErrrorMsg(resp.getMsg());
			mav.addObject("socio", req);
			return mav;
		}
		
		return "redirect:/listSocio";
	}

	@GetMapping("removeSocio")
	public Object removeSocio(@RequestParam(required = true) Integer id) {
		log.debug("removeSocio:" + id);
		
		SocioReq req = new SocioReq();
		req.setId(id);
		
		ResponseBase resp = clientWeb.post()
				.uri("socio/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
		
		
		log.debug("rc:" + resp.getRc() + " msg:" + resp.getMsg());
		
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("createSocio");
			req.setErrrorMsg(resp.getMsg());
			mav.addObject("socio", req);
			return mav;
		}
		
		return "redirect:/listSocio";
		
	}
}
