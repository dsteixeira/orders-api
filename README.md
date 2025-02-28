
# Orders API

API simples de importação de pedidos de usuários e exposição de dados pra integração.



## Stack utilizada

**Back-end:** Java 21 e Spring Boot 3.4.3

**Banco de dados:** MySQL 8.0

## Configuração prévia

Instalar Java 21 ou superior

Instalar e configurar Maven

Instalar Docker (Podman ou Colima)


## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/dsteixeira/orders-api.git
```

Entre no diretório do projeto

```bash
  cd orders-api
```

Instale as dependências

```bash
  mvn clean
```

Inicie o banco de dados com o docker-compose

```bash
  cd orders-api/docker-compose
  docker-compose up -d
```

Iniciando o serviço na pasta principal do projeto
```bash
  mvn spring-boot:run
```

O serviço será iniciado na porta 8001 e criará automaticamente via Flyway as tabelas no banco de dados.
## Rodando os testes

Para rodar os testes, rode o seguinte comando

```bash
  mvn clean test
```

Relatório de Testes (Jacoco) disponível em: /orders-api/target/site/jacoco/index.html

## Documentação da API

Com a aplicação rodando, acesse: http://localhost:8001/orders-api/v1/swagger-ui/index.html


## Autor

- [@dsteixeira](https://www.github.com/dsteixeira)