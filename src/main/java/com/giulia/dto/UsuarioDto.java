package com.giulia.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter@Setter
public class UsuarioDto {

	private String email;
	private String nome;
	private String senha;
	
}
