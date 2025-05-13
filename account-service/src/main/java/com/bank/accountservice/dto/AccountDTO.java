package com.bank.accountservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Data
public class AccountDTO {
    //private Long id;

    @NotBlank(message = "Имя владельца не может быть пустым")
    private String ownerName;

    @NotNull(message = "Баланс не может быть null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    @NotBlank(message = "Валюта обязательна")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть в формате ISO 4217, например: 'RUB', 'USD'")
    private String currency;
}