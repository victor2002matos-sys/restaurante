package br.com.restaurante.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados comuns a clientes e funcionários (RF001).
 * Herança JOINED: tabela pessoa + tabelas cliente e funcionario ligadas pelo CPF.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa {

    @Id
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos (somente números)")
    @Column(length = 11)
    private String cpf;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    public String getCpf() { return cpf; }
    /** Aceita CPF com ou sem pontuação e guarda só os dígitos. */
    public void setCpf(String cpf) { this.cpf = cpf == null ? null : cpf.replaceAll("\\D", ""); }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
