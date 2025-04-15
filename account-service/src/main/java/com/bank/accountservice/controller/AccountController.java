package com.bank.accountservice.controller;

import com.bank.accountservice.model.Account;
import com.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;



import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Создание нового аккаунта
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account) {
        boolean exists = accountRepository.existsByOwnerName(account.getOwnerName());

        if (exists) {
            return ResponseEntity
                    .badRequest()
                    .body("Account with ownerName '" + account.getOwnerName() + "' already exists.");
        }

        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }


    // Получение списка всех аккаунтов
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        return accountRepository.findById(id)
                .map(account -> {
                    account.setOwnerName(updatedAccount.getOwnerName());
                    account.setBalance(updatedAccount.getBalance());
                    account.setCurrency(updatedAccount.getCurrency());
                    accountRepository.save(account);
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(account -> {
                    accountRepository.delete(account);
                    return ResponseEntity.noContent().<Void>build(); // 👈 тут тоже указали <Void>
                })
                .orElse(ResponseEntity.notFound().<Void>build()); // 👈 и здесь
    }

    // пополнение счета
    @PatchMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build(); // нельзя пополнять на 0 или отрицательное значение
        }

        return accountRepository.findById(id)
                .map(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    accountRepository.save(account);
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // снятие со счета
    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().<Account>build(); // Явно указываем тип
        }

        return accountRepository.findById(id)
                .map(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        return ResponseEntity.badRequest().<Account>build(); // и тут
                    }
                    account.setBalance(account.getBalance().subtract(amount));
                    accountRepository.save(account);
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().<Account>build()); // и тут
    }

    // перевод со счета на счет
    @PatchMapping("/transfer")
    @Transactional
    public ResponseEntity<String> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Transfer amount must be greater than 0");
        }

        if (fromAccountId.equals(toAccountId)) {
            return ResponseEntity.badRequest().body("Cannot transfer to the same account");
        }

        Optional<Account> fromOpt = accountRepository.findById(fromAccountId);
        Optional<Account> toOpt = accountRepository.findById(toAccountId);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account from = fromOpt.get();
        Account to = toOpt.get();

        if (from.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds on the source account");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        return ResponseEntity.ok("Transfer successful");
    }



}
