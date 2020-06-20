package com.wiokru.library.repositories;

import com.wiokru.library.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository <User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long Id);
    List<User> findAll();

}
