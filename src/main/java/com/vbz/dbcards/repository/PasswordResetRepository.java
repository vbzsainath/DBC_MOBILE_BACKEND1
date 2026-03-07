package com.vbz.dbcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.PasswordReset;

public interface PasswordResetRepository
        extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset>
    findTopByUsernameAndResetPinAndUsedFalseOrderByCreatedOnDesc(
            String username, String resetPin);

    Optional<PasswordReset>
    findTopByUsernameAndUsedFalseOrderByCreatedOnDesc(String username);
}
