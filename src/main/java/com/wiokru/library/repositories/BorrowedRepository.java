package com.wiokru.library.repositories;

import com.wiokru.library.entity.Borrowed;
import com.wiokru.library.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BorrowedRepository extends CrudRepository<Borrowed, Long> {
    List<Borrowed> findAllByUser(User user);
}
