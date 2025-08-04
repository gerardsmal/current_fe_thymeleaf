package com.betacom.fe.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RicevutaDTO {
	private String nome;
	private String cognome;
	private BigDecimal totale;
	List<AttivitaDTO> riga;
}
