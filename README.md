# API de Gestão de Resíduos e Reciclagem (ESG)

Serviço RESTful em **Spring Boot 3 + Java 17**, desenvolvido para o tema ESG
**"Gestão de Resíduos e Reciclagem"**, dando continuidade ao modelo de dados
Oracle (DDL + triggers PL/SQL) entregue na fase anterior do projeto. Desde
novembro de 2026 o banco é **PostgreSQL** (veja "Mudança de Oracle para PostgreSQL").

## Funcionalidades

- Rastreamento de coleta seletiva e descarte correto de resíduos.
- Alertas automáticos (geração de coleta) quando um ponto atinge o limite de ocupação.
- Notificações automáticas de destinação incorreta e de conclusão de coleta.
- Indicadores consolidados de sustentabilidade (taxa de descarte correto, kg reciclado, etc.).
- Autenticação via **JWT** com Spring Security (perfis `ADMIN` e `USER`).
- Validação de dados com Bean Validation e tratamento global de exceções.
- Migrações de banco de dados com **Flyway**.
- Banco de dados **PostgreSQL 16** (via Docker localmente; Neon na nuvem).
- Execução via **Docker** / `docker-compose`.
- Documentação interativa via Swagger/OpenAPI.

## Arquitetura e automações herdadas da fase anterior

As quatro automações descritas no documento da fase de modelagem (triggers
`TRG_VALIDAR_DESCARTE`, `TRG_ATUALIZAR_PONTO_COLETA`, `TRG_GERAR_COLETA` e
`TRG_FINALIZAR_COLETA`) foram **portadas para a camada de serviço** da
aplicação (`DescarteService` e `ColetaService`), para que o comportamento seja
idêntico em qualquer banco (PostgreSQL em produção, H2 nos testes automatizados) e
fique testável via JUnit/Mockito. O script original das triggers foi mantido
em `docs/triggers_fase_anterior_referencia.sql` como registro da modelagem
PL/SQL aprovada na fase anterior.

## Mudança de Oracle para PostgreSQL (novembro de 2026)

**Por quê:** a ideia é deixar o app EcoCiclo funcionando a qualquer momento
para recrutadores testarem, sem depender de um PC ligado como servidor. Para
isso a API precisa ficar hospedada na nuvem de graça e por tempo indeterminado:

- A Oracle Cloud (Always Free) exigiu CNPJ no cadastro, sem opção de pessoa física.
- O Azure for Students funcionaria, mas o crédito acaba em cerca de 3 meses.
- O PostgreSQL tem hospedagem gratuita permanente e sem documento
  (API no **Render**, banco no **Neon**). A atividade não exige Oracle.

**O que mudou:**

| Arquivo | Mudança |
|---|---|
| `pom.xml` | `ojdbc11` e `flyway-database-oracle` trocados pelo driver `postgresql` (o Flyway 9 já suporta PostgreSQL no núcleo) |
| `application.yml` | URL e driver do PostgreSQL; porta lida de `PORT` (exigência do Render), com 8080 como padrão |
| `db/migration/V1` a `V4` | Sintaxe convertida: `VARCHAR2`→`VARCHAR`, `NUMBER(10)`→`BIGINT`, `NUMBER(p,s)`→`NUMERIC(p,s)`, `SYSDATE`→`CURRENT_DATE`, `SEQ.NEXTVAL`→`nextval('SEQ')`, `ALTER TABLE ... ADD (...)`→`ADD COLUMN`, sem `NOCACHE`/`COMMIT` |
| `NOTIFICACAO.DATA_NOTIFICACAO` | Agora `TIMESTAMP`: no Java é `LocalDateTime`, e o `DATE` do PostgreSQL não guarda hora |
| `docker-compose.yml` | Container `postgres:16-alpine` no lugar do Oracle XE; portas 8080 e 5432 presas a `127.0.0.1` para não expor o banco |
| `.env.example` | Removida a variável `ORACLE_SYS_PASSWORD` |
| Testes (H2) | Modo de compatibilidade `MODE=PostgreSQL` |

Entidades, sequences, regras de negócio e o conversor `'S'/'N'` não mudaram.
O fluxo completo (login, QR, resgate, voucher e carteira) e o `mvn test`
foram validados no PostgreSQL. O script original em Oracle continua em
`docs/` como registro da fase anterior.

**Hospedagem:** banco no Neon, API no Render em
`https://gestao-residuos-api-1.onrender.com/` (plano grátis, região Virginia) e um ping a cada 10 min em `/actuator/health` via
cron-job.org para o Render não "dormir". Variáveis no Render: `DB_URL`
(formato `jdbc:postgresql://<host>/<banco>?sslmode=require`), `DB_USERNAME`,
`DB_PASSWORD` e `JWT_SECRET`.

## Estrutura de pacotes

```
src/main/java/com/esg/residuos
├── config          -> SecurityConfig, OpenApiConfig
├── controller       -> Controllers REST (endpoints)
├── domain/entity     -> Entidades JPA
├── domain/enums      -> Enumeradores (StatusPonto, StatusColeta, Prioridade)
├── domain/converter  -> Conversor Boolean <-> CHAR(1) 'S'/'N' (padrão herdado do Oracle)
├── dto/request       -> DTOs de entrada
├── dto/response      -> DTOs de saída
├── exception         -> Exceções de negócio e handler global
├── repository        -> Spring Data JPA Repositories
├── security          -> JWT (filtro, serviço, UserDetailsService)
└── service           -> Regras de negócio
```

## Endpoints principais

| Método | Endpoint | Perfil | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Autentica e retorna o token JWT |
| GET | `/api/tipos-residuo` | USER/ADMIN | Lista os tipos de resíduo |
| POST/PUT/DELETE | `/api/tipos-residuo` | ADMIN | Cadastra/edita/remove tipo de resíduo |
| GET | `/api/pontos-coleta` | USER/ADMIN | Lista pontos de coleta (filtro opcional `?status=`) |
| GET | `/api/pontos-coleta/{id}/historico-descartes` | USER/ADMIN | Histórico de descartes de um ponto |
| POST/PUT/DELETE | `/api/pontos-coleta` | ADMIN | Cadastra/edita/remove ponto de coleta |
| POST | `/api/descartes` | ADMIN | Registra um novo descarte (valida, atualiza o ponto e pode gerar coleta automática) |
| GET | `/api/descartes` , `/api/descartes/{id}` | USER/ADMIN | Consulta descartes |
| POST | `/api/coletas` | ADMIN | Agenda uma nova coleta |
| PUT | `/api/coletas/{id}` | ADMIN | Atualiza/avança o status de uma coleta (ex.: concluir) |
| DELETE | `/api/coletas/{id}` | ADMIN | Cancela uma coleta |
| GET | `/api/coletas`, `/api/coletas/{id}` | USER/ADMIN | Consulta coletas |
| GET | `/api/notificacoes` | USER/ADMIN | Lista notificações (filtro `?naoLidas=true`) |
| PUT | `/api/notificacoes/{id}/marcar-lida` | ADMIN | Marca notificação como lida |
| GET | `/api/indicadores-sustentabilidade` | USER/ADMIN | Indicadores ESG consolidados |

Documentação interativa completa: `http://localhost:8080/swagger-ui.html`.

## Autenticação

Usuários padrão criados na migração `V3__create_security.sql` (apenas para
desenvolvimento — troque as senhas em um ambiente real):

| username | senha | perfil |
|---|---|---|
| `admin` | `admin123` | ADMIN (leitura e escrita) |
| `usuario` | `user123` | USER (somente leitura) |

Fluxo: `POST /api/auth/login` com `{"username": "...", "password": "..."}`
retorna um token JWT que deve ser enviado no cabeçalho
`Authorization: Bearer <token>` nas demais requisições.

## Como executar com Docker (recomendado)

1. Copie `.env.example` para `.env` e preencha as senhas/segredos.
2. Suba os containers:
   ```
   docker compose up --build
   ```
3. A API ficará disponível em `http://localhost:8080` e o PostgreSQL em `localhost:5432`. As duas portas só aceitam conexões da própria máquina.
   O Flyway aplica as migrações automaticamente na subida da aplicação.

## Como executar localmente (sem Docker)

Pré-requisitos: Java 17, Maven 3.9+, uma instância PostgreSQL acessível.

```
export DB_URL=jdbc:postgresql://localhost:5432/residuos
export DB_USERNAME=residuos_user
export DB_PASSWORD=<senha>
export JWT_SECRET=<chave-secreta-longa>
mvn spring-boot:run
```

## Testes

```
mvn test
```

Inclui teste de contexto Spring e testes unitários (`DescarteServiceTest`)
que validam a validação de descarte, geração de notificação de destinação
incorreta e a geração automática de coleta ao cruzar o limite de ocupação.

## Collection do Postman

O arquivo `postman/gestao-residuos-api.postman_collection.json` contém **todos os endpoints**,
organizados em pastas numeradas, com testes automáticos de status em cada requisição.

1. No Postman: **Import** → selecione o arquivo.
2. Rode a pasta **1. Autenticacao** primeiro: os tokens ADMIN e USER são salvos sozinhos.
3. Para rodar tudo de uma vez: botão **Run collection** (as pastas devem rodar na ordem).

Pela linha de comando (com a API no ar):

```
npx newman run postman/gestao-residuos-api.postman_collection.json
```

> Observação: no PowerShell, parâmetros `-D` do Maven precisam de aspas,
> ex.: `mvn clean compile "-Dmaven.compiler.failOnError=true"`.
