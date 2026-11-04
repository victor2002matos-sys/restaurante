package br.com.restaurante.repository;

import br.com.restaurante.domain.ItemPedido;
import br.com.restaurante.domain.Prato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    boolean existsByPrato(Prato prato);
}
