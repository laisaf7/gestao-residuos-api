-- ============================================================
-- V2: Dados iniciais (seed) - 5 registros por tabela
-- Sistema de Gestao de Residuos e Reciclagem - Projeto ESG
-- ============================================================


-- ============================================================
-- INSERCAO DE DADOS DE TESTE
-- ============================================================


-- ============================================================
-- TIPO_RESIDUO
-- ============================================================

INSERT INTO TIPO_RESIDUO (
    ID_TIPO_RESIDUO,
    NOME,
    CATEGORIA,
    COR_IDENTIFICACAO,
    DESTINACAO_CORRETA,
    IMPACTO_AMBIENTAL,
    ATIVO
)
VALUES (
    nextval('SEQ_TIPO_RESIDUO'),
    'PAPEL',
    'RECICLAVEL',
    'AZUL',
    'Encaminhar para coleta seletiva de papel e posterior reciclagem.',
    'O descarte inadequado aumenta o volume de residuos enviados para aterros.',
    'S'
);


INSERT INTO TIPO_RESIDUO (
    ID_TIPO_RESIDUO,
    NOME,
    CATEGORIA,
    COR_IDENTIFICACAO,
    DESTINACAO_CORRETA,
    IMPACTO_AMBIENTAL,
    ATIVO
)
VALUES (
    nextval('SEQ_TIPO_RESIDUO'),
    'PLASTICO',
    'RECICLAVEL',
    'VERMELHO',
    'Encaminhar para coleta seletiva de plastico e posterior reciclagem.',
    'O descarte inadequado pode provocar acumulacao de residuos no ambiente.',
    'S'
);


INSERT INTO TIPO_RESIDUO (
    ID_TIPO_RESIDUO,
    NOME,
    CATEGORIA,
    COR_IDENTIFICACAO,
    DESTINACAO_CORRETA,
    IMPACTO_AMBIENTAL,
    ATIVO
)
VALUES (
    nextval('SEQ_TIPO_RESIDUO'),
    'VIDRO',
    'RECICLAVEL',
    'VERDE',
    'Encaminhar para ponto de coleta destinado a materiais de vidro.',
    'O descarte inadequado pode causar acidentes e aumentar residuos no ambiente.',
    'S'
);


INSERT INTO TIPO_RESIDUO (
    ID_TIPO_RESIDUO,
    NOME,
    CATEGORIA,
    COR_IDENTIFICACAO,
    DESTINACAO_CORRETA,
    IMPACTO_AMBIENTAL,
    ATIVO
)
VALUES (
    nextval('SEQ_TIPO_RESIDUO'),
    'METAL',
    'RECICLAVEL',
    'AMARELO',
    'Encaminhar para coleta seletiva de metais e posterior reciclagem.',
    'A separacao inadequada dificulta o reaproveitamento do material.',
    'S'
);


INSERT INTO TIPO_RESIDUO (
    ID_TIPO_RESIDUO,
    NOME,
    CATEGORIA,
    COR_IDENTIFICACAO,
    DESTINACAO_CORRETA,
    IMPACTO_AMBIENTAL,
    ATIVO
)
VALUES (
    nextval('SEQ_TIPO_RESIDUO'),
    'ELETRONICO',
    'ESPECIAL',
    'LARANJA',
    'Encaminhar para ponto especifico de coleta de residuos eletronicos.',
    'Equipamentos eletronicos necessitam de destinacao adequada para evitar contaminacao ambiental.',
    'S'
);


-- ============================================================
-- PONTO_COLETA
-- ============================================================

INSERT INTO PONTO_COLETA (
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    NOME,
    DESCRICAO,
    ENDERECO,
    BAIRRO,
    CIDADE,
    ESTADO,
    CEP,
    CAPACIDADE_KG,
    QUANTIDADE_ATUAL_KG,
    PERCENTUAL_OCUPACAO,
    LIMITE_ALERTA_PERCENTUAL,
    STATUS,
    DATA_INSTALACAO,
    DATA_ULTIMA_COLETA,
    ATIVO
)
VALUES (
    nextval('SEQ_PONTO_COLETA'),
    1,
    'Ponto de Papel - Administrativo',
    'Ponto destinado ao descarte de papel do setor administrativo.',
    'Avenida Principal, 100',
    'Centro',
    'Sao Paulo',
    'SP',
    '01000-000',
    100,
    30,
    30,
    80,
    'DISPONIVEL',
    TO_DATE('10/01/2026', 'DD/MM/YYYY'),
    TO_DATE('20/09/2026', 'DD/MM/YYYY'),
    'S'
);


INSERT INTO PONTO_COLETA (
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    NOME,
    DESCRICAO,
    ENDERECO,
    BAIRRO,
    CIDADE,
    ESTADO,
    CEP,
    CAPACIDADE_KG,
    QUANTIDADE_ATUAL_KG,
    PERCENTUAL_OCUPACAO,
    LIMITE_ALERTA_PERCENTUAL,
    STATUS,
    DATA_INSTALACAO,
    DATA_ULTIMA_COLETA,
    ATIVO
)
VALUES (
    nextval('SEQ_PONTO_COLETA'),
    2,
    'Ponto de Plastico - Refeitorio',
    'Ponto destinado ao descarte de materiais plasticos.',
    'Avenida Principal, 100',
    'Centro',
    'Sao Paulo',
    'SP',
    '01000-000',
    120,
    48,
    40,
    80,
    'DISPONIVEL',
    TO_DATE('10/01/2026', 'DD/MM/YYYY'),
    TO_DATE('18/09/2026', 'DD/MM/YYYY'),
    'S'
);


INSERT INTO PONTO_COLETA (
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    NOME,
    DESCRICAO,
    ENDERECO,
    BAIRRO,
    CIDADE,
    ESTADO,
    CEP,
    CAPACIDADE_KG,
    QUANTIDADE_ATUAL_KG,
    PERCENTUAL_OCUPACAO,
    LIMITE_ALERTA_PERCENTUAL,
    STATUS,
    DATA_INSTALACAO,
    DATA_ULTIMA_COLETA,
    ATIVO
)
VALUES (
    nextval('SEQ_PONTO_COLETA'),
    3,
    'Ponto de Vidro - Copa',
    'Ponto destinado ao descarte de recipientes de vidro.',
    'Rua das Flores, 250',
    'Jardim Central',
    'Sao Paulo',
    'SP',
    '02000-000',
    80,
    20,
    25,
    75,
    'DISPONIVEL',
    TO_DATE('15/02/2026', 'DD/MM/YYYY'),
    TO_DATE('15/09/2026', 'DD/MM/YYYY'),
    'S'
);


INSERT INTO PONTO_COLETA (
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    NOME,
    DESCRICAO,
    ENDERECO,
    BAIRRO,
    CIDADE,
    ESTADO,
    CEP,
    CAPACIDADE_KG,
    QUANTIDADE_ATUAL_KG,
    PERCENTUAL_OCUPACAO,
    LIMITE_ALERTA_PERCENTUAL,
    STATUS,
    DATA_INSTALACAO,
    DATA_ULTIMA_COLETA,
    ATIVO
)
VALUES (
    nextval('SEQ_PONTO_COLETA'),
    4,
    'Ponto de Metal - Manutencao',
    'Ponto destinado ao descarte de materiais metalicos.',
    'Rua Industrial, 400',
    'Distrito Industrial',
    'Sao Paulo',
    'SP',
    '03000-000',
    100,
    40,
    40,
    80,
    'DISPONIVEL',
    TO_DATE('20/02/2026', 'DD/MM/YYYY'),
    TO_DATE('12/09/2026', 'DD/MM/YYYY'),
    'S'
);


INSERT INTO PONTO_COLETA (
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    NOME,
    DESCRICAO,
    ENDERECO,
    BAIRRO,
    CIDADE,
    ESTADO,
    CEP,
    CAPACIDADE_KG,
    QUANTIDADE_ATUAL_KG,
    PERCENTUAL_OCUPACAO,
    LIMITE_ALERTA_PERCENTUAL,
    STATUS,
    DATA_INSTALACAO,
    DATA_ULTIMA_COLETA,
    ATIVO
)
VALUES (
    nextval('SEQ_PONTO_COLETA'),
    5,
    'Ponto de Eletronicos - Tecnologia',
    'Ponto destinado ao descarte de equipamentos e componentes eletronicos.',
    'Rua da Tecnologia, 500',
    'Centro Empresarial',
    'Sao Paulo',
    'SP',
    '04000-000',
    50,
    10,
    20,
    70,
    'DISPONIVEL',
    TO_DATE('05/03/2026', 'DD/MM/YYYY'),
    TO_DATE('10/09/2026', 'DD/MM/YYYY'),
    'S'
);


-- ============================================================
-- DESCARTE
-- ============================================================

INSERT INTO DESCARTE (
    ID_DESCARTE,
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    DATA_DESCARTE,
    QUANTIDADE_KG,
    ORIGEM,
    DESCARTE_CORRETO,
    OBSERVACAO
)
VALUES (
    nextval('SEQ_DESCARTE'),
    1,
    1,
    TO_DATE('25/09/2026 09:10', 'DD/MM/YYYY HH24:MI'),
    30,
    'Administrativo',
    'S',
    'Descarte de documentos e papeis utilizados.'
);


INSERT INTO DESCARTE (
    ID_DESCARTE,
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    DATA_DESCARTE,
    QUANTIDADE_KG,
    ORIGEM,
    DESCARTE_CORRETO,
    OBSERVACAO
)
VALUES (
    nextval('SEQ_DESCARTE'),
    2,
    2,
    TO_DATE('25/09/2026 12:30', 'DD/MM/YYYY HH24:MI'),
    48,
    'Refeitorio',
    'S',
    'Descarte de embalagens plasticas.'
);


INSERT INTO DESCARTE (
    ID_DESCARTE,
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    DATA_DESCARTE,
    QUANTIDADE_KG,
    ORIGEM,
    DESCARTE_CORRETO,
    OBSERVACAO
)
VALUES (
    nextval('SEQ_DESCARTE'),
    3,
    3,
    TO_DATE('26/09/2026 15:20', 'DD/MM/YYYY HH24:MI'),
    20,
    'Copa',
    'S',
    'Descarte de recipientes de vidro.'
);


INSERT INTO DESCARTE (
    ID_DESCARTE,
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    DATA_DESCARTE,
    QUANTIDADE_KG,
    ORIGEM,
    DESCARTE_CORRETO,
    OBSERVACAO
)
VALUES (
    nextval('SEQ_DESCARTE'),
    4,
    4,
    TO_DATE('27/09/2026 10:45', 'DD/MM/YYYY HH24:MI'),
    40,
    'Manutencao',
    'S',
    'Descarte de pecas e componentes metalicos.'
);


INSERT INTO DESCARTE (
    ID_DESCARTE,
    ID_PONTO_COLETA,
    ID_TIPO_RESIDUO,
    DATA_DESCARTE,
    QUANTIDADE_KG,
    ORIGEM,
    DESCARTE_CORRETO,
    OBSERVACAO
)
VALUES (
    nextval('SEQ_DESCARTE'),
    5,
    5,
    TO_DATE('28/09/2026 14:15', 'DD/MM/YYYY HH24:MI'),
    10,
    'Tecnologia',
    'S',
    'Descarte de pequenos componentes eletronicos.'
);


-- ============================================================
-- COLETA
-- ============================================================

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
    nextval('SEQ_COLETA'),
    1,
    TO_DATE('19/09/2026', 'DD/MM/YYYY'),
    TO_DATE('20/09/2026', 'DD/MM/YYYY'),
    TO_DATE('20/09/2026', 'DD/MM/YYYY'),
    82,
    'CONCLUIDA',
    'Central de triagem de materiais reciclaveis',
    'Coleta de papel concluida.'
);


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
    nextval('SEQ_COLETA'),
    2,
    TO_DATE('17/09/2026', 'DD/MM/YYYY'),
    TO_DATE('18/09/2026', 'DD/MM/YYYY'),
    TO_DATE('18/09/2026', 'DD/MM/YYYY'),
    98,
    'CONCLUIDA',
    'Cooperativa de reciclagem',
    'Coleta de plastico concluida.'
);


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
    nextval('SEQ_COLETA'),
    3,
    TO_DATE('14/09/2026', 'DD/MM/YYYY'),
    TO_DATE('15/09/2026', 'DD/MM/YYYY'),
    TO_DATE('15/09/2026', 'DD/MM/YYYY'),
    62,
    'CONCLUIDA',
    'Central de reciclagem de vidro',
    'Coleta de vidro concluida.'
);


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
    nextval('SEQ_COLETA'),
    4,
    TO_DATE('11/09/2026', 'DD/MM/YYYY'),
    TO_DATE('12/09/2026', 'DD/MM/YYYY'),
    TO_DATE('12/09/2026', 'DD/MM/YYYY'),
    55,
    'CONCLUIDA',
    'Ferro-velho e sucata metalica',
    'Coleta de metal concluida.'
);


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
    nextval('SEQ_COLETA'),
    5,
    TO_DATE('09/09/2026', 'DD/MM/YYYY'),
    TO_DATE('10/09/2026', 'DD/MM/YYYY'),
    TO_DATE('10/09/2026', 'DD/MM/YYYY'),
    12,
    'CONCLUIDA',
    'Ponto de descarte de eletronicos licenciado',
    'Coleta de eletronicos concluida.'
);


-- ============================================================
-- NOTIFICACAO
-- ============================================================

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
    nextval('SEQ_NOTIFICACAO'),
    1,
    1,
    'COLETA_CONCLUIDA',
    'Coleta concluida',
    'A coleta de papel do ponto Ponto de Papel - Administrativo foi concluida.',
    TO_DATE('20/09/2026', 'DD/MM/YYYY'),
    'S',
    'MEDIA'
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
    nextval('SEQ_NOTIFICACAO'),
    2,
    2,
    'COLETA_CONCLUIDA',
    'Coleta concluida',
    'A coleta de plastico do ponto Ponto de Plastico - Refeitorio foi concluida.',
    TO_DATE('18/09/2026', 'DD/MM/YYYY'),
    'S',
    'MEDIA'
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
    nextval('SEQ_NOTIFICACAO'),
    5,
    5,
    'COLETA_CONCLUIDA',
    'Coleta concluida',
    'A coleta de eletronicos do ponto Ponto de Eletronicos - Tecnologia foi concluida.',
    TO_DATE('10/09/2026', 'DD/MM/YYYY'),
    'S',
    'MEDIA'
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
    nextval('SEQ_NOTIFICACAO'),
    3,
    3,
    'COLETA_CONCLUIDA',
    'Coleta concluida',
    'A coleta de vidro do ponto Ponto de Vidro - Copa foi concluida.',
    TO_DATE('15/09/2026', 'DD/MM/YYYY'),
    'S',
    'MEDIA'
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
    nextval('SEQ_NOTIFICACAO'),
    4,
    4,
    'COLETA_CONCLUIDA',
    'Coleta concluida',
    'A coleta de metal do ponto Ponto de Metal - Manutencao foi concluida.',
    TO_DATE('12/09/2026', 'DD/MM/YYYY'),
    'S',
    'MEDIA'
);

