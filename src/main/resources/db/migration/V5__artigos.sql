-- ============================================================
-- V5: Ideias de reaproveitamento - artigos ensinando a transformar
-- materiais reciclaveis em novos produtos (usuarios podem publicar)
-- ============================================================

CREATE SEQUENCE SEQ_ARTIGO START WITH 1 INCREMENT BY 1;
CREATE TABLE ARTIGO (
    ID_ARTIGO       BIGINT          NOT NULL,
    TITULO          VARCHAR(120)    NOT NULL,
    MATERIAL        VARCHAR(30)     NOT NULL,
    CONTEUDO        VARCHAR(4000)   NOT NULL,
    AUTOR           VARCHAR(50)     NOT NULL,
    DATA_CRIACAO    TIMESTAMP       NOT NULL,
    CONSTRAINT PK_ARTIGO PRIMARY KEY (ID_ARTIGO)
);

INSERT INTO ARTIGO (ID_ARTIGO, TITULO, MATERIAL, CONTEUDO, AUTOR, DATA_CRIACAO) VALUES
(nextval('SEQ_ARTIGO'), 'Bolsa com calça jeans velha', 'Tecido',
 E'Material: 1 calça jeans, tesoura, linha e agulha (ou máquina).\n\n1. Corte as pernas da calça logo abaixo do gancho.\n2. Vire do avesso e costure a barra inferior para fechar o fundo.\n3. Desvire: os bolsos da calça viram os bolsos da bolsa.\n4. Use uma das pernas cortadas para fazer a alça: corte uma tira de 8 cm, dobre ao meio e costure.\n5. Prenda a alça nas laterais por dentro, perto do cós.', 'EcoCiclo', CURRENT_TIMESTAMP),
(nextval('SEQ_ARTIGO'), 'Horta vertical com garrafa PET', 'Plástico',
 E'Material: garrafas PET de 2 L, tesoura, barbante, terra e mudas.\n\n1. Lave as garrafas e retire os rótulos.\n2. Corte uma abertura retangular na lateral de cada garrafa, deitada.\n3. Faça 3 furos pequenos no lado de baixo para drenar a água.\n4. Passe o barbante pelo gargalo e pelo fundo e amarre as garrafas uma abaixo da outra.\n5. Coloque terra, plante as mudas e pendure em local com sol.', 'EcoCiclo', CURRENT_TIMESTAMP),
(nextval('SEQ_ARTIGO'), 'Porta-lápis com lata de alumínio', 'Metal',
 E'Material: latas limpas, lixa, tinta ou retalhos de tecido, cola.\n\n1. Retire a tampa com abridor que não deixa borda cortante.\n2. Lixe a borda superior até ficar lisa.\n3. Pinte ou encape a lata com tecido ou papel colado.\n4. Cole 3 ou 4 latas lado a lado para criar um organizador de mesa.', 'EcoCiclo', CURRENT_TIMESTAMP),
(nextval('SEQ_ARTIGO'), 'Luminária de pote de vidro', 'Vidro',
 E'Material: pote de vidro com tampa, cordão de luz LED a pilha.\n\n1. Lave o pote e retire o rótulo com água morna.\n2. Coloque o cordão de LED dentro, deixando a caixinha de pilhas na tampa.\n3. Para luz difusa, pinte o vidro por fora com cola branca e um pouco de tinta.\n4. Feche e use como luminária de mesa ou decoração.', 'EcoCiclo', CURRENT_TIMESTAMP),
(nextval('SEQ_ARTIGO'), 'Caixa organizadora de papelão', 'Papel',
 E'Material: caixa de papelão, papel de presente ou revista velha, cola, tesoura.\n\n1. Corte a parte de cima da caixa na altura desejada.\n2. Reforce os cantos com fita.\n3. Encape por fora e por dentro com papel colado.\n4. Faça divisórias com tiras de papelão encaixadas em X.', 'EcoCiclo', CURRENT_TIMESTAMP),
(nextval('SEQ_ARTIGO'), 'Prateleira com caixote de feira', 'Madeira',
 E'Material: caixote de madeira, lixa, verniz ou tinta, parafusos e buchas.\n\n1. Retire pregos soltos e lixe todas as farpas.\n2. Pinte ou passe verniz e deixe secar por 24 h.\n3. Fixe na parede com 2 parafusos e buchas pela tábua do fundo.\n4. Use deitado como nicho ou empilhe dois para formar uma estante.', 'EcoCiclo', CURRENT_TIMESTAMP);
