package com.app.flashcash.repository;

import com.app.flashcash.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNumber(String accountNumber);

    List<Account> findByUser(Long user);

    boolean existsByAccountNumber(String accountNumber);
}
