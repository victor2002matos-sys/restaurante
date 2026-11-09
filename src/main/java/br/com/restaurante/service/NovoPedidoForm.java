package br.com.restaurante.service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Dados do formulário de abertura de pedido (UC-1). */
public class NovoPedidoForm {

    private Integer mesaNumero;
    private String clienteCpf;
    private String clienteNome;
    /** id do prato -> quantidade; pratos com quantidade vazia ou zero são ignorados. */
    private Map<Long, Integer> quantidades = new LinkedHashMap<>();

    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public String getClienteCpf() { return clienteCpf; }
    public void setClienteCpf(String clienteCpf) { this.clienteCpf = clienteCpf; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public Map<Long, Integer> getQuantidades() { return quantidades; }
    public void setQuantidades(Map<Long, Integer> quantidades) { this.quantidades = quantidades; }
}
