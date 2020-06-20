package com.wiokru.library.repositories;

import com.wiokru.library.entity.Reserved;
import com.wiokru.library.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservedRepository extends CrudRepository<Reserved, Long> {
    List<Reserved> findAll();
    List<Reserved> findAllByUser(User u);
}
