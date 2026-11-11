-- ============================================================
-- V3: Triggers PL/SQL de automacao do ciclo de vida do residuo
-- Sistema de Gestao de Residuos e Reciclagem - Projeto ESG
-- Camada de seguranca/consistencia no banco, complementar as
-- regras de negocio replicadas na aplicacao Spring Boot
-- (DescarteService / ColetaService).
-- ============================================================

-- ============================================================
-- AUTOMACAO 1 - VALIDACAO DO DESCARTE
-- ============================================================

-- ============================================================
-- TRIGGER: VALIDACAO DO DESCARTE
-- ============================================================

CREATE OR REPLACE TRIGGER TRG_VALIDAR_DESCARTE
BEFORE INSERT ON DESCARTE
FOR EACH ROW

DECLARE

    V_TIPO_PONTO
        PONTO_COLETA.ID_TIPO_RESIDUO%TYPE;

    V_NOME_PONTO
        PONTO_COLETA.NOME%TYPE;

    V_STATUS_PONTO
        PONTO_COLETA.STATUS%TYPE;

    V_ATIVO_PONTO
        PONTO_COLETA.ATIVO%TYPE;

    V_DESTINACAO_CORRETA
        TIPO_RESIDUO.DESTINACAO_CORRETA%TYPE;

BEGIN

    SELECT
        ID_TIPO_RESIDUO,
        NOME,
        STATUS,
        ATIVO
    INTO
        V_TIPO_PONTO,
        V_NOME_PONTO,
        V_STATUS_PONTO,
        V_ATIVO_PONTO
    FROM PONTO_COLETA
    WHERE ID_PONTO_COLETA = :NEW.ID_PONTO_COLETA;


    IF V_ATIVO_PONTO = 'N'
       OR V_STATUS_PONTO = 'INATIVO' THEN

        RAISE_APPLICATION_ERROR(
            -20001,
            'O ponto de coleta informado esta inativo.'
        );

    END IF;


    IF V_TIPO_PONTO = :NEW.ID_TIPO_RESIDUO THEN

        :NEW.DESCARTE_CORRETO := 'S';

    ELSE

        :NEW.DESCARTE_CORRETO := 'N';


        SELECT DESTINACAO_CORRETA
        INTO V_DESTINACAO_CORRETA
        FROM TIPO_RESIDUO
        WHERE ID_TIPO_RESIDUO = :NEW.ID_TIPO_RESIDUO;


        IF :NEW.OBSERVACAO IS NULL THEN

            :NEW.OBSERVACAO :=
                'Descarte realizado em ponto inadequado. '
                || V_DESTINACAO_CORRETA;

        ELSE

            :NEW.OBSERVACAO :=
                :NEW.OBSERVACAO
                || ' Descarte realizado em ponto inadequado. '
                || V_DESTINACAO_CORRETA;

        END IF;


        INSERT INTO NOTIFICACAO (
            ID_NOTIFICACAO,
            ID_PONTO_COLETA,
            ID_COLETA,
            TIPO,
            TITULO,
            MENSAGEM,
            DATA_NOTIFICACAO,
            LIDA,
            PRIORIDADE
        )
        VALUES (
            SEQ_NOTIFICACAO.NEXTVAL,
            :NEW.ID_PONTO_COLETA,
            NULL,
            'DESTINACAO_INCORRETA',
            'Descarte realizado em ponto inadequado',
            'O residuo informado nao corresponde ao tipo aceito pelo ponto '
            || V_NOME_PONTO
            || '. '
            || V_DESTINACAO_CORRETA,
            SYSDATE,
            'N',
            'ALTA'
        );

    END IF;


EXCEPTION

    WHEN NO_DATA_FOUND THEN

        RAISE_APPLICATION_ERROR(
            -20002,
            'Ponto de coleta ou tipo de residuo nao encontrado.'
        );

END TRG_VALIDAR_DESCARTE;
/


-- ============================================================
-- AUTOMACAO 2 - ATUALIZACAO DO PONTO DE COLETA
-- ============================================================

-- ============================================================
-- TRIGGER: ATUALIZACAO DO PONTO DE COLETA
-- ============================================================

CREATE OR REPLACE TRIGGER TRG_ATUALIZAR_PONTO_COLETA
AFTER INSERT ON DESCARTE
FOR EACH ROW

BEGIN

    IF :NEW.DESCARTE_CORRETO = 'S' THEN

        UPDATE PONTO_COLETA
        SET
            QUANTIDADE_ATUAL_KG =
                NVL(QUANTIDADE_ATUAL_KG, 0)
                + :NEW.QUANTIDADE_KG,

            PERCENTUAL_OCUPACAO =
                (
                    (
                        NVL(QUANTIDADE_ATUAL_KG, 0)
                        + :NEW.QUANTIDADE_KG
                    )
                    / CAPACIDADE_KG
                ) * 100,

            STATUS =
                CASE

                    WHEN (
                        (
                            NVL(QUANTIDADE_ATUAL_KG, 0)
                            + :NEW.QUANTIDADE_KG
                        )
                        / CAPACIDADE_KG
                    ) * 100 >= 100
                    THEN 'CHEIO'

                    WHEN (
                        (
                            NVL(QUANTIDADE_ATUAL_KG, 0)
                            + :NEW.QUANTIDADE_KG
                        )
                        / CAPACIDADE_KG
                    ) * 100 >= LIMITE_ALERTA_PERCENTUAL
                    THEN 'ALERTA'

                    ELSE 'DISPONIVEL'

                END

        WHERE ID_PONTO_COLETA =
              :NEW.ID_PONTO_COLETA;

    END IF;

END TRG_ATUALIZAR_PONTO_COLETA;
/


-- ============================================================
-- AUTOMACAO 3 - GERACAO AUTOMATICA DE COLETA
-- ============================================================

-- ============================================================
-- TRIGGER: GERACAO AUTOMATICA DE COLETA
-- ============================================================

CREATE OR REPLACE TRIGGER TRG_GERAR_COLETA
AFTER UPDATE OF PERCENTUAL_OCUPACAO ON PONTO_COLETA
FOR EACH ROW

WHEN (
    NEW.PERCENTUAL_OCUPACAO >= NEW.LIMITE_ALERTA_PERCENTUAL
    AND OLD.PERCENTUAL_OCUPACAO < NEW.LIMITE_ALERTA_PERCENTUAL
)

DECLARE

    V_ID_COLETA
        COLETA.ID_COLETA%TYPE;

BEGIN

    V_ID_COLETA := SEQ_COLETA.NEXTVAL;


    INSERT INTO COLETA (
        ID_COLETA,
        ID_PONTO_COLETA,
        DATA_SOLICITACAO,
        DATA_AGENDADA,
        DATA_COLETA,
        QUANTIDADE_COLETADA_KG,
        STATUS,
        DESTINO_FINAL,
        OBSERVACAO
    )
    VALUES (
        V_ID_COLETA,
        :NEW.ID_PONTO_COLETA,
        SYSDATE,
        NULL,
        NULL,
        NULL,
        'SOLICITADA',
        NULL,
        'Coleta solicitada automaticamente devido ao limite de ocupacao do ponto.'
    );


    INSERT INTO NOTIFICACAO (
        ID_NOTIFICACAO,
        ID_PONTO_COLETA,
        ID_COLETA,
        TIPO,
        TITULO,
        MENSAGEM,
        DATA_NOTIFICACAO,
        LIDA,
        PRIORIDADE
    )
    VALUES (
        SEQ_NOTIFICACAO.NEXTVAL,
        :NEW.ID_PONTO_COLETA,
        V_ID_COLETA,
        'LIMITE_COLETA',
        'Limite de ocupacao atingido',
        'O ponto '
        || :NEW.NOME
        || ' atingiu '
        || TO_CHAR(ROUND(:NEW.PERCENTUAL_OCUPACAO, 2))
        || '% de ocupacao. Uma coleta foi solicitada automaticamente.',
        SYSDATE,
        'N',
        'ALTA'
    );

END TRG_GERAR_COLETA;
/


-- ============================================================
-- AUTOMACAO 4 - FINALIZACAO DA COLETA
-- ============================================================

-- ============================================================
-- TRIGGER: FINALIZACAO DA COLETA
-- ============================================================

CREATE OR REPLACE TRIGGER TRG_FINALIZAR_COLETA
BEFORE UPDATE OF STATUS ON COLETA
FOR EACH ROW

WHEN (
    NEW.STATUS = 'CONCLUIDA'
    AND OLD.STATUS <> 'CONCLUIDA'
)

DECLARE

    V_NOME_PONTO
        PONTO_COLETA.NOME%TYPE;

    V_QUANTIDADE_ATUAL
        PONTO_COLETA.QUANTIDADE_ATUAL_KG%TYPE;

BEGIN

    SELECT NOME, QUANTIDADE_ATUAL_KG
    INTO V_NOME_PONTO, V_QUANTIDADE_ATUAL
    FROM PONTO_COLETA
    WHERE ID_PONTO_COLETA = :NEW.ID_PONTO_COLETA;

    :NEW.DATA_COLETA := SYSDATE;
    :NEW.QUANTIDADE_COLETADA_KG := V_QUANTIDADE_ATUAL;

    UPDATE PONTO_COLETA
    SET
        QUANTIDADE_ATUAL_KG = 0,
        PERCENTUAL_OCUPACAO = 0,
        STATUS = 'DISPONIVEL',
        DATA_ULTIMA_COLETA = NVL(:NEW.DATA_COLETA, SYSDATE)
    WHERE ID_PONTO_COLETA = :NEW.ID_PONTO_COLETA;


    INSERT INTO NOTIFICACAO (
        ID_NOTIFICACAO,
        ID_PONTO_COLETA,
        ID_COLETA,
        TIPO,
        TITULO,
        MENSAGEM,
        DATA_NOTIFICACAO,
        LIDA,
        PRIORIDADE
    )
    VALUES (
        SEQ_NOTIFICACAO.NEXTVAL,
        :NEW.ID_PONTO_COLETA,
        :NEW.ID_COLETA,
        'COLETA_CONCLUIDA',
        'Coleta concluida',
        'A coleta do ponto '
        || V_NOME_PONTO
        || ' foi concluida. O ponto esta disponivel para um novo ciclo de armazenamento.',
        SYSDATE,
        'N',
        'MEDIA'
    );
EXCEPTION

    WHEN NO_DATA_FOUND THEN

        RAISE_APPLICATION_ERROR(
            -20003,
            'Ponto de coleta nao encontrado para a coleta informada.'
        );

END TRG_FINALIZAR_COLETA;
/
