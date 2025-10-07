package br.com.organizatec.gestao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;

public interface TerceirizadoRepository extends JpaRepository<Terceirizado, Long> {
}