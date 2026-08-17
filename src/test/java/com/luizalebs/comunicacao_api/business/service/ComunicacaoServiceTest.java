package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.client.EmailNotificacaoClient;
import com.luizalebs.comunicacao_api.business.mapper.ComunicacaoMapper;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.ComunicacaoNaoEncontradaException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.DadosInvalidosException;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ComunicacaoServiceTest {

    @Mock

    private ComunicacaoRepository repository;

    @Mock
    private ComunicacaoMapper mapper;

    @Mock
    private EmailNotificacaoClient emailClient;

    @InjectMocks

    private ComunicacaoService service;

    @Test
    void deveAgendarComunicacaoComSucessoQuandoModoForEmail() {

        ComunicacaoInDTO dtoEntrada = ComunicacaoInDTO.builder()
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .build();

        ComunicacaoEntity entityEsperada = ComunicacaoEntity.builder()
                .id(1L)
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        ComunicacaoOutDTO dtoSaidaEsperado = ComunicacaoOutDTO.builder()
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .statusEnvio(StatusEnvioEnum.ENVIADO)
                .build();

        when(mapper.paraEntity(dtoEntrada)).thenReturn(entityEsperada);

        when(mapper.paraDTO(entityEsperada)).thenReturn(dtoSaidaEsperado);

        ComunicacaoOutDTO resultado = service.agendarComunicacao(dtoEntrada);

        assertEquals(StatusEnvioEnum.ENVIADO, resultado.getStatusEnvio());

        verify(repository, times(2)).save(entityEsperada);

        verify(emailClient, times(1)).enviarEmailComunicacao(entityEsperada);

    }

    @Test
    void naoDeveEnviarEmailQuandoModoForDiferenteDeEmail() {

        ComunicacaoInDTO dtoEntrada = ComunicacaoInDTO.builder()
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.SMS)
                .build();

        ComunicacaoEntity entityEsperada = ComunicacaoEntity.builder()
                .id(1L)
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.SMS)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        ComunicacaoOutDTO dtoSaidaEsperado = ComunicacaoOutDTO.builder()
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.SMS)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        when(mapper.paraEntity(dtoEntrada)).thenReturn(entityEsperada);
        when(mapper.paraDTO(entityEsperada)).thenReturn(dtoSaidaEsperado);

        ComunicacaoOutDTO resultado = service.agendarComunicacao(dtoEntrada);

        assertEquals(StatusEnvioEnum.PENDENTE, resultado.getStatusEnvio());

        verify(repository, times(1)).save(entityEsperada);

        verify(emailClient, never()).enviarEmailComunicacao(any());
    }

    @Test
    void deveLancarExcecaoQuandoDtoForNulo() {

        assertThrows(DadosInvalidosException.class, () -> {
            service.agendarComunicacao(null);
        });

    }

    @Test
    void deveBuscarStatusComunicacaoComSucesso() {

        String email = "thais@teste.com";

        ComunicacaoEntity entity = ComunicacaoEntity.builder()
                .id(1L)
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        ComunicacaoOutDTO dtoEsperado = ComunicacaoOutDTO.builder()
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        when(repository.findByEmailDestinatario(email)).thenReturn(entity);
        when(mapper.paraDTO(entity)).thenReturn(dtoEsperado);

        ComunicacaoOutDTO resultado = service.buscarStatusComunicacao(email);

        assertEquals(email, resultado.getEmailDestinatario());
        assertEquals(StatusEnvioEnum.PENDENTE, resultado.getStatusEnvio());
    }

    @Test
    void deveLancarExcecaoQuandoComunicacaoNaoEncontradaAoBuscar() {

        String email = "naoexiste@teste.com";

        when(repository.findByEmailDestinatario(email)).thenReturn(null);

        assertThrows(ComunicacaoNaoEncontradaException.class, () -> {
            service.buscarStatusComunicacao(email);
        });
    }

    @Test
    void deveCancelarComunicacaoComSucesso() {

        String email = "thais@teste.com";

        ComunicacaoEntity entity = ComunicacaoEntity.builder()
                .id(1L)
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        ComunicacaoOutDTO dtoEsperado = ComunicacaoOutDTO.builder()
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.CANCELADO)
                .build();

        when(repository.findByEmailDestinatario(email)).thenReturn(entity);
        when(mapper.paraDTO(entity)).thenReturn(dtoEsperado);

        ComunicacaoOutDTO resultado = service.alterarStatusComunicacao(email);

        assertEquals(StatusEnvioEnum.CANCELADO, resultado.getStatusEnvio());

        verify(repository, times(1)).save(entity);

    }

}