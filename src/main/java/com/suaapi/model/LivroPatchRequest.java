package com.suaapi.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Corpo aceito pelo PATCH: todos os campos são opcionais.
 * Só os campos enviados (diferentes de null) são alterados no livro.
 * As validações abaixo só rodam quando o campo é informado.
 */
public class LivroPatchRequest {

    @Pattern(regexp = ".*\\S.*", message = "O título não pode ser vazio")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    private String titulo;

    @Pattern(regexp = ".*\\S.*", message = "O autor não pode ser vazio")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    private String autor;

    @Pattern(regexp = "\\d{10}|\\d{13}", message = "O ISBN deve ter 10 ou 13 dígitos numéricos")
    private String isbn;

    @Min(value = 1000, message = "O ano de publicação deve ser no mínimo 1000")
    @Max(value = 2100, message = "O ano de publicação deve ser no máximo 2100")
    private Integer anoPublicacao;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
}
