package com.esg.residuos.domain.enums;

public enum TipoTransacao {
    CREDITO,
    VOUCHER,
    TRANSFERENCIA,
    /** Comprador pagou um produto de artesao com EcoMoedas. */
    COMPRA,
    /** Artesao recebeu EcoMoedas pela venda de um produto. */
    VENDA
}
