package com.wiokru.library.repositories;

import com.wiokru.library.entity.BookCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookCategoryRepository extends CrudRepository<BookCategory, Long> {
    List<BookCategory> findAll();
    Optional<BookCategory> findById(Long id);
}
