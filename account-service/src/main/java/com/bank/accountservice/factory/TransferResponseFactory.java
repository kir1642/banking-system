package com.bank.accountservice.factory;

import com.bank.accountservice.dto.TransferResponseDTO;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransferResponseFactory {

    @Autowired
    private AccountMapper accountMapper;

    public TransferResponseDTO create(Account from, Account to, BigDecimal amount) {
        return new TransferResponseDTO(
                accountMapper.accountToAccountDTO(from),
                accountMapper.accountToAccountDTO(to),
                amount,
                "Transfer successful",
                LocalDateTime.now()
        );
    }
}
