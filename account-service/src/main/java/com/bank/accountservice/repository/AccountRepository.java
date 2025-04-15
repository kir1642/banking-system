package com.bank.accountservice.repository;

import com.bank.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Дополнительно можешь добавлять свои методы, например:
    // List<Account> findByOwnerName(String ownerName);

    boolean existsByOwnerName(String ownerName);

}
