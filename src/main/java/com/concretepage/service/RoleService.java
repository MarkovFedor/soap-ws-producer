package com.concretepage.service;

import com.concretepage.entity.Role;
import com.concretepage.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findAll();
        return roles;
    }

    public Role getRoleById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.get();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }
}
