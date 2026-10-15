# WebFlux Enterprise API & Distributed Architecture Portfolio

## 🚀 Sobre o Projeto
Este projeto consiste em uma **API REST reativa, segura e de alta performance** desenvolvida em **Spring Boot (3.3+) / Spring WebFlux** com **PostgreSQL** e **Redis**, projetada para demonstrar maturidade arquitetural e padrões exigidos em vagas de engenharia de software sênior. 

A aplicação adota um modelo de programação não-bloqueante (baseado em Reactor/Netty), garantindo alta vazão (*throughput*) e baixo consumo de recursos sob concorrência extrema.

---

## 🛠️ Stack Tecnológica & Infraestrutura
* **Linguagem & Runtime:** Java 21 LTS
* **Framework Principal:** Spring Boot, Spring WebFlux, Spring Data R2DBC
* **Banco de Dados Relacional:** PostgreSQL 16 (Assíncrono via R2DBC)
* **Cache & Mensageria/Rate Limiting:** Redis 7 (Reativo)
* **Segurança:** Spring Security WebFlux (Stateless JWT Authentication, BCrypt)
* **Mensageria & EDA (Fase 4):** RabbitMQ (Event-Driven Architecture)
* **Qualidade & Testes:** JUnit 5, Mockito, StepVerifier (Reactor Test), WebTestClient e Testcontainers (PostgreSQL efêmero)
* **Containerização:** Docker & Docker Compose

---

## 🏛️ Arquitetura & Decisões de Engenharia

### 1. Paradigma Reativo End-to-End (Non-Blocking I/O)
* **Por que?** Arquiteturas tradicionais baseadas em Servlet (Thread-per-request) sofrem gargalos de escalabilidade em I/O bound pesado. O WebFlux utiliza um modelo orientado a eventos sobre o **Netty**, permitindo que poucas threads gerenciem milhares de conexões simultâneas de forma eficiente.

### 2. Segurança Estateless (JWT) & Filtros Reativos Customizados
* O ecossistema de segurança foi adaptado para o fluxo reativo através da implementação de um `WebFilter` assíncrono (`JwtAuthenticationFilter`), injetando o contexto de autenticação diretamente no `ReactiveSecurityContextHolder` sem bloquear o pipeline de execução.

### 3. Resiliência e Proteção contra DDoS (Rate Limiting)
* Implementação de um filtro de controle de tráfego (`RateLimitFilter`) acoplado ao **Redis reativo**. Utiliza comandos atômicos (`INCR` com expiração por janela de tempo) baseados no IP do cliente, retornando `429 Too Many Requests` de forma não-bloqueante ao atingir o limite estipulado.

### 4. Arquitetura Orientada a Eventos (EDA) & Microsserviços
* Desacoplamento de domínios através de mensageria assíncrona. No momento em que um usuário é persistido com sucesso no `user-service`, um evento estruturado é publicado em uma exchange/fila do **RabbitMQ**, permitindo que microsserviços satélites (como o `notification-service`) consumam o evento de forma independente e assíncrona para o disparo de notificações.

---

## 📊 Estrutura do Repositório
* `src/main/java/com/drago/webflux_api/controller` — Endpoints REST reativos documentados.
* `src/main/java/com/drago/webflux_api/service` — Regras de negócio reativas (`Mono`/`Flux`).
* `src/main/java/com/drago/webflux_api/repository` — Repositórios reativos baseados em R2DBC.
* `src/main/java/com/drago/webflux_api/security` — Configuração de segurança JWT e Filtros de Rate Limiting.
* `src/test/java/...` — Testes unitários com StepVerifier e testes de integração.