package br.com.organizatec.gestao.web;

import java.time.LocalDate;

import static org.hamcrest.Matchers.startsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.service.ControleAcessoService;
import br.com.organizatec.gestao.service.FuncionarioService;
import br.com.organizatec.gestao.service.RelatorioService;

/**
 * Teste de autorização carregando o CONTEXTO COMPLETO da aplicação,
 * garantindo que o SecurityConfig e os filtros sejam aplicados exatamente
 * como em runtime. Não sobe servidor (usa MockMvc).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // mockamos os services para isolar a web+security
    @MockBean private FuncionarioService funcionarioService;
    @MockBean private ControleAcessoService controleAcessoService;
    @MockBean private RelatorioService relatorioService;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("POST /funcionarios: RH = 2xx, Recepcao = 403")
    void testAcessoFuncionariosApenasRH() throws Exception {
        String payload = """
        {
          "nome": "Israel LP",
          "cpf": "111.222.333-44",
          "dataNascimento": "1990-05-20",
          "cargo": "Professor",
          "salarioBase": 10000.0,
          "dataContratacao": "2024-01-10",
          "departamento": "TI"
        }
        """;

        var salvo = new FuncionarioProprio(
                "Israel LP", "111.222.333-44",
                LocalDate.of(1990, 5, 20),
                "MAT-2025-0001",
                "Professor", 10000.0,
                LocalDate.of(2024, 1, 10),
                "TI"
        );
        Mockito.when(funcionarioService.criarFuncionario(any(FuncionarioProprio.class)))
                .thenReturn(salvo);

        // RH → deve passar (200/201)
        mockMvc.perform(post("/funcionarios")
                        .with(user("rh").roles("RH"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().is2xxSuccessful());

        // RECEPCAO → 403
        mockMvc.perform(post("/funcionarios")
                        .with(user("recepcao").roles("RECEPCAO"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /acesso/entrada/terceirizado/1: Recepcao e Seguranca = 200")
    void testAcessoAcessoPermitidoMultiplosPerfis() throws Exception {
        doNothing().when(controleAcessoService).registrarEntrada(1L, "terceirizado");

        mockMvc.perform(post("/acesso/entrada/terceirizado/{id}", 1L)
                        .with(user("recepcao").roles("RECEPCAO"))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/acesso/entrada/terceirizado/{id}", 1L)
                        .with(user("seguranca").roles("SEGURANCA"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /relatorios/circulacao/csv: Seguranca = 200 (CSV), Recepcao = 403")
    void testAcessoRelatoriosApenasRHESeguranca() throws Exception {
        Mockito.when(relatorioService.exportarCirculacaoDiariaCSV())
                .thenReturn("Tipo,Nome,Documento,Status,Hora_Entrada\n");

        // SEGURANCA → 200 + conteúdo CSV
        mockMvc.perform(get("/relatorios/circulacao/csv")
                        .with(user("seguranca").roles("SEGURANCA"))
                        .accept("text/csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("Tipo,Nome,Documento,Status,Hora_Entrada")));

        // RECEPCAO → 403
        mockMvc.perform(get("/relatorios/circulacao/csv")
                        .with(user("recepcao").roles("RECEPCAO"))
                        .accept("text/csv"))
                .andExpect(status().isForbidden());
    }
}