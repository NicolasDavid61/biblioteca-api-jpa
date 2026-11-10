package com.suaapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entidade única da aplicação: representa um livro do acervo.
 * Cada objeto desta classe vira uma linha da tabela "livros" no banco H2.
 */
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String autor;

    @NotBlank(message = "O ISBN é obrigatório")
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "O ISBN deve ter 10 ou 13 dígitos numéricos")
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @NotNull(message = "O ano de publicação é obrigatório")
    @Min(value = 1000, message = "O ano de publicação deve ser no mínimo 1000")
    @Max(value = 2100, message = "O ano de publicação deve ser no máximo 2100")
    @Column(nullable = false)
    private Integer anoPublicacao;

    public Livro() {
    }

    public Livro(String titulo, String autor, String isbn, Integer anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
