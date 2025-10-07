package br.com.organizatec.gestao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;

public interface FuncionarioProprioRepository extends JpaRepository<FuncionarioProprio, Long> {

    Optional<FuncionarioProprio> findByCpf(String cpf);

    Optional<FuncionarioProprio> findByMatricula(String matricula);

    // pega o último funcionário cuja matrícula começa com o prefixo (ex.: "MAT-2025-")
    Optional<FuncionarioProprio> findTopByMatriculaStartingWithOrderByMatriculaDesc(String prefix);
}