package com.vbz.dbcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {

}
