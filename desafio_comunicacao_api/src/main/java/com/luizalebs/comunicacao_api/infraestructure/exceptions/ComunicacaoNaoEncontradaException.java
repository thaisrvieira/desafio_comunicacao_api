package com.luizalebs.comunicacao_api.infraestructure.exceptions;

public class ComunicacaoNaoEncontradaException extends RuntimeException{

    public ComunicacaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
