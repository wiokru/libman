package com.wiokru.library.repositories;

import com.wiokru.library.entity.Book;
import com.wiokru.library.entity.Borrowed;
import com.wiokru.library.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowedRepository extends CrudRepository<Borrowed, Long> {
    List<Borrowed> findAllByUser(User user);
    Optional<Borrowed> findByBook(Book book);
    List<Borrowed> findAll();
}
