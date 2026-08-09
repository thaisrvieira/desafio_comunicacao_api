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
Ao ser agendada, a comunicação recebe automaticamente o status `PENDENTE`.

- **GET /comunicacao?emailDestinatario={email}**: Retorna o status atual da comunicação cadastrada para o e-mail informado.

- **PATCH /comunicacao/cancelar?emailDestinatario={email}**: Altera o status da comunicação para `CANCELADO`.