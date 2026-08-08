package com.luizalebs.comunicacao_api.infraestructure.exceptions;

import com.luizalebs.comunicacao_api.api.dto.ErroPadraoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComunicacaoNaoEncontradaException.class)
    public ResponseEntity<ErroPadraoDTO> handleComunicacaoNaoEncontrada(ComunicacaoNaoEncontradaException ex) {

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro(HttpStatus.NOT_FOUND.getReasonPhrase())
                .mensagem(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroPadraoDTO> handleDadosInvalidos(DadosInvalidosException ex) {

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .mensagem(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> handleValidacao(MethodArgumentNotValidException ex) {

        String mensagens = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .mensagem(mensagens)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDTO> handleGeral(Exception ex) {

        ErroPadraoDTO erro = ErroPadraoDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .mensagem("Ocorreu um erro inesperado: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}