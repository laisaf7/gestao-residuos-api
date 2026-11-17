-- ============================================================
-- V9: Incentivo para entregar no atelie de artesao.
-- A entrega rende +50% de EcoMoedas (calculado no codigo) e vira um
-- cupom de 50% de desconto em um produto daquele atelie.
-- ============================================================

-- 'S' = o cupom desta entrega ainda nao foi usado
ALTER TABLE ENTREGA_QR ADD COLUMN DESCONTO_DISPONIVEL CHAR(1) DEFAULT 'N' NOT NULL;
ALTER TABLE ENTREGA_QR ADD CONSTRAINT CK_ENTREGA_QR_DESCONTO CHECK (DESCONTO_DISPONIVEL IN ('S', 'N'));
