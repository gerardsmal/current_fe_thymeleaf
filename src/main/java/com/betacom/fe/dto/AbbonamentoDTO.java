package com.betacom.fe.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AbbonamentoDTO {

	private Integer id;
	private LocalDate dataIscrizione;
	private List<AttivitaDTO> attivita;
}
