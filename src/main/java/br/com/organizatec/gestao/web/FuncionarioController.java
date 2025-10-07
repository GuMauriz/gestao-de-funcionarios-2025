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
import br.com.organizatec.gestao.service.FuncionarioService;

/**
 * Controller mínimo para demonstrar a persistência via Service.
 */
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    @Autowired
    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

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