package br.com.organizatec.gestao.web;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;

/**
 * Controller mínimo só para demonstrar a persistência.
 * POST /funcionarios cria e salva um FuncionarioProprio.
 *
 * Exemplo de JSON:
 * {
 *   "nome": "Ana Souza",
 *   "cpf": "123.456.789-00",
 *   "dataNascimento": "1995-02-10",
 *   "matricula": "MAT-0001",
 *   "cargo": "Analista",
 *   "salarioBase": 4500.0,
 *   "dataContratacao": "2024-01-15",
 *   "departamento": "TI"
 * }
 */
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioProprioRepository repository;

    @Autowired
    public FuncionarioController(FuncionarioProprioRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioProprio criar(@RequestBody NovoFuncionarioDto dto) {
        FuncionarioProprio f = new FuncionarioProprio(
                dto.nome(),
                dto.cpf(),
                LocalDate.parse(dto.dataNascimento()),
                dto.matricula(),
                dto.cargo(),
                dto.salarioBase(),
                LocalDate.parse(dto.dataContratacao()),
                dto.departamento()
        );
        return repository.save(f);
    }

    /**
     * DTO mínimo para entrada. Usamos record para reduzir boilerplate.
     */
    public record NovoFuncionarioDto(
            String nome,
            String cpf,
            String dataNascimento,
            String matricula,
            String cargo,
            double salarioBase,
            String dataContratacao,
            String departamento
    ) {}
}