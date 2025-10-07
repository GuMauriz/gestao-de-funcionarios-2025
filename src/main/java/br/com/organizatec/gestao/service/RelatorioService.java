package br.com.organizatec.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;

@Service
public class RelatorioService {

    private final TerceirizadoRepository terceirizadoRepository;
    private final VisitanteRepository visitanteRepository;

    public RelatorioService(TerceirizadoRepository terceirizadoRepository,
                            VisitanteRepository visitanteRepository) {
        this.terceirizadoRepository = terceirizadoRepository;
        this.visitanteRepository = visitanteRepository;
    }

    /**
     * Imprime no console um resumo da circulação diária.
     * Formato:
     * [Tipo] Nome  |  Status: Dentro/Fora
     */
    @Transactional(readOnly = true)
    public void gerarRelatorioCirculacaoDiaria() {
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        System.out.println("=== RELATÓRIO: CIRCULAÇÃO DIÁRIA ===");
        terceirizados.forEach(t -> {
            String status = (t.getDataHoraEntrada() != null && t.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            System.out.printf("[Terceirizado] %s | Status: %s%n", t.getNome(), status);
        });
        visitantes.forEach(v -> {
            String status = (v.getDataHoraEntrada() != null && v.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            System.out.printf("[Visitante] %s | Status: %s%n", v.getNomeCompleto(), status);
        });
        System.out.println("=== FIM DO RELATÓRIO ===");
    }
}