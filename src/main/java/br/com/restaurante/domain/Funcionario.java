package br.com.restaurante.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/** Funcionário do restaurante (RF003). O CPF é usado como login. */
@Entity
public class Funcionario extends Pessoa {

    @NotNull(message = "Função é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Funcao funcao;

    @NotNull(message = "Turno é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Turno turno;

    /** Senha criptografada com BCrypt. */
    @Column(nullable = false)
    private String senha;

    public Funcao getFuncao() { return funcao; }
    public void setFuncao(Funcao funcao) { this.funcao = funcao; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
