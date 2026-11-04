package br.com.restaurante.repository;

import br.com.restaurante.domain.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    List<Promocao> findAllByOrderByDataInicioDesc();

    @Query("select pr from Promocao pr where :data between pr.dataInicio and pr.dataFim")
    List<Promocao> findVigentes(@Param("data") LocalDate data);
}
