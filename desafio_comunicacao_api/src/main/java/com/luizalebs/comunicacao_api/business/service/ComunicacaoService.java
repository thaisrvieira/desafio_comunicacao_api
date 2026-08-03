package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.client.EmailNotificacaoClient;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final ComunicacaoConverter converter;
    private final EmailNotificacaoClient emailClient;

    public ComunicacaoService(ComunicacaoRepository repository, ComunicacaoConverter converter, EmailNotificacaoClient emailClient) {
        this.repository = repository;
        this.converter = converter;
        this.emailClient = emailClient;
    }

    public ComunicacaoOutDTO agendarComunicacao(ComunicacaoInDTO dto) {
        if (Objects.isNull(dto)) {
            throw new RuntimeException();
        }
        dto.setStatusEnvio(StatusEnvioEnum.PENDENTE);
        ComunicacaoEntity entity = converter.paraEntity(dto);
        repository.save(entity);

        if (entity.getModoDeEnvio() == ModoEnvioEnum.EMAIL) {
            emailClient.enviarEmailComunicacao(entity);
            entity.setStatusEnvio(StatusEnvioEnum.ENVIADO);
            repository.save(entity);
        }

        ComunicacaoOutDTO outDTO = converter.paraDTO(entity);
        return outDTO;
    }

    public ComunicacaoOutDTO buscarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO alterarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        entity.setStatusEnvio(StatusEnvioEnum.CANCELADO);
        repository.save(entity);
        return (converter.paraDTO(entity));
    }

}
