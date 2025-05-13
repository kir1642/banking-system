package com.bank.accountservice.mapper;

import com.bank.accountservice.dto.AccountDTO;
import com.bank.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "ownerName", target = "ownerName")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "currency", target = "currency")
    AccountDTO accountToAccountDTO(Account account);

    @Mapping(source = "ownerName", target = "ownerName")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "currency", target = "currency")
    Account accountDTOToAccount(AccountDTO accountDTO);

    List<AccountDTO> toDtoList(List<Account> accounts);
}