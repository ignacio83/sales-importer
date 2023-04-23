# Backend

## Guia do desenvolvedor

Construindo a aplicação:

```shell
./gradlew build
```

Executando os testes unitários:

```shell
./gradlew test
```

Executando os testes de integração:

```shell
./gradlew integrationTest
```

Executando os testes de componente:

```shell
./gradlew componentTest
```

Iniciando o banco de dados com docker pela primeira vez:
```shell
(cd .. && make run-database)
```

Iniciando o banco de dados com docker:
```shell
(cd .. && make start-database)
```

Desligando o banco de dados com docker:
```shell
(cd .. && make stop-database)
```