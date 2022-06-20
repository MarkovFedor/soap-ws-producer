package com.concretepage.repository;

import com.concretepage.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findRoleByName(String name);
}
