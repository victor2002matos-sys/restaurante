package br.com.restaurante.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Mesa do restaurante (RF009). */
@Entity
public class Mesa {

    @Id
    @NotNull(message = "Número é obrigatório")
    @Min(value = 1, message = "Número deve ser positivo")
    private Integer numero;

    @Min(value = 1, message = "Capacidade deve ser positiva")
    private Integer capacidade;

    public Mesa() {}

    public Mesa(Integer numero, Integer capacidade) {
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
}
