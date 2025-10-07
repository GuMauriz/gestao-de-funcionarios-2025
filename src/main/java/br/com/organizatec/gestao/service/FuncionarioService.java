package br.com.organizatec.gestao.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioProprioRepository repository;

    public FuncionarioService(FuncionarioProprioRepository repository) {
        this.repository = repository;
    }

    /**
     * Regras:
     * - Validações básicas (nome, cpf, cargo, salarioBase, dataContratacao).
     * - CPF único.
     * - Matrícula gerada automaticamente no formato: MAT-YYYY-#### (ex.: MAT-2025-0001).
     */
    @Transactional
    public FuncionarioProprio criarFuncionario(FuncionarioProprio f) {
        validarDadosObrigatorios(f);
        validarCpfUnico(f.getCpf());

        // gera matrícula caso não tenha sido informada, ou se vier vazia
        if (!StringUtils.hasText(f.getMatricula())) {
            String novaMatricula = gerarMatricula();
            f.setMatricula(novaMatricula);
        }

        return repository.save(f);
    }

    private void validarDadosObrigatorios(FuncionarioProprio f) {
        if (!StringUtils.hasText(f.getNome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (!StringUtils.hasText(f.getCpf())) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        if (!StringUtils.hasText(f.getCargo())) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }
        if (f.getSalarioBase() < 0) {
            throw new IllegalArgumentException("Salário base não pode ser negativo.");
        }
        if (f.getDataContratacao() == null) {
            throw new IllegalArgumentException("Data de contratação é obrigatória.");
        }
    }

    private void validarCpfUnico(String cpf) {
        Optional.ofNullable(cpf)
                .flatMap(repository::findByCpf)
                .ifPresent(x -> { throw new IllegalArgumentException("CPF já cadastrado."); });
    }

    /**
     * Gera a próxima matrícula com base no ano atual e na última matrícula existente.
     * Ex.: se última for MAT-2025-0009 -> próxima = MAT-2025-0010
     */
    private String gerarMatricula() {
        int ano = LocalDate.now().getYear();
        String prefixo = "MAT-" + ano + "-";

        // tenta achar a última matrícula daquele ano
        return repository.findTopByMatriculaStartingWithOrderByMatriculaDesc(prefixo)
                .map(FuncionarioProprio::getMatricula)
                .map(ultima -> {
                    String sufixo = ultima.substring(prefixo.length()); // "0009"
                    int seq = Integer.parseInt(sufixo);
                    return prefixo + String.format("%04d", seq + 1);
                })
                .orElse(prefixo + "0001");
    }
}