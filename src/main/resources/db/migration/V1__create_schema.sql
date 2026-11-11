-- ============================================================
-- V1: Criacao do esquema (sequences, tabelas e indices)
-- Sistema de Gestao de Residuos e Reciclagem - Projeto ESG
-- ============================================================

-- ============================================================
-- SISTEMA DE GESTAO DE RESIDUOS E RECICLAGEM
-- Automacao de processos ESG com SQL
-- ============================================================

-- ============================================================
-- CRIACAO DAS SEQUENCES
-- ============================================================

CREATE SEQUENCE SEQ_TIPO_RESIDUO
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE SEQ_PONTO_COLETA
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE SEQ_DESCARTE
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE SEQ_COLETA
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE SEQ_NOTIFICACAO
    START WITH 1
    INCREMENT BY 1;


-- ============================================================
-- TABELA TIPO_RESIDUO
-- ============================================================

CREATE TABLE TIPO_RESIDUO (
    ID_TIPO_RESIDUO        BIGINT       NOT NULL,
    NOME                    VARCHAR(100)    NOT NULL,
    CATEGORIA               VARCHAR(50)     NOT NULL,
    COR_IDENTIFICACAO       VARCHAR(30),
    DESTINACAO_CORRETA      VARCHAR(300)    NOT NULL,
    IMPACTO_AMBIENTAL       VARCHAR(500),
    ATIVO                   CHAR(1)          DEFAULT 'S' NOT NULL,

    CONSTRAINT PK_TIPO_RESIDUO
        PRIMARY KEY (ID_TIPO_RESIDUO),

    CONSTRAINT UK_TIPO_RESIDUO_NOME
        UNIQUE (NOME),

    CONSTRAINT CK_TIPO_RESIDUO_ATIVO
        CHECK (ATIVO IN ('S', 'N'))
);


-- ============================================================
-- TABELA PONTO_COLETA
-- ============================================================

CREATE TABLE PONTO_COLETA (
    ID_PONTO_COLETA             BIGINT       NOT NULL,
    ID_TIPO_RESIDUO             BIGINT       NOT NULL,
    NOME                        VARCHAR(120)     NOT NULL,
    DESCRICAO                   VARCHAR(300),
    ENDERECO                    VARCHAR(200),
    BAIRRO                      VARCHAR(100),
    CIDADE                      VARCHAR(100),
    ESTADO                      CHAR(2),
    CEP                         VARCHAR(10),
    CAPACIDADE_KG               NUMERIC(10,2)      NOT NULL,
    QUANTIDADE_ATUAL_KG         NUMERIC(10,2)      DEFAULT 0 NOT NULL,
    PERCENTUAL_OCUPACAO         NUMERIC(6,2)       DEFAULT 0 NOT NULL,
    LIMITE_ALERTA_PERCENTUAL    NUMERIC(5,2)       DEFAULT 80 NOT NULL,
    STATUS                      VARCHAR(20)       DEFAULT 'DISPONIVEL' NOT NULL,
    DATA_INSTALACAO             DATE              DEFAULT CURRENT_DATE NOT NULL,
    DATA_ULTIMA_COLETA          DATE,
    ATIVO                       CHAR(1)           DEFAULT 'S' NOT NULL,

    CONSTRAINT PK_PONTO_COLETA
        PRIMARY KEY (ID_PONTO_COLETA),

    CONSTRAINT FK_PONTO_TIPO_RESIDUO
        FOREIGN KEY (ID_TIPO_RESIDUO)
        REFERENCES TIPO_RESIDUO (ID_TIPO_RESIDUO),

    CONSTRAINT CK_PONTO_CAPACIDADE
        CHECK (CAPACIDADE_KG > 0),

    CONSTRAINT CK_PONTO_QUANTIDADE
        CHECK (QUANTIDADE_ATUAL_KG >= 0),

    CONSTRAINT CK_PONTO_PERCENTUAL
        CHECK (PERCENTUAL_OCUPACAO >= 0),

    CONSTRAINT CK_PONTO_LIMITE
        CHECK (
            LIMITE_ALERTA_PERCENTUAL > 0
            AND LIMITE_ALERTA_PERCENTUAL <= 100
        ),

    CONSTRAINT CK_PONTO_STATUS
        CHECK (
            STATUS IN (
                'DISPONIVEL',
                'ALERTA',
                'CHEIO',
                'EM_COLETA',
                'INATIVO'
            )
        ),

    CONSTRAINT CK_PONTO_ATIVO
        CHECK (ATIVO IN ('S', 'N'))
);


-- ============================================================
-- TABELA DESCARTE
-- ============================================================

CREATE TABLE DESCARTE (
    ID_DESCARTE            BIGINT       NOT NULL,
    ID_PONTO_COLETA        BIGINT       NOT NULL,
    ID_TIPO_RESIDUO        BIGINT       NOT NULL,
    DATA_DESCARTE          DATE             DEFAULT CURRENT_DATE NOT NULL,
    QUANTIDADE_KG          NUMERIC(10,2)     NOT NULL,
    ORIGEM                 VARCHAR(100),
    DESCARTE_CORRETO       CHAR(1)          DEFAULT 'S' NOT NULL,
    OBSERVACAO             VARCHAR(500),

    CONSTRAINT PK_DESCARTE
        PRIMARY KEY (ID_DESCARTE),

    CONSTRAINT FK_DESCARTE_PONTO
        FOREIGN KEY (ID_PONTO_COLETA)
        REFERENCES PONTO_COLETA (ID_PONTO_COLETA),

    CONSTRAINT FK_DESCARTE_TIPO
        FOREIGN KEY (ID_TIPO_RESIDUO)
        REFERENCES TIPO_RESIDUO (ID_TIPO_RESIDUO),

    CONSTRAINT CK_DESCARTE_QUANTIDADE
        CHECK (QUANTIDADE_KG > 0),

    CONSTRAINT CK_DESCARTE_CORRETO
        CHECK (DESCARTE_CORRETO IN ('S', 'N'))
);


-- ============================================================
-- TABELA COLETA
-- ============================================================

CREATE TABLE COLETA (
    ID_COLETA                  BIGINT       NOT NULL,
    ID_PONTO_COLETA            BIGINT       NOT NULL,
    DATA_SOLICITACAO           DATE             DEFAULT CURRENT_DATE NOT NULL,
    DATA_AGENDADA              DATE,
    DATA_COLETA                DATE,
    QUANTIDADE_COLETADA_KG     NUMERIC(10,2),
    STATUS                     VARCHAR(20)     DEFAULT 'SOLICITADA' NOT NULL,
    DESTINO_FINAL              VARCHAR(300),
    OBSERVACAO                 VARCHAR(500),

    CONSTRAINT PK_COLETA
        PRIMARY KEY (ID_COLETA),

    CONSTRAINT FK_COLETA_PONTO
        FOREIGN KEY (ID_PONTO_COLETA)
        REFERENCES PONTO_COLETA (ID_PONTO_COLETA),

    CONSTRAINT CK_COLETA_QUANTIDADE
        CHECK (
            QUANTIDADE_COLETADA_KG IS NULL
            OR QUANTIDADE_COLETADA_KG >= 0
        ),

    CONSTRAINT CK_COLETA_STATUS
        CHECK (
            STATUS IN (
                'SOLICITADA',
                'AGENDADA',
                'EM_ANDAMENTO',
                'CONCLUIDA',
                'CANCELADA'
            )
        )
);


-- ============================================================
-- TABELA NOTIFICACAO
-- ============================================================

CREATE TABLE NOTIFICACAO (
    ID_NOTIFICACAO         BIGINT       NOT NULL,
    ID_PONTO_COLETA        BIGINT       NOT NULL,
    ID_COLETA              BIGINT,
    TIPO                    VARCHAR(40)     NOT NULL,
    TITULO                  VARCHAR(150)    NOT NULL,
    MENSAGEM                VARCHAR(500)    NOT NULL,
    DATA_NOTIFICACAO       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LIDA                    CHAR(1)          DEFAULT 'N' NOT NULL,
    PRIORIDADE              VARCHAR(10)     DEFAULT 'MEDIA' NOT NULL,

    CONSTRAINT PK_NOTIFICACAO
        PRIMARY KEY (ID_NOTIFICACAO),

    CONSTRAINT FK_NOTIFICACAO_PONTO
        FOREIGN KEY (ID_PONTO_COLETA)
        REFERENCES PONTO_COLETA (ID_PONTO_COLETA),

    CONSTRAINT FK_NOTIFICACAO_COLETA
        FOREIGN KEY (ID_COLETA)
        REFERENCES COLETA (ID_COLETA),

    CONSTRAINT CK_NOTIFICACAO_LIDA
        CHECK (LIDA IN ('S', 'N')),

    CONSTRAINT CK_NOTIFICACAO_PRIORIDADE
        CHECK (
            PRIORIDADE IN (
                'BAIXA',
                'MEDIA',
                'ALTA'
            )
        )
);


-- ============================================================
-- INDICES
-- ============================================================

CREATE INDEX IDX_PONTO_TIPO_RESIDUO
    ON PONTO_COLETA (ID_TIPO_RESIDUO);

CREATE INDEX IDX_DESCARTE_PONTO
    ON DESCARTE (ID_PONTO_COLETA);

CREATE INDEX IDX_DESCARTE_TIPO
    ON DESCARTE (ID_TIPO_RESIDUO);

CREATE INDEX IDX_DESCARTE_DATA
    ON DESCARTE (DATA_DESCARTE);

CREATE INDEX IDX_COLETA_PONTO
    ON COLETA (ID_PONTO_COLETA);

CREATE INDEX IDX_COLETA_STATUS
    ON COLETA (STATUS);

CREATE INDEX IDX_NOTIFICACAO_PONTO
    ON NOTIFICACAO (ID_PONTO_COLETA);

CREATE INDEX IDX_NOTIFICACAO_COLETA
    ON NOTIFICACAO (ID_COLETA);

CREATE INDEX IDX_NOTIFICACAO_DATA
    ON NOTIFICACAO (DATA_NOTIFICACAO);
