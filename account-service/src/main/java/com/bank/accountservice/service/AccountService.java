package com.bank.accountservice.service;

import com.bank.accountservice.dto.AccountDTO;
import com.bank.accountservice.dto.TransferResponseDTO;
import com.bank.accountservice.factory.TransferResponseFactory;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.Account;
import com.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransferResponseFactory responseFactory;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          TransferResponseFactory responseFactory) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.responseFactory = responseFactory;
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO dto) {
        Account account = accountMapper.accountDTOToAccount(dto);
        if (accountRepository.existsByOwnerName(account.getOwnerName())) {
            throw new IllegalArgumentException("Account with ownerName already exists");
        }
        Account saved = accountRepository.save(account);
        return accountMapper.accountToAccountDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountMapper.toDtoList(accountRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::accountToAccountDTO);
    }

    @Transactional
    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        return accountRepository.findById(id).map(account -> {
            account.setOwnerName(dto.getOwnerName());
            account.setBalance(dto.getBalance());
            account.setCurrency(dto.getCurrency());
            Account updated = accountRepository.save(account);
            return accountMapper.accountToAccountDTO(updated);
        }).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        accountRepository.delete(account);
    }

    @Transactional
    public AccountDTO deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        return accountMapper.accountToAccountDTO(accountRepository.save(account));
    }

    @Transactional
    public AccountDTO withdraw(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountMapper.accountToAccountDTO(accountRepository.save(account));
    }

    @Transactional
    public TransferResponseDTO transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account from = accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account to = accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        return responseFactory.create(from, to, amount);
    }

}
