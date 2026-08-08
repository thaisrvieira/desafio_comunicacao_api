package com.luizalebs.comunicacao_api.infraestructure.exceptions;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
