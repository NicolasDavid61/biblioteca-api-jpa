package com.suaapi.exception;

/** Lançada quando a operação viola uma regra de dados únicos, como ISBN repetido (vira HTTP 409). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
