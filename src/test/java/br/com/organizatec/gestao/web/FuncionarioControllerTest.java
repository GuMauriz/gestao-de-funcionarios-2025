package br.com.organizatec.gestao.web;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.service.FuncionarioService;

@WebMvcTest(controllers = FuncionarioController.class)
@AutoConfigureMockMvc
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FuncionarioService service;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @WithMockUser(username = "rh", roles = {"RH"})
    @DisplayName("GET /funcionarios/cpf/{cpf} deve retornar 200 e o JSON do funcionário")
    void getPorCpf_deveRetornar200() throws Exception {
        var f = new FuncionarioProprio(
                "Ana Souza",
                "123.456.789-00",
                LocalDate.of(1995, 2, 10),
                "MAT-2025-0001",
                "Analista",
                4500.0,
                LocalDate.of(2024, 1, 15),
                "TI"
        );

        Mockito.when(service.buscarPorCpf("123.456.789-00"))
                .thenReturn(Optional.of(f));

        mockMvc.perform(get("/funcionarios/cpf/{cpf}", "123.456.789-00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("Ana Souza")))
                .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
                .andExpect(jsonPath("$.cargo", is("Analista")))
                .andExpect(jsonPath("$.matricula", is("MAT-2025-0001")));
    }

    @Test
    @WithMockUser(username = "rh", roles = {"RH"})
    @DisplayName("POST /funcionarios deve retornar 201 Created")
    void postCriacao_deveRetornar201() throws Exception {
        // entrada (DTO sem matrícula)
        String payload = """
        {
          "nome": "Carlos Silva",
          "cpf": "111.222.333-44",
          "dataNascimento": "1990-05-20",
          "cargo": "Gerente",
          "salarioBase": 5000.0,
          "dataContratacao": "2024-01-10",
          "departamento": "TI"
        }
        """;

        // saída simulada
        var salvo = new FuncionarioProprio(
                "Carlos Silva",
                "111.222.333-44",
                LocalDate.of(1990, 5, 20),
                "MAT-2025-0002",
                "Gerente",
                5000.0,
                LocalDate.of(2024, 1, 10),
                "TI"
        );

        Mockito.when(service.criarFuncionario(any(FuncionarioProprio.class)))
                .thenReturn(salvo);

        mockMvc.perform(post("/funcionarios")
                        .with(csrf()) // ✅ adiciona token CSRF para POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf", is("111.222.333-44")))
                .andExpect(jsonPath("$.matricula", is("MAT-2025-0002")));
    }
}