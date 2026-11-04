package br.com.restaurante.repository;

import br.com.restaurante.domain.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    List<Ingrediente> findAllByOrderByNomeAsc();
    boolean existsByNomeIgnoreCase(String nome);
}
