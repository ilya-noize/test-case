# Tasks project Test-Case

## REST - POST,PUT,GET,DELETE <sup>1 2</sup>

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



## Docker-compose

- [docker-compose.yml](docker-compose.yml)
- [Dockerfile](Dockerfile)

### server
- amazoncorretto:17
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

> Request per second (RPS). Счастье одного пользователя нам, конечно же, важно. Но что, если к вам пришел не один, а тысячи пользователей. Сколько запросов в секунду может выдержать ваш сервер и не упасть?