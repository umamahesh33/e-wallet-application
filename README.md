# e-wallet-application

E-Wallet-Application is a backend service that enables users to transact money btw registered wallets.

## Tech
- Java
- Spring-Boot
- ORM(Hibernate)
- maven
- redis
- kafka

## Plugins
- Project Lombok
- JPAbuddy

## Project Description
1 This project is inspired though microservices, And divided as four indiviual services <br>
2 UserService, WalletService, TransactionService, NotificationService <br>
3 User always go though the rest end points of UserService module <br>
4 UserService needs authentication to use its service except for user_registration<br>
5 All these services shares required data via kafka messaging and RestTemplate<br>
6 WalletService is used to check balance, user_wallet_info<br>
7 TransactionService is yse to send and add money(for all type of transactions_info also like history of transactions)<br>
8 NotificationService is used to send mails to confirm mailId, To send transaction details.<br>
9 Security is implemented by spring-security
10 Redis used for caching userdata and used for furtherly whenever needed, during login, needs to send userdata to otherservices.

## Setup
>git clone https://github.com/umamahesh33/e-wallet-application.git
- go though the datasource.url in properties file(foreach module) and create those databases locally and then run all four modules

## REST-API documentation
- http://localhost:8000/swagger-ui.html  UserService
- http://localhost:7000/swagger-ui.html  TransactionService
- http://localhost:9000/swagger-ui.html  WalletService
- http://localhost:6000/swagger-ui.html  NotificationService

