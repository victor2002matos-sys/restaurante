package br.com.restaurante.repository;

import br.com.restaurante.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    List<Cliente> findAllByOrderByNomeAsc();
}
