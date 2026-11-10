# Biblioteca API

Web API RESTful para cadastro de livros, com CRUD completo, validações e persistência em banco de dados H2.

**Tecnologias:** Java 17, Spring Boot, Spring Web, Spring Data JPA, Bean Validation, H2 Database e Maven.

## Como compilar e rodar (linha de comando)

Pré-requisitos: **JDK 17 ou superior** instalado (confira com `java -version`).

Na pasta do projeto (onde está o `pom.xml`):

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run

# Se você tiver o Maven instalado
mvn spring-boot:run
```

Quando o terminal mostrar `Started BibliotecaApiApplication`, a API está no ar em `http://localhost:8080`.

Para apenas compilar: `./mvnw clean package` (o `.jar` fica em `target/`).

### Banco de dados

Os dados ficam em um banco **H2 em arquivo**, na pasta `data/` (criada automaticamente na primeira execução). Por isso os livros continuam salvos depois de reiniciar o servidor. A tabela `livros` é criada e atualizada sozinha pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## Entidade: Livro

Tabela `livros`.

| Campo | Tipo | Regras |
|---|---|---|
| `id` | Long | Chave primária, gerada automaticamente (auto-incremento) |
| `titulo` | String | Obrigatório, não pode ser vazio, máximo de 150 caracteres |
| `autor` | String | Obrigatório, não pode ser vazio, máximo de 100 caracteres |
| `isbn` | String | Obrigatório, **único**, 10 ou 13 dígitos numéricos |
| `anoPublicacao` | Integer | Obrigatório, entre 1000 e 2100 |

## Rotas

| Método | Rota | Descrição | Status de sucesso |
|---|---|---|---|
| GET | `/api/livros` | Lista todos os livros (filtro opcional `?titulo=texto`) | 200 OK |
| GET | `/api/livros/{id}` | Busca um livro por ID | 200 OK |
| POST | `/api/livros` | Cadastra um livro | 201 Created |
| PUT | `/api/livros/{id}` | Atualiza todos os campos de um livro | 200 OK |
| PATCH | `/api/livros/{id}` | Atualiza só os campos enviados | 200 OK |
| DELETE | `/api/livros/{id}` | Remove um livro | 204 No Content |

### Códigos de erro

| Status | Quando acontece |
|---|---|
| 400 Bad Request | Dados inválidos (validação), JSON malformado ou ID que não é número |
| 404 Not Found | Não existe livro com o ID informado |
| 409 Conflict | Já existe outro livro com o mesmo ISBN |

## Exemplos de requisições e respostas

Os exemplos usam cURL. As datas e os IDs das respostas são ilustrativos.

> **Windows:** no PowerShell use `curl.exe` (e não `curl`) e escreva o JSON com as aspas escapadas, como em `-d "{\"titulo\":\"...\"}"`. Se ficar trabalhoso, use o arquivo `requests.http` (extensão REST Client do VS Code) ou o Postman.

### POST: criar livro

```bash
curl -i -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro","autor":"Machado de Assis","isbn":"9788535910663","anoPublicacao":1899}'
```

Resposta `201 Created` (com o cabeçalho `Location: http://localhost:8080/api/livros/1`):

```json
{
  "id": 1,
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "isbn": "9788535910663",
  "anoPublicacao": 1899
}
```

### GET: listar todos

```bash
curl -i http://localhost:8080/api/livros
```

Resposta `200 OK`:

```json
[
  {
    "id": 1,
    "titulo": "Dom Casmurro",
    "autor": "Machado de Assis",
    "isbn": "9788535910663",
    "anoPublicacao": 1899
  }
]
```

Filtrando por título: `curl -i "http://localhost:8080/api/livros?titulo=casmurro"`

### GET: buscar por ID

```bash
curl -i http://localhost:8080/api/livros/1
```

Resposta `200 OK` com o livro. Para um ID inexistente, a resposta é `404 Not Found`:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Livro com id 999 não encontrado",
  "campos": null,
  "timestamp": "2026-11-02T20:30:00.123"
}
```

### PUT: atualizar completamente

Todos os campos são obrigatórios.

```bash
curl -i -X PUT http://localhost:8080/api/livros/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro (edição revisada)","autor":"Machado de Assis","isbn":"9788535910663","anoPublicacao":1899}'
```

Resposta `200 OK` com o livro atualizado.

### PATCH: atualizar parcialmente

Envie só o que quer mudar.

```bash
curl -i -X PATCH http://localhost:8080/api/livros/1 \
  -H "Content-Type: application/json" \
  -d '{"anoPublicacao":1900}'
```

Resposta `200 OK` com o livro atualizado.

### DELETE: remover

```bash
curl -i -X DELETE http://localhost:8080/api/livros/1
```

Resposta `204 No Content`, sem corpo.

### Erro de validação (400)

```bash
curl -i -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{"titulo":"","autor":"Alguém","isbn":"123","anoPublicacao":5}'
```

Resposta `400 Bad Request`:

```json
{
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos",
  "campos": {
    "titulo": "O título é obrigatório",
    "isbn": "O ISBN deve ter 10 ou 13 dígitos numéricos",
    "anoPublicacao": "O ano de publicação deve ser no mínimo 1000"
  },
  "timestamp": "2026-11-02T20:31:00.456"
}
```

### ISBN repetido (409)

Cadastrar de novo um livro com um ISBN que já existe:

```json
{
  "status": 409,
  "erro": "Conflict",
  "mensagem": "Já existe um livro cadastrado com o ISBN 9788535910663",
  "campos": null,
  "timestamp": "2026-11-02T20:32:00.789"
}
```

## Estrutura do projeto

```
src/main/java/com/suaapi
├── BibliotecaApiApplication.java   classe principal
├── controller/LivroController.java rotas HTTP (CRUD)
├── model/Livro.java                entidade JPA com validações
├── model/LivroPatchRequest.java    corpo do PATCH (campos opcionais)
├── repository/LivroRepository.java acesso ao banco (JpaRepository)
└── exception/                      erros padronizados (400, 404, 409)
```
