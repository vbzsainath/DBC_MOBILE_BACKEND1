package com.vbz.dbcards.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.PasswordReset;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.entity.UserLdap;

public interface UserRepository extends JpaRepository<User, Long> {



	boolean existsByEmail(String email);

	Optional<User> findByEmailAndMobileNumber(String email, Long mobileNumber);

	Optional<User> findByMobileNumber(Long mobileNumber);

	Optional<User> findByEmail(String email);

	boolean existsByMobileNumber(Long mobile);
	

	

	

	
	

	

	

	

	
}
