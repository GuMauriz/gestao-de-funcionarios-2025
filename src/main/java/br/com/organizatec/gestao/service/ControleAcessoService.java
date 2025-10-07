package br.com.organizatec.gestao.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.organizatec.gestao.domain.comum.RegistravelAcesso;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;

@Service
public class ControleAcessoService {

    private final TerceirizadoRepository terceirizadoRepository;
    private final VisitanteRepository visitanteRepository;

    public ControleAcessoService(TerceirizadoRepository terceirizadoRepository,
                                 VisitanteRepository visitanteRepository) {
        this.terceirizadoRepository = terceirizadoRepository;
        this.visitanteRepository = visitanteRepository;
    }

    @Transactional
    public void registrarEntrada(Long id, String tipoPessoa) {
        RegistravelAcesso alvo = localizarRegistravel(id, tipoPessoa);
        alvo.registrarEntrada(LocalDateTime.now());
        persistir(alvo);
    }

    @Transactional
    public void registrarSaida(Long id, String tipoPessoa) {
        RegistravelAcesso alvo = localizarRegistravel(id, tipoPessoa);
        alvo.registrarSaida(LocalDateTime.now());
        persistir(alvo);
    }

    // --- helpers ---
    private RegistravelAcesso localizarRegistravel(Long id, String tipo) {
        String t = tipo == null ? "" : tipo.trim().toLowerCase();
        return switch (t) {
            case "terceirizado" -> terceirizadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Terceirizado não encontrado: id=" + id));
            case "visitante" -> visitanteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Visitante não encontrado: id=" + id));
            default -> throw new IllegalArgumentException("Tipo inválido. Use 'terceirizado' ou 'visitante'.");
        };
    }

    private void persistir(RegistravelAcesso alvo) {
        if (alvo instanceof Terceirizado t) {
            terceirizadoRepository.save(t);
        } else if (alvo instanceof Visitante v) {
            visitanteRepository.save(v);
        } else {
            throw new IllegalStateException("Tipo não suportado: " + alvo.getClass().getSimpleName());
        }
    }
}