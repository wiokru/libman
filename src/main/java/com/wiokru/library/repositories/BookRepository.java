package com.wiokru.library.repositories;

import com.wiokru.library.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findAll();
    List<Book> findAllByTitle(String title);
    List<Book> findAllByPublisher(String publisher);
}
