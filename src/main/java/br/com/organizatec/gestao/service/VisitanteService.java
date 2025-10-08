package br.com.organizatec.gestao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.VisitanteRepository;

/**
 * Regras básicas de Visitantes (CRUD essencial).
 */
@Service
public class VisitanteService {

    private final VisitanteRepository repository;

    public VisitanteService(VisitanteRepository repository) {
        this.repository = repository;
    }

    public Visitante criar(Visitante v) {
        return repository.save(v);
    }

    public List<Visitante> listarTodos() {
        return repository.findAll();
    }

    public Optional<Visitante> buscarPorId(Long id) {
        return repository.findById(id);
    }
}