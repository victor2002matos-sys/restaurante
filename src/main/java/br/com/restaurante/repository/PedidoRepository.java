package br.com.restaurante.repository;

import br.com.restaurante.domain.Cliente;
import br.com.restaurante.domain.Mesa;
import br.com.restaurante.domain.Pedido;
import br.com.restaurante.domain.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByDataHoraDesc();

    List<Pedido> findByStatusInOrderByDataHoraAsc(Collection<StatusPedido> status);

    boolean existsByMesaAndStatusNotIn(Mesa mesa, Collection<StatusPedido> status);

    boolean existsByMesa(Mesa mesa);

    boolean existsByCliente(Cliente cliente);
}
