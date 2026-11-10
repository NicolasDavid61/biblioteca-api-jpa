package com.suaapi.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Centraliza o tratamento de erros: converte exceções em respostas HTTP padronizadas. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));

        ErroResposta corpo = ErroResposta.de(400, "Bad Request", "Dados inválidos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        ErroResposta corpo = ErroResposta.de(400, "Bad Request",
                "Corpo da requisição ausente ou com JSON inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        ErroResposta corpo = ErroResposta.de(400, "Bad Request",
                "O valor informado para '" + ex.getName() + "' é inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroResposta corpo = ErroResposta.de(404, "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> tratarConflito(ConflitoException ex) {
        ErroResposta corpo = ErroResposta.de(409, "Conflict", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }

    // Rede de segurança: se a restrição UNIQUE do banco for violada, também retorna 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarIntegridade(DataIntegrityViolationException ex) {
        ErroResposta corpo = ErroResposta.de(409, "Conflict",
                "A operação viola uma restrição de dados únicos (ISBN já cadastrado)");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }
}
