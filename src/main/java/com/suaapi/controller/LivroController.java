package com.suaapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.suaapi.exception.ConflitoException;
import com.suaapi.exception.RecursoNaoEncontradoException;
import com.suaapi.model.Livro;
import com.suaapi.model.LivroPatchRequest;
import com.suaapi.repository.LivroRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroRepository repository;

    public LivroController(LivroRepository repository) {
        this.repository = repository;
    }

    // GET /api/livros ou GET /api/livros?titulo=casmurro -> 200 OK
    @GetMapping
    public ResponseEntity<List<Livro>> listar(@RequestParam(required = false) String titulo) {
        List<Livro> livros = (titulo == null || titulo.isBlank())
                ? repository.findAll()
                : repository.findByTituloContainingIgnoreCase(titulo);
        return ResponseEntity.ok(livros);
    }

    // GET /api/livros/{id} -> 200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarOuFalhar(id));
    }

    // POST /api/livros -> 201 Created, 400 Bad Request ou 409 Conflict
    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        if (repository.existsByIsbn(livro.getIsbn())) {
            throw new ConflitoException("Já existe um livro cadastrado com o ISBN " + livro.getIsbn());
        }

        livro.setId(null); // garante que o banco gere o ID
        Livro salvo = repository.save(livro);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    // PUT /api/livros/{id} -> 200 OK, 400, 404 ou 409 (substitui todos os campos)
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        Livro livro = buscarOuFalhar(id);
        validarIsbnUnico(dados.getIsbn(), id);

        livro.setTitulo(dados.getTitulo());
        livro.setAutor(dados.getAutor());
        livro.setIsbn(dados.getIsbn());
        livro.setAnoPublicacao(dados.getAnoPublicacao());

        return ResponseEntity.ok(repository.save(livro));
    }

    // PATCH /api/livros/{id} -> 200 OK, 400, 404 ou 409 (altera só os campos enviados)
    @PatchMapping("/{id}")
    public ResponseEntity<Livro> atualizarParcialmente(@PathVariable Long id,
                                                       @Valid @RequestBody LivroPatchRequest dados) {
        Livro livro = buscarOuFalhar(id);

        if (dados.getIsbn() != null) {
            validarIsbnUnico(dados.getIsbn(), id);
            livro.setIsbn(dados.getIsbn());
        }
        if (dados.getTitulo() != null) {
            livro.setTitulo(dados.getTitulo());
        }
        if (dados.getAutor() != null) {
            livro.setAutor(dados.getAutor());
        }
        if (dados.getAnoPublicacao() != null) {
            livro.setAnoPublicacao(dados.getAnoPublicacao());
        }

        return ResponseEntity.ok(repository.save(livro));
    }

    // DELETE /api/livros/{id} -> 204 No Content ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        repository.delete(buscarOuFalhar(id));
        return ResponseEntity.noContent().build();
    }

    private Livro buscarOuFalhar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(id));
    }

    // Permite manter o próprio ISBN, mas não usar o de outro livro
    private void validarIsbnUnico(String isbn, Long idAtual) {
        if (repository.existsByIsbnAndIdNot(isbn, idAtual)) {
            throw new ConflitoException("Já existe outro livro cadastrado com o ISBN " + isbn);
        }
    }
}
