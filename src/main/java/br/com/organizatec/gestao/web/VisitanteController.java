package br.com.organizatec.gestao.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.service.VisitanteService;

/**
 * Endpoints REST de Visitantes (ROLE_RECEPCAO).
 */
@RestController
@RequestMapping("/visitantes")
public class VisitanteController {

    private final VisitanteService service;

    public VisitanteController(VisitanteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Visitante criar(@RequestBody NovoVisitanteDto dto) {
        Visitante v = new Visitante(dto.nome(), dto.cpf(), dto.empresaOrigem(), dto.motivoVisita());
        return service.criar(v);
    }

    @GetMapping
    public List<Visitante> listarTodos() {
        return service.listarTodos();
    }

    // DTO mínimo
    public record NovoVisitanteDto(
            String nome,
            String cpf,
            String empresaOrigem,
            String motivoVisita
    ) {}
}