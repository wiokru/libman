package com.wiokru.library.repositories;

import com.wiokru.library.entity.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    List<Author> findAll();
    Optional<Author> findById(Long id);
}
