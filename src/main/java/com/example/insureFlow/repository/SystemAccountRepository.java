package com.example.insureFlow.repository;

import com.example.insureFlow.entity.SystemAccount;
import com.example.insureFlow.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {

    
    Optional<SystemAccount> findByEmail(String email);
    boolean existsByEmail(String email);

    
    List<SystemAccount> findByRole(UserRole role);

    
    List<SystemAccount> findByRoleAndIsActiveTrue(UserRole role);
}