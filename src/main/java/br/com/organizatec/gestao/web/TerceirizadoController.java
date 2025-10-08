package br.com.organizatec.gestao.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.service.TerceirizadoService;

/**
 * Endpoints REST de Terceirizados (ROLE_RH).
 */
@RestController
@RequestMapping("/terceirizados")
public class TerceirizadoController {

    private final TerceirizadoService service;

    public TerceirizadoController(TerceirizadoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Terceirizado criar(@RequestBody NovoTerceirizadoDto dto) {
        Terceirizado t = new Terceirizado(dto.nome(), dto.cpf(), dto.empresaOrigem(), dto.setor());
        return service.criar(t);
    }

    @GetMapping
    public List<Terceirizado> listarTodos() {
        return service.listarTodos();
    }

    // DTO mínimo
    public record NovoTerceirizadoDto(
            String nome,
            String cpf,
            String empresaOrigem,
            String setor
    ) {}
}