package br.com.restaurante.repository;

import br.com.restaurante.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findAllByOrderByDataHoraDesc();

    boolean existsByPedidoId(Long pedidoId);

    @Query("select avg(a.nota) from Avaliacao a")
    Double mediaNotas();
}
