package br.com.restaurante;

import br.com.restaurante.domain.Promocao;
import br.com.restaurante.domain.TipoDesconto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PromocaoTest {

    private Promocao promocao(TipoDesconto tipo, String desconto) {
        Promocao p = new Promocao();
        p.setTipoDesconto(tipo);
        p.setDesconto(new BigDecimal(desconto));
        return p;
    }

    @Test
    void descontoPercentual() {
        assertThat(promocao(TipoDesconto.PERCENTUAL, "15").aplicar(new BigDecimal("42.00")))
                .isEqualByComparingTo("35.70");
    }

    @Test
    void descontoEmValorNaoDeixaPrecoNegativo() {
        assertThat(promocao(TipoDesconto.VALOR, "2").aplicar(new BigDecimal("9.00"))).isEqualByComparingTo("7.00");
        assertThat(promocao(TipoDesconto.VALOR, "50").aplicar(new BigDecimal("9.00"))).isEqualByComparingTo("0.00");
    }
}
