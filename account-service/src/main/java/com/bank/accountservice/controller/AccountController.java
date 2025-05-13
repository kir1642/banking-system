package com.bank.accountservice.controller;

import com.bank.accountservice.dto.AccountDTO;
import com.bank.accountservice.dto.TransferResponseDTO;
import com.bank.accountservice.factory.TransferResponseFactory;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.Account;
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
    private final AccountMapper accountMapper;
    private final TransferResponseFactory responseFactory;

    @Autowired
    public AccountController(AccountService accountService,
                             AccountMapper accountMapper,
                             TransferResponseFactory responseFactory) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.responseFactory = responseFactory;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        Account account = accountMapper.accountDTOToAccount(accountDTO);
        Account created = accountService.createAccount(account);
        AccountDTO responseDTO = accountMapper.accountToAccountDTO(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountDTO> dtoList = accountMapper.toDtoList(accounts);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(accountMapper::accountToAccountDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт с id " + id + " не найден"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO updatedDTO) {
        Account updatedAccount = accountMapper.accountDTOToAccount(updatedDTO);
        Account savedAccount = accountService.updateAccount(id, updatedAccount);
        AccountDTO responseDTO = accountMapper.accountToAccountDTO(savedAccount);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<AccountDTO> deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Account updated = accountService.deposit(id, amount);
        AccountDTO responseDTO = accountMapper.accountToAccountDTO(updated);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Account updated = accountService.withdraw(id, amount);
        AccountDTO responseDTO = accountMapper.accountToAccountDTO(updated);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/transfer")
    @Transactional
    public ResponseEntity<TransferResponseDTO> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        Account from = accountService.withdraw(fromAccountId, amount);
        Account to = accountService.deposit(toAccountId, amount);

        TransferResponseDTO response = responseFactory.create(from, to, amount);
        return ResponseEntity.ok(response);
    }
}
