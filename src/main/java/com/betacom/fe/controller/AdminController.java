package com.betacom.fe.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.betacom.fe.configuration.UtenteServices;
import com.betacom.fe.dto.UtenteDTO;
import com.betacom.fe.requests.AttivitaReq;
import com.betacom.fe.requests.UtenteReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {
	private WebClient clientWeb;
	private UtenteServices utS;

	public AdminController(WebClient clientWeb, UtenteServices utS) {
		this.clientWeb = clientWeb;
		this.utS = utS;
	}

	
	@GetMapping("/listAttivita")
	public ModelAndView listAttivita(Model model) {
		ModelAndView mav = new ModelAndView("admin/listAttivita");

		ResponseList<?> atti = clientWeb.get()
				.uri("attivita/list")
				.retrieve()
				.bodyToMono(ResponseList.class)
				.block();
		
		log.debug("Response attivita rc:" + atti.getRc());

		model.addAttribute("attivita", atti);
		model.addAttribute("req", new AttivitaReq());
			
		
		return mav;
	}

	@PostMapping("saveAttivita")
	public String saveAttivita(AttivitaReq req) {
		log.debug("saveAttivita:" + req);
		
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "attivita/" + operation;	
		
		HttpMethod typeM = "create".equalsIgnoreCase(operation) ? HttpMethod.POST : HttpMethod.PUT;
		
		resp = clientWeb.method(typeM)
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
	
		log.debug(operation + " rc:" + resp.getRc() + " msg:" + resp.getMsg());
		return "redirect:/admin/listAttivita";
		
	}
	
	@GetMapping("removeAttivita")
	public Object removeAttivita(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
		log.debug("removeAttivita :" + id);
		
		AttivitaReq req = new AttivitaReq();
		req.setId(id);
		
		ResponseBase  resp = clientWeb.post()
				.uri("attivita/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		
		log.debug("rc:" + resp.getRc() + " msg:" + resp.getMsg());
	
		if (!resp.getRc()) {
			redirectAttributes.addFlashAttribute("errorMessage", resp.getMsg());
		}

		return "redirect:/admin/listAttivita";
		
	}
	
	@GetMapping("/listUtente")
	public ModelAndView listUtente() {
		ModelAndView mav = new ModelAndView("admin/listUtente");
		
		ResponseList<UtenteDTO> ut = clientWeb.get()
				.uri("utente/list")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseList<UtenteDTO>>() {})
				.block();

		mav.addObject("utente", ut.getDati());
		
		return mav;
	}
	
	@GetMapping("/createUtente")
	public ModelAndView createUtente() {
		ModelAndView mav = new ModelAndView("admin/createUtente");
		mav.addObject("req", new UtenteReq());
		mav.addObject("errorMSG", null);
		
		
		return mav;
	}

	@GetMapping("/updateUtente")
	public ModelAndView updateUtente(@RequestParam Integer id) {
		ModelAndView mav = new ModelAndView("admin/createUtente");
		
		UtenteDTO resp = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("utente/listById")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<UtenteDTO>>() {})
				.block()
				.getDati();
		
		UtenteReq r = UtenteReq.builder()
				.id(resp.getId())
				.userName(resp.getUserName())
				.email(resp.getEmail())
				.role(resp.getRole())
				.pwd(resp.getPwd())
				.build();
		
		mav.addObject("req", r);
		mav.addObject("errorMSG", null);
		
		
		return mav;
	}

	
	
	@PostMapping("/saveUtente")
	public Object saveUtente(@ModelAttribute UtenteReq req) {
		
		log.debug("saveUtente:" + req);
		
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "utente/" + operation;		
		HttpMethod typeM = "create".equalsIgnoreCase(operation) ? HttpMethod.POST : HttpMethod.PUT;
	
			resp = clientWeb.method(typeM)
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(ResponseBase.class)
					.block();			
		
		log.debug(operation + " :" + resp.getRc());
		
		if (!resp.getRc()) {
			ModelAndView mav = new ModelAndView("admin/createUtente");
			mav.addObject("req", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		}
		
		utS.updateUtente(req);
		
		return "redirect:/admin/listUtente";
	}
	
	@GetMapping("removeUtente")
	public Object removeUtente(@RequestParam(required = true) Integer id) {
		log.debug("removeUtente:" + id);
		
		UtenteReq req = new UtenteReq();
		req.setId(id);
		
		ResponseBase resp = clientWeb.post()
				.uri("utente/remove")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
		
		if (!resp.getRc()) {
			ModelAndView mav = new ModelAndView("admin/createUtente");
			mav.addObject("req", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		} 
	
		log.debug("user to remove:" + resp.getMsg());
		
		utS.removeUtente(resp.getMsg());
		
		return "redirect:/admin/listUtente";
		
	}
}
