package com.suaapi.exception;

/** Lançada quando não existe livro com o ID informado (vira HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(Long id) {
        super("Livro com id " + id + " não encontrado");
    }
}
