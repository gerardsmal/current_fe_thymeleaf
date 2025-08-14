package com.betacom.fe.requests;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttivitaReq {
	private Integer id;
	private String descrizione;
	private BigDecimal prezzo;
	private Integer abbonamentiId;
	private Integer socioId;
}
