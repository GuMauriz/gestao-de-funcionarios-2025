package br.com.organizatec.gestao.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.service.FuncionarioService;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 🔴 garante 201
    public FuncionarioProprio criar(@RequestBody NovoFuncionarioDto dto) {
        FuncionarioProprio f = new FuncionarioProprio(
                dto.nome(),
                dto.cpf(),
                LocalDate.parse(dto.dataNascimento()),
                null, // matrícula gerada no service
                dto.cargo(),
                dto.salarioBase(),
                LocalDate.parse(dto.dataContratacao()),
                dto.departamento()
        );
        return service.criarFuncionario(f);
    }

    @GetMapping
    public List<FuncionarioProprio> listarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/cpf/{cpf}")
    public FuncionarioProprio porCpf(@PathVariable String cpf) {
        return service.buscarPorCpf(cpf).orElse(null);
    }

    @GetMapping("/matricula/{matricula}")
    public FuncionarioProprio porMatricula(@PathVariable String matricula) {
        return service.buscarPorMatricula(matricula).orElse(null);
    }

    public record NovoFuncionarioDto(
            String nome, String cpf, String dataNascimento,
            String cargo, double salarioBase,
            String dataContratacao, String departamento
    ) {}
}
