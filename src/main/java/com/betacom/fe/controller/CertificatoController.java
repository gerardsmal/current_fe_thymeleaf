package com.betacom.fe.controller;

import java.time.LocalDate;

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
import com.betacom.fe.requests.CertificatoReq;
import com.betacom.fe.requests.SocioReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class CertificatoController {
	private WebClient clientWeb;

	public CertificatoController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}

	
	@GetMapping("listCertificato")
	public ModelAndView listCertificato(@RequestParam Integer id) {
		log.debug("listCertificato id:" + id);
		ModelAndView mav = new ModelAndView("listCertificato");
		SocioDTO soc = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("socio/getSocio")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
				.block()
				.getDati();
		
		
		CertificatoReq req = new CertificatoReq();
		if (soc.getCertificato() != null) {
			req.setId(soc.getCertificato().getId());
			req.setSocioId(id);
			req.setDataCertificato(soc.getCertificato().getDataCertificato());
			req.setDataCertificatoString(soc.getCertificato().getDataCertificato().toString());
			req.setTipo(soc.getCertificato().getTipo());
			req.setTipoSelect((soc.getCertificato().getTipo() ? 1 : 2));
		} else {
			req.setSocioId(id);		
		}
		
		mav.addObject("certificato", req);
		
		
		return mav;
	}
	
	@PostMapping("saveCertificato")
	public String saveCertificato(@ModelAttribute("certificato") CertificatoReq req) {
		req.setTipo((req.getTipoSelect() == 1) ? true : false);
		req.setDataCertificato(LocalDate.parse(req.getDataCertificatoString()));

		log.debug("saveCertificato:" + req);
		
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "certificato/" + operation;	
		
		HttpMethod typeM = "create".equalsIgnoreCase(operation) ? HttpMethod.POST : HttpMethod.PUT;
		
		resp = clientWeb.method(typeM)
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
		
		log.debug(operation + " rc:" + resp.getRc());
		
		
		
		return "redirect:/listSocio";
	}
	
}
