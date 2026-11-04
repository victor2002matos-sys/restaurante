package br.com.restaurante.domain;

public enum TipoDesconto {
    PERCENTUAL("Porcentagem (%)"),
    VALOR("Valor (R$)");

    private final String descricao;

    TipoDesconto(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
