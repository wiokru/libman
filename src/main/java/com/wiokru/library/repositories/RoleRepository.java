package com.wiokru.library.repositories;

import com.wiokru.library.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
    List<Role> findAll();
    Optional<Role> findById(Long id);
}
