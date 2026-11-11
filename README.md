# API de Gestão de Resíduos e Reciclagem (ESG)

Serviço RESTful em **Spring Boot 3 + Java 17**, desenvolvido para o tema ESG
**"Gestão de Resíduos e Reciclagem"**, dando continuidade ao modelo de dados
Oracle (DDL + triggers PL/SQL) entregue na fase anterior do projeto.

## Funcionalidades

- Rastreamento de coleta seletiva e descarte correto de resíduos.
- Alertas automáticos (geração de coleta) quando um ponto atinge o limite de ocupação.
- Notificações automáticas de destinação incorreta e de conclusão de coleta.
- Indicadores consolidados de sustentabilidade (taxa de descarte correto, kg reciclado, etc.).
- Autenticação via **JWT** com Spring Security (perfis `ADMIN` e `USER`).
- Validação de dados com Bean Validation e tratamento global de exceções.
- Migrações de banco de dados com **Flyway**.
- Banco de dados **Oracle** (via Docker, imagem `gvenzl/oracle-xe`).
- Execução via **Docker** / `docker-compose`.
- Documentação interativa via Swagger/OpenAPI.

## Arquitetura e automações herdadas da fase anterior

As quatro automações descritas no documento da fase de modelagem (triggers
`TRG_VALIDAR_DESCARTE`, `TRG_ATUALIZAR_PONTO_COLETA`, `TRG_GERAR_COLETA` e
`TRG_FINALIZAR_COLETA`) foram **portadas para a camada de serviço** da
aplicação (`DescarteService` e `ColetaService`), para que o comportamento seja
idêntico em qualquer banco (Oracle em produção, H2 nos testes automatizados) e
fique testável via JUnit/Mockito. O script original das triggers foi mantido
em `docs/triggers_fase_anterior_referencia.sql` como registro da modelagem
PL/SQL aprovada na fase anterior.

## Estrutura de pacotes

```
src/main/java/com/esg/residuos
├── config          -> SecurityConfig, OpenApiConfig
├── controller       -> Controllers REST (endpoints)
├── domain/entity     -> Entidades JPA
├── domain/enums      -> Enumeradores (StatusPonto, StatusColeta, Prioridade)
├── domain/converter  -> Conversor Boolean <-> CHAR(1) 'S'/'N' (padrão Oracle)
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
3. A API ficará disponível em `http://localhost:8080` e o Oracle em `localhost:1521`.
   O Flyway aplica as migrações automaticamente na subida da aplicação.

## Como executar localmente (sem Docker)

Pré-requisitos: Java 17, Maven 3.9+, uma instância Oracle acessível.

```
export DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1
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
