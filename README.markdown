# Gestão de Funcionários — Organizatec

Sistema acadêmico de **gestão de pessoas** com foco em modelagem **POO**, **persistência** (JPA/H2), **camadas** (Repository/Service/Web), **regras de negócio**, **relatórios** (CSV/PDF), **segurança** (Spring Security por perfil) e **testes** (JUnit 5 + MockMvc).

## 🧭 Estrutura Geral

```
src
├─ main
│  ├─ java/br/com/organizatec/gestao
│  │  ├─ config/            # Segurança, filtros de log (MDC)
│  │  ├─ domain/            # Entidades e interfaces (POO)
│  │  ├─ repository/        # JPA Repositories
│  │  ├─ service/           # Regras de negócio
│  │  └─ web/               # Controllers (REST)
│  └─ resources
│     ├─ application.yml
│     └─ db/migration/...   # (Se usar Flyway)
└─ test
   └─ java/...              # Testes unitários e de autorização
```

---

## 🚀 Como executar

### 1) Build e testes
```bash
mvn clean package
```

### 2) Subir a aplicação (porta padrão 8080)
```bash
mvn spring-boot:run
```

### 3) Banco de dados
- **H2 em memória** (dev/test).  
- Console H2: `/h2-console` (restrito a ROLE_RH; use usuário `rh/rh123`).  
- JDBC URL comum: `jdbc:h2:mem:testdb`.

---

## 🔐 Usuários (em memória) e permissões

| Usuário     | Senha         | Perfis (roles)     | Acesso                                                                                     |
|-------------|---------------|--------------------|--------------------------------------------------------------------------------------------|
| `rh`        | `rh123`       | `ROLE_RH`          | **/funcionarios/**, **/terceirizados/**, **/relatorios/**, `/h2-console`                   |
| `recepcao`  | `recepcao123` | `ROLE_RECEPCAO`    | **/visitantes/**, **/acesso/**                                                             |
| `seguranca` | `seg123`      | `ROLE_SEGURANCA`   | **/acesso/**, **/relatorios/**                                                             |

> Política de segurança implementada em `SecurityConfig`:
>
> - `/funcionarios` e `/terceirizados`: apenas **RH**  
> - `/visitantes`: apenas **RECEPCAO**  
> - `/acesso`: **RECEPCAO** e **SEGURANCA**  
> - `/relatorios`: **RH** e **SEGURANCA**  
> - `/h2-console`: **RH** (em produção, desabilitar)

---

## 📡 Endpoints principais

### Funcionários Próprios (ROLE_RH)
- `POST /funcionarios` — cria funcionário (matrícula gerada no Service)  
  ```json
  {
    "nome":"Carlos Silva",
    "cpf":"111.222.333-44",
    "dataNascimento":"1990-05-20",
    "cargo":"Analista",
    "salarioBase":5000.0,
    "dataContratacao":"2024-01-10",
    "departamento":"TI"
  }
  ```
- `GET /funcionarios` — lista todos  
- `GET /funcionarios/cpf/{cpf}` — busca por CPF  
- `GET /funcionarios/matricula/{matricula}` — busca por matrícula

### Terceirizados (ROLE_RH)
- CRUD simples (conforme evolução do módulo)

### Visitantes (ROLE_RECEPCAO)
- CRUD simples (conforme evolução do módulo)

### Controle de Acesso (ROLE_RECEPCAO, ROLE_SEGURANCA)
- `POST /acesso/entrada/{tipo}/{id}` — registra entrada (tipo = `terceirizado`/`visitante`)  
- `POST /acesso/saida/{tipo}/{id}` — registra saída

### Relatórios (ROLE_RH, ROLE_SEGURANCA)
- `GET /relatorios/circulacao/csv` — exporta CSV (conteúdo em texto, `text/csv`)  
- `GET /relatorios/circulacao/pdf` — exporta PDF (OpenPDF) com tabela simples

---

## 🧪 Testes

- **Persistência (H2 + JPA):** `@DataJpaTest` para os Repositories.  
- **Service:** testes com Mockito das regras (ex.: matrícula automática, salário `Gerente` +20%).  
- **Controller + Security:** `AuthorizationIntegrationTest` valida 2xx/403 por perfil com `MockMvc`.  
- Rodar todos os testes:
  ```bash
  mvn clean test
  ```
- Rodar apenas os testes de autorização:
  ```bash
  mvn -Dtest=AuthorizationIntegrationTest test
  ```

---

## 🧰 Tecnologias

- **Java 21**, **Spring Boot** (Web, Data JPA, Security), **H2**, **Flyway** (opcional)  
- **OpenPDF** para PDF  
- **JUnit 5**, **Mockito**, **MockMvc**  
- **Logback** com **MDC** (imprime `user` e `ip` em cada linha de log)

---

## 📄 LGPD & Logs

- Filtro `MdcLoggingFilter` injeta usuário e IP no MDC.  
- `logback-spring.xml` imprime `user=%X{user} ip=%X{ip}` em cada log.  
- Dados sensíveis de pessoas (CPF) aparecem apenas onde necessário.

---

## ✍️ Autoria

Projeto desenvolvido para fins acadêmicos pelos alunos do sexto semestre de Engenharia da Computação (2°Semestre - 2025) - FESA: Alex Saifi, Gustavo Mauriz, Nicolas Gomes - EM nome da empresa **Organizatec**.  
Módulos e código documentados em JavaDoc conforme boas práticas.

---

## ✅ Status

✔️ Projeto **100% funcional**, **seguro**, **testado** e **documentado**.  
Pronto para apresentação e defesa.