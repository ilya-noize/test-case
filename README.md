# Tasks project Test-Case

## REST - POST,PUT,GET,DELETE <sup>1 2</sup>

### POST /api/v1/wallet

#### Request
> JSON
> {
> "balance":0
> }
#### Responce
> JSON
> {
> "walletId":"98c8fe7a-524e-492c-bfd5-db98417749f2",
> "balance":0
> }
**HttpStatus**: **201**

### PUT /api/v1/wallet

#### Request
> JSON
> {
> "walletId": "98c8fe7a-524e-492c-bfd5-db98417749f2",
> "operationType":"DEPOSIT" or "WITHDRAW",
> "amount": 1000
> }
#### Responce
> JSON
> {
> "walletId":"98c8fe7a-524e-492c-bfd5-db98417749f2",
> "balance":1000
> }
**HttpStatus**: **200**

### GET /api/v1/wallets/98c8fe7a-524e-492c-bfd5-db98417749f2

#### Responce
> JSON
> {
> "walletId":"98c8fe7a-524e-492c-bfd5-db98417749f2",
> "balance":1000
> }

**HttpStatus:** **200**

### DELETE /api/v1/wallets/98c8fe7a-524e-492c-bfd5-db98417749f2

> **HttpStatus**: **200**

### Обработка ошибок

#### WalletBalancePaymentsException.java
- Баланс счёта во время создание кошелька должна быть не отрицательной
- Сумма для операции должна быть положительной
- Средств должно быть достаточно для снятия

#### WalletNotFoundException.java
- Кошелёк должен существовать при проведении по нему фин. операций
- Кошелёк должен существовать при запросе баланса
- Кошелёк должен существовать при удалении кошелька

## Liquibase

[db.changelog-master.yaml](src/main/resources/db/changelog/db.changelog-master.yaml)

## Проблемы при работе в конкурентной среде <sup>3<sup>

Обратите особое внимание проблемам при работе в конкурентной среде
(1000 RPS по одному кошельку)

## Docker-compose

- [docker-compose.yml](docker-compose.yml)
- [Dockerfile](Dockerfile)

### server
- eclipse-temurin:17-jre-alpine
- ports: 9090:9090
- depends: db
- build: Dockerfile

### db
- postgres:16.0-alpine3.18
- ports: 15432:5432

### pgAdmin
- dpage/pgadmin4:7
- ports: 5050:80


# Notes
<div><sup><b> 1 - эндпоинты должны быть покрыты тестами.</b></sup></div>

<div><sup><b> 2 - Каждый запрос должен быть обработан (нет 50Х error)</b></sup></div>

<div><sup><b> 3 - 1000 RPS по одному кошельку</b>
</sup></div>

> Request per second (RPS) - Запрос в секунду.