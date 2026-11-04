package br.com.restaurante.domain;

/** Função do funcionário; também define o perfil de acesso (RNF002). */
public enum Funcao {
    GARCOM("Garçom"),
    COZINHEIRO("Cozinheiro"),
    GERENTE("Gerente");

    private final String descricao;

    Funcao(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
