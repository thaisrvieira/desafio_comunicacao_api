package com.luizalebs.comunicacao_api.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ComunicacaoInDTO implements Serializable {

    @NotNull(message = "A data e hora de envio são obrigatórias.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataHoraEnvio;

    @NotBlank(message = "O nome do destinatário é obrigatório.")
    private String nomeDestinatario;

    @NotBlank(message = "O e-mail do destinatário é obrigatório.")
    private String emailDestinatario;

    private String telefoneDestinatario;
    // ↑ sem validação, continua opcional

    @NotBlank(message = "A mensagem é obrigatória.")
    private String mensagem;

    @NotNull(message = "O modo de envio é obrigatório.")
    private ModoEnvioEnum modoDeEnvio;

    @JsonIgnore
    private StatusEnvioEnum statusEnvio;

}
