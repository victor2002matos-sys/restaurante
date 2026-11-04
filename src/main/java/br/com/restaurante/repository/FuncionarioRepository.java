package br.com.restaurante.repository;

import br.com.restaurante.domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    List<Funcionario> findAllByOrderByNomeAsc();
}
