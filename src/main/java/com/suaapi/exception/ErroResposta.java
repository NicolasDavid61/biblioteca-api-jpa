package com.suaapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato padrão das respostas de erro da API. O campo "campos" só vem preenchido nos erros de validação. */
public record ErroResposta(
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos,
        LocalDateTime timestamp) {

    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(status, erro, mensagem, null, LocalDateTime.now());
    }

    public static ErroResposta de(int status, String erro, String mensagem, Map<String, String> campos) {
        return new ErroResposta(status, erro, mensagem, campos, LocalDateTime.now());
    }
}
