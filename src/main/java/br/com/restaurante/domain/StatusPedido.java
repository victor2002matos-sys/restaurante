package br.com.restaurante.domain;

/** Ciclo de vida do pedido (RF012). */
public enum StatusPedido {
    AGUARDANDO_PREPARO("Aguardando preparo"),
    EM_PREPARO("Em preparo"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }

    /** Pedido ainda ocupa a mesa. */
    public boolean isAberto() {
        return this != FINALIZADO && this != CANCELADO;
    }

    /** Próximo status no fluxo normal, ou null se não houver. */
    public StatusPedido proximo() {
        return switch (this) {
            case AGUARDANDO_PREPARO -> EM_PREPARO;
            case EM_PREPARO -> PRONTO;
            case PRONTO -> ENTREGUE;
            case ENTREGUE -> FINALIZADO;
            default -> null;
        };
    }
}
