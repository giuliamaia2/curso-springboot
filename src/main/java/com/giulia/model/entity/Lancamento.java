package com.giulia.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.giulia.model.enums.StatusLancamento;
import com.giulia.model.enums.TipoLancamento;

import lombok.Data;

@Entity
@Table(name = "lancamento", schema = "financas")
@Data
public class Lancamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "descricao")
	private String descricao;

	@Column(name = "mes")
	private Integer mes;

	@Column(name = "ano")
	private Integer ano;

	// Relacionamento many-atual one-entidade relacionada
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@Column(name = "valor")
	private BigDecimal valor;

	@Column(name = "data_cadastro")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class) // padrão de conversão
	private LocalDate dataCadastro;

	@Column(name = "tipo")
	@Enumerated(value = EnumType.STRING) // isso é um check gravado pelo valor (string)
	private TipoLancamento tipo;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING) // isso é um check gravado pelo valor (string)
	private StatusLancamento status;	
}
