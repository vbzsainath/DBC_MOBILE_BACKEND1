package com.vbz.dbcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.UserLogin;

public interface UserLoginRepository extends JpaRepository<UserLogin,Long> {
	
}
