package br.com.organizatec.gestao.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.service.FuncionarioService;

/**
 * Controller responsável pelos endpoints de CRUD de Funcionários Próprios.
 * - POST /funcionarios          → cria novo funcionário (matrícula automática)
 * - GET  /funcionarios          → lista todos
 * - GET  /funcionarios/cpf/{cpf}        → busca por CPF
 * - GET  /funcionarios/matricula/{matricula} → busca por matrícula
 */
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    @Autowired
    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    // --- CRIAÇÃO (usa matrícula automática no Service) ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioProprio criar(@RequestBody NovoFuncionarioDto dto) {
        FuncionarioProprio f = new FuncionarioProprio(
                dto.nome(),
                dto.cpf(),
                LocalDate.parse(dto.dataNascimento()),
                null, // matrícula será gerada automaticamente pelo Service
                dto.cargo(),
                dto.salarioBase(),
                LocalDate.parse(dto.dataContratacao()),
                dto.departamento()
        );
        return service.criarFuncionario(f);
    }

    // --- CONSULTAS ---
    @GetMapping
    public List<FuncionarioProprio> listarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<FuncionarioProprio> buscarPorCpf(@PathVariable("cpf") String cpf) {
        return service.buscarPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<FuncionarioProprio> buscarPorMatricula(@PathVariable("matricula") String matricula) {
        return service.buscarPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO mínimo de entrada
    public record NovoFuncionarioDto(
            String nome,
            String cpf,
            String dataNascimento,
            String cargo,
            double salarioBase,
            String dataContratacao,
            String departamento
    ) {}
}