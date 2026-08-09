package com.luizalebs.comunicacao_api.business.client;

import com.luizalebs.comunicacao_api.business.client.dto.TarefasDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Date;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor

public class EmailNotificacaoClient {

    private final RestTemplate restTemplate;

    @Value("${email.api.url}")
    private String emailApiUrl;

    public void enviarEmailComunicacao(ComunicacaoEntity entity){

        TarefasDTO dto = TarefasDTO.builder()
                .id(String.valueOf(entity.getId()))
                .nomeTarefa("Comunicação #" + entity.getId())
                .descricao(entity.getMensagem())
                .dataCriacao(LocalDateTime.now())
                .dataEvento(converterParaLocalDateTime(entity.getDataHoraenvio()))
                .emailUsuario(entity.getEmailDestinatario())
                .statusNotificacaoEnum("PENDENTE")
                .build();

        restTemplate.postForEntity(emailApiUrl, dto, Void.class);
    }

    private LocalDateTime converterParaLocalDateTime(Date data){
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
