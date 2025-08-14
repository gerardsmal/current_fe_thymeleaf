package com.betacom.fe.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.betacom.fe.requests.AttivitaReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {
	private WebClient clientWeb;

	public AdminController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
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
	
	
}
