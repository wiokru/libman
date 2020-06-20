package com.wiokru.library.repositories;

import com.wiokru.library.entity.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
