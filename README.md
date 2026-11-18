# API de Gestão de Resíduos e Reciclagem (ESG)

Serviço RESTful em **Spring Boot 3 + Java 17**, desenvolvido para o tema ESG
**"Gestão de Resíduos e Reciclagem"**, dando continuidade ao modelo de dados
Oracle (DDL + triggers PL/SQL) entregue na fase anterior do projeto. Desde
novembro de 2026 o banco é **PostgreSQL** (veja "Mudança de Oracle para PostgreSQL").

É o backend do app Android [EcoCiclo](https://github.com/laisaf7/eco-ciclo), que
transforma recicláveis entregues em ecopontos em EcoMoedas para gastar no comércio do bairro.
Para ver a jornada sem instalar nada, use o protótipo navegável: https://eco-ciclo-web.vercel.app/.
Documentação técnica (modelo de dados, regras e fluxos): [`docs/DOCUMENTACAO.md`](docs/DOCUMENTACAO.md).

## Funcionalidades

- **Carteira EcoCiclo:** QR Code da pesagem, conversão de kg em EcoMoedas, vouchers,
  transferências para comércios e histórico (veja "Carteira EcoCiclo").
- Rastreamento de coleta seletiva e descarte correto de resíduos.
- Alertas automáticos (geração de coleta) quando um ponto atinge o limite de ocupação.
- Notificações automáticas de destinação incorreta e de conclusão de coleta.
- Indicadores consolidados de sustentabilidade (taxa de descarte correto, kg reciclado, etc.).
- Artigos de reaproveitamento ("Ideias"): passo a passo para transformar recicláveis em
  produtos novos, com foto e filtro por material; qualquer usuário publica, o autor
  edita e exclui, o `ADMIN` modera (veja "Artigos de reaproveitamento").
- Autenticação via **JWT** com Spring Security (perfis `ADMIN`, `USER` e `ARTESAO`).
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

## Carteira EcoCiclo

Criada na `V4__ecociclo.sql`: coordenadas nos pontos, `MOEDAS_POR_KG` nos tipos de
resíduo (papel 2, plástico 5, vidro 1,5, metal 8, demais 1), saldo no usuário e as
tabelas `COMERCIO`, `ENTREGA_QR` e `TRANSACAO_MOEDA`.

1. Na pesagem, `ADMIN` (ou o artesão, no próprio ateliê) chama `POST /api/entregas-qr`
   com ponto, material e kg. O código é um UUID de uso único.
2. O cidadão envia o código em `POST /api/carteira/resgates`. A API registra o descarte
   (que soma os kg e atualiza a ocupação do ponto) e, se o material bate com o ponto,
   credita `kg × MOEDAS_POR_KG` (+50% em ateliê) e grava uma transação `CREDITO`.
3. `POST /api/carteira/pagamentos` debita o saldo como `VOUCHER` (código de 12 caracteres
   para mostrar no caixa) ou `TRANSFERENCIA` (o valor vai para o comércio).
4. `GET /api/carteira` devolve saldo, kg desviados e as 20 últimas transações, da mais
   recente para a mais antiga (por `DATA_TRANSACAO`, desempate pelo id).

`GET /api/pontos-coleta` devolve, em cada ponto, `moedasPorKg` do material aceito.

### Dados de demonstração (novembro de 2026)

| Migração | O quê |
|---|---|
| `V10__historico_demo.sql` | Cada conta ativa sem movimentações ganha entregas e um voucher, com valores diferentes por conta (3 perfis). Grava descarte, transação e saldo como numa entrega real, então o saldo é a soma do histórico |
| `V11__codigos_demo.sql` | Coluna `DEMO` em `ENTREGA_QR` e os códigos `ECO-4821` (3 kg de plástico), `ECO-7310` (1,2 kg de metal) e `ATL-0550` (2 kg no ateliê), os mesmos do protótipo web |

**Códigos de demonstração:** a cada leitura, `CarteiraService.resgatar` cria uma entrega
nova com UUID e resgata essa cópia, então o código nunca fica marcado como usado e vale
para qualquer conta. Por isso qualquer pessoa pode acumular moedas de demonstração com
eles, e cada uso soma kg à ocupação do ponto (o ponto pode aparecer como "Cheio"; o
artesão esvazia o ateliê pelo app).

## Artigos de reaproveitamento (novembro de 2026)

Base da aba **Ideias** do app EcoCiclo: artigos que ensinam a transformar
material reciclável (ou que viraria lixo, como uma calça jeans) em outro produto.

| Migração | O que faz |
|---|---|
| `V5__artigos.sql` | Tabela `ARTIGO` e 6 artigos de exemplo (um por material), com autor `EcoCiclo` |
| `V6__artigo_imagem.sql` | Coluna `IMAGEM` (`BYTEA`) e as fotos dos 6 exemplos embutidas em Base64 (por isso o arquivo tem ~530 KB) |
| `V7__artigo_atualizacao.sql` | Coluna `DATA_ATUALIZACAO`, preenchida a cada edição |

**Regras:**

- Materiais aceitos: `Plástico`, `Papel`, `Vidro`, `Metal`, `Tecido`, `Madeira`, `Outros`.
- Foto opcional, enviada em `imagemBase64` no corpo do POST/PUT. Só JPEG (a API confere
  os bytes iniciais) e no máximo 1 MB. O app reduz a foto para 1280 px antes de enviar.
- Na edição, sem `imagemBase64` a foto atual é mantida; `"removerImagem": true` apaga a foto.
- Só o autor edita. O autor ou um `ADMIN` exclui; outro usuário recebe 403.
- A listagem usa uma projeção JPQL (`ArtigoRepository`) para não carregar os bytes das
  fotos; cada foto é baixada à parte em `/api/artigos/{id}/imagem`, com cache de 7 dias
  no celular. O app acrescenta `?v=` com a data da última edição para não mostrar a foto antiga.
- As fotos dos exemplos têm licença livre (CC BY, CC BY-SA ou domínio público). O
  crédito, com autor, licença e link, está no fim do texto de cada artigo.

Junto com essa entrega, o `GlobalExceptionHandler` passou a manter o status dos
erros do próprio Spring: rota inexistente responde 404, método errado 405 e
JSON mal formatado 400 (antes todos viravam 500).

## Perfil artesão (novembro de 2026)

Incentiva quem cria produtos com material reciclado. O artesão:

- vira **ponto de coleta** do material que usa: o ateliê é um `PONTO_COLETA` com
  `ID_USUARIO_ARTESAO` preenchido e capacidade de até 100 kg (ecopontos têm centenas);
- gera o QR das entregas **só no próprio ateliê** e esvazia o ponto quando usa o
  material (ateliê não gera coleta automática de caminhão);
- vende **produtos** com preço em reais (vitrine) e, opcional, em EcoMoedas. A compra
  no app debita o comprador (`COMPRA`, com código de retirada) e credita o artesão (`VENDA`).

| Migração | O quê |
|---|---|
| `V8__artesao.sql` | Papel `ARTESAO`, coluna do artesão no ponto, tabela `PRODUTO`, tipos `COMPRA`/`VENDA` e a conta de demonstração `artesao` com um ateliê de plástico (50 kg) e 2 produtos |
| `V9__incentivo_atelie.sql` | Coluna `DESCONTO_DISPONIVEL` em `ENTREGA_QR`: cada entrega correta num ateliê vira um cupom |

**Incentivo para entregar no ateliê** (`ArtesaoService.INCENTIVO_PERCENTUAL` = 50):

- a entrega rende +50% de EcoMoedas (o QR já mostra o valor com bônus);
- e vira um cupom de 50% de desconto em **uma** compra de produto daquele ateliê com
  EcoMoedas. O artesão recebe o valor com desconto. `GET /api/carteira` devolve
  `ateliesComDesconto` (ids dos ateliês onde a pessoa tem cupom).

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
| GET | `/api/carteira` | Logado | Saldo, kg desviados, últimas 20 transações e ateliês com cupom |
| POST | `/api/carteira/resgates` | Logado | Resgata o código do QR e credita as EcoMoedas |
| POST | `/api/carteira/pagamentos` | Logado | Gera voucher ou transfere EcoMoedas para um comércio |
| GET | `/api/comercios` | Logado | Comércios parceiros |
| POST | `/api/artesao` | USER | Vira artesão: cria o ateliê (corpo de ponto de coleta, até 100 kg) e devolve token novo |
| GET | `/api/artesao/atelie` | ARTESAO | Ateliê de quem está logado (ocupação e status) |
| POST | `/api/artesao/atelie/esvaziar` | ARTESAO | Zera a ocupação do ateliê |
| POST | `/api/entregas-qr` | ADMIN ou ARTESAO | Gera o QR da pesagem; artesão só no próprio ateliê |
| GET | `/api/produtos`, `/api/produtos/{id}/imagem` | Logado | Catálogo dos artesãos e foto do produto |
| POST | `/api/produtos` | ARTESAO | Publica produto no próprio ateliê |
| PUT / DELETE | `/api/produtos/{id}` | Artesão dono (DELETE também ADMIN) | Edita ou exclui produto |
| POST | `/api/produtos/{id}/compras` | Logado | Compra com EcoMoedas e devolve o código de retirada |
| GET | `/api/artigos` | USER/ADMIN | Lista artigos de reaproveitamento, sem as fotos (filtro opcional `?material=`) |
| GET | `/api/artigos/{id}/imagem` | USER/ADMIN | Foto JPEG do artigo (404 se não tiver) |
| POST | `/api/artigos` | USER/ADMIN | Publica um artigo; o autor é o usuário do token |
| PUT | `/api/artigos/{id}` | Autor | Edita o artigo (texto, material, trocar ou remover a foto) |
| DELETE | `/api/artigos/{id}` | Autor ou ADMIN | Exclui o artigo |

Documentação interativa completa: `http://localhost:8080/swagger-ui.html`.

## Autenticação

Usuários padrão criados na migração `V3__create_security.sql` (apenas para
desenvolvimento — troque as senhas em um ambiente real):

| username | senha | perfil |
|---|---|---|
| `admin` | `admin123` | ADMIN (leitura e escrita) |
| `usuario` | `user123` | USER (leitura, carteira e os próprios artigos) |
| `artesao` | `artesao123` | ARTESAO (tudo do USER + ateliê e produtos; criado na `V8`) |

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

Inclui teste de contexto Spring e testes unitários:

- `DescarteServiceTest`: validação de descarte, notificação de destinação
  incorreta e geração automática de coleta ao cruzar o limite de ocupação.
- `CarteiraServiceTest`: conversão de kg em EcoMoedas no resgate do QR, QR usado
  duas vezes, código de demonstração reutilizável, descarte incorreto sem moedas,
  pagamento sem saldo e transferência.
- `ArtigoServiceTest`: autor do artigo, validação da foto (JPEG/Base64) e quem
  pode editar ou excluir (autor, outro usuário, admin).
- `ArtesaoServiceTest`: cadastro troca o papel e cria o ateliê, limite de 100 kg,
  esvaziar e QR só no próprio ateliê.
- `ProdutoServiceTest`: compra move moedas do cliente para o artesão, saldo
  insuficiente, compra do próprio produto, produto só em reais e dono do produto.
- `ErrosHttpTest`: status 404/405/400 nos erros do Spring e permissão de
  `USER` para editar e excluir artigos.

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
