package com.vbz.dbcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster,Long> {

	RoleMaster findByRoleName(String string);

}
