package br.com.organizatec.gestao.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.service.ControleAcessoService;

@RestController
@RequestMapping("/acesso")
public class ControleAcessoController {

    private final ControleAcessoService service;

    public ControleAcessoController(ControleAcessoService service) {
        this.service = service;
    }

    @PostMapping("/entrada/{tipo}/{id}")
    public ResponseEntity<String> registrarEntrada(@PathVariable String tipo, @PathVariable Long id) {
        service.registrarEntrada(id, tipo);
        return ResponseEntity.ok("Entrada registrada para " + tipo + " id=" + id);
    }

    @PostMapping("/saida/{tipo}/{id}")
    public ResponseEntity<String> registrarSaida(@PathVariable String tipo, @PathVariable Long id) {
        service.registrarSaida(id, tipo);
        return ResponseEntity.ok("Saída registrada para " + tipo + " id=" + id);
    }
}