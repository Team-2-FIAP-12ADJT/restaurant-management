# Restaurant Management

API backend Spring Boot para gerenciamento de restaurante.

Este README cobre execucao local e deploy local usando Docker. O projeto possui arquivos separados para desenvolvimento e producao local:

- `Dockerfile` + `docker-compose.yaml`: ambiente de desenvolvimento, com codigo montado como volume, Maven Wrapper e porta de debug.
- `Dockerfile.prod` + `docker-compose.prod.yaml`: imagem de producao local, com build multi-stage e execucao do JAR.

## Requisitos

- Docker
- Docker Compose
- Java 21, somente se for executar sem Docker
- Maven Wrapper incluso no projeto (`./mvnw`)

## Configuracao

O projeto usa variaveis de ambiente lidas pelo Docker Compose e pela aplicacao Spring Boot. Crie ou ajuste o arquivo `.env` na raiz do projeto:

```dotenv
SPRING_DATASOURCE_DATABASE=restaurant_management
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/restaurant_management
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_PORT=5432
APP_PORT=8080
JWT_SECRET=replace-with-base64-secret
JWT_ACCESS_TOKEN_EXPIRATION_TIME=15
```

Notas:

- Em Docker Compose, `SPRING_DATASOURCE_URL` e sobrescrita para usar o hostname interno `db`.
- A aplicacao esta configurada em `src/main/resources/application.yaml` para escutar na porta `8080`.
- `APP_PORT` controla a porta publicada no host; o container continua ouvindo em `8080`.
- `JWT_SECRET` deve ser Base64. Gere um valor local com `openssl rand -base64 32` e nao commite segredos reais.
- `JWT_ACCESS_TOKEN_EXPIRATION_TIME` e obrigatoria para inicializar a aplicacao.
- O Liquibase roda na inicializacao da aplicacao usando `classpath:db/changelog/db.changelog-master.xml`.

## Execucao Local Com Docker

Use este modo para desenvolvimento. Ele usa:

- `docker-compose.yaml`
- `Dockerfile`
- Postgres em container
- codigo fonte montado em `/app`
- cache Maven em volume Docker
- debug remoto na porta `5005`

Suba a aplicacao:

```bash
docker compose up --build
```

Ou rode em background:

```bash
docker compose up --build -d
```

Acesse a aplicacao em:

```text
http://localhost:8080
```

Para acompanhar logs:

```bash
docker compose logs -f app
```

Para parar os containers:

```bash
docker compose down
```

Para parar e remover volumes locais do banco e cache Maven:

```bash
docker compose down -v
```

## Deploy Local De Producao

Use este modo para validar uma execucao local mais proxima de producao. Ele usa:

- `docker-compose.prod.yaml`
- `Dockerfile.prod`
- build multi-stage com Maven
- imagem final com JRE
- execucao via `java -jar app.jar`
- usuario nao-root dentro do container da aplicacao

Suba o ambiente de producao local:

```bash
docker compose -f docker-compose.prod.yaml up --build
```

Ou rode em background:

```bash
docker compose -f docker-compose.prod.yaml up --build -d
```

Acesse a aplicacao em:

```text
http://localhost:8080
```

Para acompanhar logs:

```bash
docker compose -f docker-compose.prod.yaml logs -f app
```

Para parar:

```bash
docker compose -f docker-compose.prod.yaml down
```

Para parar e remover o volume do banco de producao local:

```bash
docker compose -f docker-compose.prod.yaml down -v
```

## Diferencas Entre Os Arquivos Docker

| Arquivo | Uso | Caracteristicas |
| --- | --- | --- |
| `Dockerfile` | Desenvolvimento | Usa JDK 21 Alpine, instala `bash`, monta o projeto via volume e executa `./mvnw spring-boot:run`. |
| `Dockerfile.prod` | Producao local | Compila o projeto em uma etapa Maven e gera imagem final com JRE, `curl` e usuario nao-root. |
| `docker-compose.yaml` | Desenvolvimento | Sobe Postgres e app, expoe `8080`, expoe debug em `127.0.0.1:5005`, monta o codigo local e cache Maven. |
| `docker-compose.prod.yaml` | Producao local | Sobe Postgres e app usando `Dockerfile.prod`, sem volume de codigo e sem porta de debug. |

## Execucao Sem Docker

Use este modo somente se houver um PostgreSQL acessivel fora do Compose.

1. Ajuste o `.env` para apontar para o banco local:

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/restaurant_management
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

2. Execute a aplicacao:

```bash
./mvnw spring-boot:run
```

3. Acesse:

```text
http://localhost:8080
```

## Comandos Uteis

Compilar e testar o projeto:

```bash
./mvnw clean test
```

Gerar o pacote da aplicacao:

```bash
./mvnw clean package
```

Reconstruir apenas a imagem da aplicacao em desenvolvimento:

```bash
docker compose build app
```

Reconstruir a imagem de producao local:

```bash
docker compose -f docker-compose.prod.yaml build app
```

Ver containers ativos:

```bash
docker compose ps
```

## Troubleshooting

### Porta 8080 ocupada

Pare o processo usando a porta ou ajuste `server.port` e o mapeamento `APP_PORT` de forma consistente.

### Porta 5432 ocupada

Altere `SPRING_DATASOURCE_PORT` no `.env` para outra porta do host, por exemplo:

```dotenv
SPRING_DATASOURCE_PORT=5433
```

Dentro do Compose, a aplicacao continua acessando o banco por `db:5432`.

### Banco com estado antigo

Se houver erro causado por schema antigo ou dados locais, remova os volumes:

```bash
docker compose down -v
```

Para o ambiente de producao local:

```bash
docker compose -f docker-compose.prod.yaml down -v
```

### Permissao do Maven Wrapper

Se `./mvnw` nao tiver permissao de execucao no host:

```bash
chmod +x ./mvnw
```

O container de desenvolvimento tambem executa `chmod +x ./mvnw` antes de iniciar a aplicacao.
