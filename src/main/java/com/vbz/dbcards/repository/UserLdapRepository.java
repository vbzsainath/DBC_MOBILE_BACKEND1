package com.vbz.dbcards.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vbz.dbcards.entity.UserLdap;

public interface UserLdapRepository extends JpaRepository<UserLdap, Long> {

	


	boolean existsByEmail(String email);

	Optional<UserLdap> findByEmail(String email);



//    Optional<UserLdap> findByUsername(String username);

	
}
