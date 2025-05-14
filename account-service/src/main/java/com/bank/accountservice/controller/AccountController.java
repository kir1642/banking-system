package com.bank.accountservice.controller;

import com.bank.accountservice.dto.AccountDTO;
import com.bank.accountservice.dto.TransferResponseDTO;
import com.bank.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO createdDTO = accountService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);

    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> dtoList = accountService.getAllAccounts();
        return ResponseEntity.ok(dtoList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт с id " + id + " не найден"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO updatedDTO) {
        AccountDTO updated = accountService.updateAccount(id, updatedDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<AccountDTO> deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        AccountDTO updated = accountService.deposit(id, amount);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        AccountDTO updated = accountService.withdraw(id, amount);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/transfer")
    @Transactional
    public ResponseEntity<TransferResponseDTO> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        TransferResponseDTO responseDTO = accountService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok(responseDTO);
    }

}
