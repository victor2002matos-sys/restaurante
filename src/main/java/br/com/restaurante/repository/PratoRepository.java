package br.com.restaurante.repository;

import br.com.restaurante.domain.Prato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    List<Prato> findAllByOrderByCategoriaAscNomeAsc();

    List<Prato> findByDisponivelTrueOrderByCategoriaAscNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Query("select distinct p.categoria from Prato p order by p.categoria")
    List<String> findCategorias();
}
