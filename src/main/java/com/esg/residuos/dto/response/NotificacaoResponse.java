package com.esg.residuos.dto.response;

import com.esg.residuos.domain.enums.Prioridade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoResponse {
    private Long id;
    private Long idPontoColeta;
    private String nomePontoColeta;
    private Long idColeta;
    private String tipo;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataNotificacao;
    private Boolean lida;
    private Prioridade prioridade;
}
