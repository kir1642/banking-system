# Banking System 🏦

Микросервисный проект на Java с использованием Spring Boot и Docker.  
Реализует базовую логику банковского аккаунта.

## 📦 Модули

- `account-service` — сервис управления аккаунтами
- В планах: `transaction-service`, `fraud-detector`, `auth-service`, `api-gateway`, `analytics-service`, и др.

## ⚙️ Стек технологий

- Java 17
- Spring Boot 3
- Maven
- PostgreSQL
- Docker / Docker Compose

## 🚀 Как запустить

```bash
docker-compose up
```

Приложение по умолчанию работает на порту 8081.

## 🔧 Примеры API
Пополнение счёта: 

PATCH /accounts/{id}/deposit?amount=100.00

Снятие средств:

PATCH /accounts/{id}/withdraw?amount=50.00

Перевод между счетами:

PATCH /accounts/transfer?fromId=1&toId=2&amount=100.00

