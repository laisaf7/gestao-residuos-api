# API de Gestão de Resíduos / EcoCiclo · Documentação técnica

Guia para quem vai entender, rodar ou alterar a API. A lista completa de rotas está no
[README](../README.md#endpoints-principais) e no Swagger (`/swagger-ui.html`).

## 1. Visão geral

A API tem duas partes sobre o mesmo banco:

- **Gestão de resíduos (ESG):** pontos de coleta, descartes, coletas automáticas,
  notificações e indicadores de sustentabilidade.
- **EcoCiclo:** a carteira de EcoMoedas. Cada entrega pesada num ecoponto vira moedas,
  que o cidadão gasta em comércios do bairro ou em produtos de artesãos.

O cliente principal é o app Android [EcoCiclo](https://github.com/laisaf7/eco-ciclo).

## 2. Tecnologias

| Item | Uso |
|---|---|
| Java 17 + Spring Boot 3 | API REST |
| Spring Data JPA (Hibernate) | Acesso ao banco |
| Spring Security + JWT | Login e permissões por perfil |
| Flyway | Migrações versionadas do banco (`src/main/resources/db/migration`) |
| PostgreSQL 16 | Banco (Docker local; Neon em produção) |
| H2 (modo PostgreSQL) | Testes automatizados, com Flyway desligado |
| springdoc-openapi | Swagger |
| Docker / docker-compose | Execução local e imagem do Render |

## 3. Camadas

```
Requisição HTTP
  -> security (JwtAuthenticationFilter valida o token e carrega o perfil)
  -> controller (rotas, @Valid nos DTOs de entrada)
  -> service (regras de negócio, transações)
  -> repository (Spring Data JPA)
  -> PostgreSQL
Erros -> exception/GlobalExceptionHandler (BusinessException 422, ResourceNotFoundException 404,
         AccessDenied 403, erros do Spring mantêm o próprio status)
```

Campos booleanos ficam como `CHAR(1)` `'S'/'N'` (padrão herdado do Oracle), convertidos por
`domain/converter/SimNaoConverter`.

## 4. Modelo de dados

| Tabela | Guarda | Principais ligações |
|---|---|---|
| `USUARIO` | Login, senha (BCrypt), perfil (`ADMIN`, `USER`, `ARTESAO`) e saldo de EcoMoedas | |
| `TIPO_RESIDUO` | Material (papel, plástico, vidro, metal...) e `MOEDAS_POR_KG` | |
| `PONTO_COLETA` | Ecoponto ou ateliê: endereço, coordenadas, capacidade, ocupação e status | `TIPO_RESIDUO`; `ID_USUARIO_ARTESAO` quando é ateliê |
| `DESCARTE` | Cada entrega pesada (kg, correto ou não, origem) | `PONTO_COLETA`, `TIPO_RESIDUO`, `USUARIO` |
| `COLETA` | Coleta de caminhão agendada ou automática | `PONTO_COLETA` |
| `NOTIFICACAO` | Alertas de limite de ocupação, descarte incorreto e coleta concluída | `PONTO_COLETA` |
| `ENTREGA_QR` | QR Code da pesagem: código, kg, quem resgatou, cupom de ateliê e se é de demonstração | `PONTO_COLETA`, `TIPO_RESIDUO`, `USUARIO` |
| `TRANSACAO_MOEDA` | Movimentações: `CREDITO`, `VOUCHER`, `TRANSFERENCIA`, `COMPRA`, `VENDA` | `USUARIO`, `COMERCIO` |
| `COMERCIO` | Comércios parceiros e o saldo recebido em transferências | |
| `PRODUTO` | Produtos dos artesãos (preço em reais e, opcional, em EcoMoedas) | `PONTO_COLETA` (ateliê) |
| `ARTIGO` | Ideias de reaproveitamento, com foto opcional | `USUARIO` (autor) |

Os ids vêm de sequences (`SEQ_<TABELA>`).

## 5. Regras de negócio

### Descarte e coleta automática (`DescarteService`, `ColetaService`)

- O descarte é **correto** quando o material informado é o que o ponto aceita.
- Descarte incorreto não soma ao ponto e gera uma notificação `DESTINACAO_INCORRETA`.
- Descarte correto soma os kg ao ponto e recalcula a ocupação e o status:
  `DISPONIVEL`, `ALERTA` (acima do limite de alerta) ou `CHEIO` (100%).
- Ao cruzar o limite de alerta, a API agenda uma coleta e notifica `LIMITE_COLETA`
  (ateliês não geram coleta de caminhão).

Essas regras eram triggers PL/SQL na fase anterior; o script original está em
`docs/triggers_fase_anterior_referencia.sql`.

### Carteira (`CarteiraService`)

| Ação | Regra |
|---|---|
| Gerar QR | Só `ADMIN` ou o artesão no próprio ateliê; o material precisa ser o do ponto. Código UUID de uso único |
| Resgatar | Registra o descarte; se correto, credita `kg × MOEDAS_POR_KG`. No ateliê: +50% (`ArtesaoService.INCENTIVO_PERCENTUAL`) e um cupom de 50% num produto de lá |
| QR já usado | Erro 422 "Este QR Code ja foi utilizado"; o saldo não muda |
| Código de demonstração (`DEMO = 'S'`) | Cada leitura cria uma entrega nova e resgata a cópia, então o código nunca se esgota |
| Pagar | Exige saldo suficiente. `VOUCHER` gera um código de 12 caracteres; `TRANSFERENCIA` soma ao saldo do comércio |
| Histórico | As 20 transações mais recentes, por `DATA_TRANSACAO` (desempate pelo id) |

Resgate e pagamento travam a linha do usuário (`findForUpdate...`), e o resgate trava a do
QR, para que duas requisições ao mesmo tempo não gastem o mesmo saldo ou o mesmo código.

### Artesão (`ArtesaoService`, `ProdutoService`)

- Um `USER` vira artesão ao cadastrar o ateliê (até 100 kg). A API devolve um token novo com o perfil `ARTESAO`.
- Compra com EcoMoedas: debita o comprador (`COMPRA`, com código de retirada) e credita o
  artesão (`VENDA`). Com cupom do ateliê, o preço cai 50% e o cupom é consumido.
- Ninguém compra o próprio produto; produto só com preço em reais não é vendido pelo app.

### Artigos (`ArtigoService`)

Foto só JPEG e até 1 MB. Só o autor edita; o autor ou um `ADMIN` exclui.

## 6. Segurança

- `POST /api/auth/login` devolve um JWT (validade em `jwt.expiration-ms`); as demais rotas
  exigem `Authorization: Bearer <token>`.
- Permissões por rota em `config/SecurityConfig.java`; regras de dono (artigo, produto,
  ateliê) são checadas no service.
- Públicos: login, Swagger e `/actuator/health`.
- As senhas de demonstração são públicas de propósito, para quem avalia seguir o roteiro do app.

## 7. Migrações

| Versão | Conteúdo |
|---|---|
| `V1`, `V2` | Esquema de gestão de resíduos e dados iniciais |
| `V3` | Usuários e perfis (`admin`, `usuario`) |
| `V4` | Carteira EcoCiclo: coordenadas, moedas por kg, comércios, QR e transações |
| `V5` a `V7` | Artigos de reaproveitamento, fotos e data de edição |
| `V8`, `V9` | Perfil artesão, produtos e cupom do ateliê |
| `V10` | Histórico de demonstração por conta |
| `V11` | Códigos de demonstração reutilizáveis |

**Para mudar o banco:** crie `V12__descricao.sql`. Nunca edite uma migração que já rodou em
produção: o Flyway confere o checksum e a API não sobe. Teste a sequência inteira num
PostgreSQL vazio antes do push, por exemplo:

```bash
docker run -d --rm --name pg-teste -e POSTGRES_PASSWORD=x -e POSTGRES_DB=t postgres:16-alpine
for f in $(ls src/main/resources/db/migration/V*.sql | sort -V); do
  docker exec -i pg-teste psql -v ON_ERROR_STOP=1 -U postgres -d t < "$f" || break
done
docker rm -f pg-teste
```

## 8. Execução e deploy

| Ambiente | Como |
|---|---|
| Local com Docker | `docker compose up --build` (variáveis no `.env`, a partir do `.env.example`) |
| Local sem Docker | `mvn spring-boot:run` com `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET` |
| Produção | Render (Docker) publica sozinho a cada push na `main`; o Flyway roda as migrações novas na subida. Banco no Neon |

Variáveis no Render: `DB_URL` (`jdbc:postgresql://<host>/<banco>?sslmode=require`),
`DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`. A porta vem de `PORT`.

O plano grátis do Render desliga a API depois de um tempo parada e leva cerca de 50 s
para religar; um ping a cada 10 min em `/actuator/health` (cron-job.org) evita isso.

## 9. Testes

`mvn test` roda os testes unitários dos services (Mockito) e de HTTP (`ErrosHttpTest`)
sobre H2. O Flyway fica desligado nos testes, então as migrações devem ser testadas à parte
(seção 7). A collection do Postman em `postman/` cobre todas as rotas.
