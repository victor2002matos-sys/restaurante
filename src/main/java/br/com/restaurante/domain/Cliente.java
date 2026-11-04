package br.com.restaurante.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

/** Cliente do restaurante com suas preferências (RF002). */
@Entity
public class Cliente extends Pessoa {

    @Size(max = 255)
    private String preferencias;

    @Size(max = 255)
    private String alergias;

    @Column(name = "mesa_preferida")
    private Integer mesaPreferida;

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    public Integer getMesaPreferida() { return mesaPreferida; }
    public void setMesaPreferida(Integer mesaPreferida) { this.mesaPreferida = mesaPreferida; }
}
