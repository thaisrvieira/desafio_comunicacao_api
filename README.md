# Comunicação API

API desenvolvida como desafio técnico do curso Javanauta, baseada em um exercício real de processo seletivo da Luiza Labs. A aplicação gerencia o agendamento de comunicações (e-mail, SMS, push, WhatsApp) para destinatários, com integração a uma API externa de notificação por e-mail.

## Tecnologias utilizadas

- Java 11
- Spring Boot 2.7
- Spring Data JPA + MySQL
- MapStruct (conversão DTO ↔ Entity)
- Bean Validation
- Springdoc OpenAPI (Swagger)
- Docker + Docker Compose
- JUnit 5 + Mockito (testes unitários)
- GitHub Actions (CI)

## Como rodar o projeto

### Opção 1: Via Docker (recomendado)

Pré-requisito: Docker instalado.

```bash
docker compose up --build
```

Isso sobe a aplicação (porta 8080) e o banco MySQL (porta 3306) já configurados e conectados entre si.

### Opção 2: Localmente

Pré-requisitos: Java 11, Maven, e um MySQL rodando na porta 3306 (pode ser via container: `docker run --name mysql-comunicacao -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=comunicacao1 -p 3306:3306 -d mysql:8.0`).

```bash
mvn spring-boot:run
```

### Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Integração com API externa

Quando uma comunicação é agendada com `modoDeEnvio: EMAIL`, esta aplicação realiza uma chamada HTTP para uma API externa de notificação, responsável pelo envio efetivo do e-mail (via SMTP + templates Thymeleaf).

Repositório da API de notificação: [thaisrvieira/notificacao](https://github.com/thaisrvieira/notificacao)

## Endpoints da Aplicação

- **POST /comunicacao/agendar**: Agenda uma nova comunicação para envio. O corpo da requisição deve ser preenchido com:
```json
  {
    "dataHoraEnvio": "yyyy-MM-dd HH:mm:ss",
    "nomeDestinatario": "",
    "emailDestinatario": "",
    "telefoneDestinatario": "",
    "mensagem": "",
    "modoDeEnvio": "EMAIL | SMS | PUSH | WHATSAPP"
  }
```
Ao ser agendada, a comunicação recebe automaticamente o status `PENDENTE`. Quando `modoDeEnvio` é `EMAIL`, a aplicação também dispara uma chamada para a API externa de notificação, atualizando o status para `ENVIADO` em caso de sucesso.

- **GET /comunicacao?emailDestinatario={email}**: Retorna o status atual da comunicação cadastrada para o e-mail informado.

- **PATCH /comunicacao/cancelar?emailDestinatario={email}**: Altera o status da comunicação para `CANCELADO`.

## Tratamento de erros

A API centraliza o tratamento de exceções através de um `GlobalExceptionHandler`, retornando respostas padronizadas no seguinte formato:

```json
{
  "timestamp": "2026-08-17 19:00:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Nenhuma comunicação encontrada para o e-mail: exemplo@teste.com"
}
```

| Cenário | Status HTTP |
|---|---|
| Comunicação não encontrada | 404 |
| Dados inválidos / campos obrigatórios ausentes | 400 |
| Erro inesperado | 500 |

## Testes

O projeto conta com testes unitários para as camadas Controller e Service, utilizando JUnit 5 e Mockito. Para rodar:

```bash
mvn test
```