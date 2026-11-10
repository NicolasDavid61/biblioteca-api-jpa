package com.suaapi.repository;

import com.suaapi.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * O Spring Data JPA gera a implementação desta interface sozinho.
 * Os métodos abaixo viram consultas SQL a partir do próprio nome.
 */
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
