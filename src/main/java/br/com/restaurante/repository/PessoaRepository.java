package br.com.restaurante.repository;

import br.com.restaurante.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, String> {
}
