-- ============================================================
-- V3: Estrutura de seguranca (usuarios e perfis de acesso)
-- Sistema de Gestao de Residuos e Reciclagem - Projeto ESG
-- ============================================================

CREATE SEQUENCE SEQ_USUARIO
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE USUARIO (
    ID_USUARIO      BIGINT      NOT NULL,
    USERNAME        VARCHAR(60)    NOT NULL,
    PASSWORD        VARCHAR(200)   NOT NULL,
    ROLE            VARCHAR(20)    NOT NULL,
    ATIVO           CHAR(1)         DEFAULT 'S' NOT NULL,

    CONSTRAINT PK_USUARIO
        PRIMARY KEY (ID_USUARIO),

    CONSTRAINT UK_USUARIO_USERNAME
        UNIQUE (USERNAME),

    CONSTRAINT CK_USUARIO_ROLE
        CHECK (ROLE IN ('ADMIN', 'USER')),

    CONSTRAINT CK_USUARIO_ATIVO
        CHECK (ATIVO IN ('S', 'N'))
);

-- Usuarios padrao de demonstracao.
-- IMPORTANTE: troque as senhas apos a primeira execucao em producao.
-- admin / admin123 (perfil ADMIN: acesso total, inclusive escrita)
-- usuario / user123 (perfil USER: apenas leitura - metodos GET)

INSERT INTO USUARIO (ID_USUARIO, USERNAME, PASSWORD, ROLE, ATIVO)
VALUES (
    nextval('SEQ_USUARIO'),
    'admin',
    '$2b$12$1HMtVrtMXGXBXGbyt3lhweD65MF6Qi2Lcq62Nj2qGwh6RLuL47jDK',
    'ADMIN',
    'S'
);

INSERT INTO USUARIO (ID_USUARIO, USERNAME, PASSWORD, ROLE, ATIVO)
VALUES (
    nextval('SEQ_USUARIO'),
    'usuario',
    '$2b$12$P8Inmzmbu1qDCSFEMM43guTWlXCDU43osOi3BK5durkwh90n0FnpO',
    'USER',
    'S'
);

