package com.springboot.imgur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.imgur.responsedata.RoleName;
import com.springboot.imgur.responsedata.Role;

@Repository
public interface RoleRepository extends  JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
       
}