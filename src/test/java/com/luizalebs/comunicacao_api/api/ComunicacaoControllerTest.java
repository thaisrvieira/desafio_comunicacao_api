package com.luizalebs.comunicacao_api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(ComunicacaoController.class)
public class ComunicacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunicacaoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveAgendarComunicacaoComSucesso() throws Exception {

        ComunicacaoInDTO dtoEntrada = ComunicacaoInDTO.builder()
                .dataHoraEnvio(new Date())
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .build();

        ComunicacaoOutDTO dtoSaida = ComunicacaoOutDTO.builder()
                .nomeDestinatario("Thais")
                .emailDestinatario("thais@teste.com")
                .mensagem("Mensagem de teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .statusEnvio(StatusEnvioEnum.ENVIADO)
                .build();

        when(service.agendarComunicacao(any(ComunicacaoInDTO.class))).thenReturn(dtoSaida);

        mockMvc.perform(post("/comunicacao/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusEnvio").value("ENVIADO"));
    }

    @Test
    void deveBuscarStatusComSucesso() throws Exception {

        String email = "thais@teste.com";

        ComunicacaoOutDTO dtoSaida = ComunicacaoOutDTO.builder()
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        when(service.buscarStatusComunicacao(email)).thenReturn(dtoSaida);

        mockMvc.perform(get("/comunicacao")
                        .param("emailDestinatario", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailDestinatario").value(email));
    }

    @Test
    void deveCancelarComSucesso() throws Exception {

        // Arrange
        String email = "thais@teste.com";

        ComunicacaoOutDTO dtoSaida = ComunicacaoOutDTO.builder()
                .emailDestinatario(email)
                .statusEnvio(StatusEnvioEnum.CANCELADO)
                .build();

        when(service.alterarStatusComunicacao(email)).thenReturn(dtoSaida);

        // Act + Assert
        mockMvc.perform(patch("/comunicacao/cancelar")
                        .param("emailDestinatario", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusEnvio").value("CANCELADO"));
    }



}