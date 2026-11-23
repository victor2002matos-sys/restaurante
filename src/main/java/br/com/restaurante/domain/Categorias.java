package br.com.restaurante.domain;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Categorias do cardápio, na ordem em que aparecem para o cliente e para o garçom. */
public final class Categorias {

    public static final String ENTRADAS = "Entradas";
    public static final String PRATOS_PRINCIPAIS = "Pratos principais";
    public static final String SOBREMESAS = "Sobremesas";
    public static final String BEBIDAS = "Bebidas";

    /** Ordem das seções; categorias criadas pelo gerente com outro nome vão para o final. */
    public static final List<String> ORDEM = List.of(ENTRADAS, PRATOS_PRINCIPAIS, SOBREMESAS, BEBIDAS);

    private Categorias() {
    }

    /** Agrupa os pratos por categoria, mantendo a ordem acima e a ordem original dentro de cada grupo. */
    public static Map<String, List<Prato>> agrupar(List<Prato> pratos) {
        return pratos.stream()
                .sorted(Comparator.comparingInt((Prato p) -> posicao(p.getCategoria())))
                .collect(Collectors.groupingBy(Prato::getCategoria, LinkedHashMap::new, Collectors.toList()));
    }

    private static int posicao(String categoria) {
        int i = ORDEM.indexOf(categoria);
        return i < 0 ? ORDEM.size() : i;
    }
}
