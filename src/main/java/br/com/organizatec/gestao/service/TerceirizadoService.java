package br.com.organizatec.gestao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;

/**
 * Regras básicas de Terceirizados (CRUD essencial).
 */
@Service
public class TerceirizadoService {

    private final TerceirizadoRepository repository;

    public TerceirizadoService(TerceirizadoRepository repository) {
        this.repository = repository;
    }

    public Terceirizado criar(Terceirizado t) {
        return repository.save(t);
    }

    public List<Terceirizado> listarTodos() {
        return repository.findAll();
    }

    public Optional<Terceirizado> buscarPorId(Long id) {
        return repository.findById(id);
    }
}