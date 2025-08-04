package com.betacom.fe.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttivitaDTO {
	private Integer id;
	private String descrizione;
	private BigDecimal prezzo;

}
