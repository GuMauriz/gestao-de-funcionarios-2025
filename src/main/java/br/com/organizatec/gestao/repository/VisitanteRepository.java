package br.com.organizatec.gestao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.organizatec.gestao.domain.visitantes.Visitante;

public interface VisitanteRepository extends JpaRepository<Visitante, Long> {
}