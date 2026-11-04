package br.com.restaurante.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/** Promoção com período e desconto em valor ou porcentagem (RF007, RF008). */
@Entity
@Check(constraints = "data_fim >= data_inicio")
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotNull(message = "Data inicial é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "Data final é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @NotNull(message = "Tipo de desconto é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto", nullable = false, length = 20)
    private TipoDesconto tipoDesconto;

    @NotNull(message = "Desconto é obrigatório")
    @DecimalMin(value = "0.01", message = "Desconto deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal desconto;

    @ManyToMany
    @JoinTable(name = "prato_promocao",
            joinColumns = @JoinColumn(name = "promocao_id"),
            inverseJoinColumns = @JoinColumn(name = "prato_id"))
    @OrderBy("nome")
    private Set<Prato> pratos = new LinkedHashSet<>();

    @AssertTrue(message = "Data final deve ser igual ou posterior à data inicial")
    public boolean isPeriodoValido() {
        return dataInicio == null || dataFim == null || !dataFim.isBefore(dataInicio);
    }

    @AssertTrue(message = "Desconto percentual deve ser no máximo 100%")
    public boolean isPercentualValido() {
        return tipoDesconto != TipoDesconto.PERCENTUAL || desconto == null
                || desconto.compareTo(BigDecimal.valueOf(100)) <= 0;
    }

    public boolean isVigente(LocalDate data) {
        return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
    }

    /** Aplica o desconto ao preço, sem deixar o resultado negativo. */
    public BigDecimal aplicar(BigDecimal preco) {
        BigDecimal resultado = tipoDesconto == TipoDesconto.PERCENTUAL
                ? preco.subtract(preco.multiply(desconto).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                : preco.subtract(desconto);
        return resultado.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public void setTipoDesconto(TipoDesconto tipoDesconto) { this.tipoDesconto = tipoDesconto; }
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    public Set<Prato> getPratos() { return pratos; }
    public void setPratos(Set<Prato> pratos) { this.pratos = pratos; }
}
