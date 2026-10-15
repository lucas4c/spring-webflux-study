# WebFlux Enterprise API & Distributed Architecture Portfolio

*Read this in other languages: [🇺🇸 English](#english-version) | [🇧🇷 Português](#versão-em-português)*

---

<a id="english-version"></a>
# 🇺🇸 English Version

## 🚀 About the Project
This project is an ecosystem of reactive, secure, and high-performance microservices. Built with **Spring Boot (3.3+) / Spring WebFlux**, **PostgreSQL**, **Redis**, **Apache Kafka**, and orchestrated via **Kubernetes**, it was designed to explore architectural practices and the patterns demanded in real world software engineering solutions.

The application adopts a non-blocking programming model (Reactor/Netty), ensuring high throughput and low resource consumption under extreme concurrency, combined with an event-driven architecture and cloud-native infrastructure.

---

## 🛠️ Tech Stack & Infrastructure
* **Language & Runtime:** Java 21 LTS
* **Core Framework:** Spring Boot, Spring WebFlux, Spring Data R2DBC
* **Relational Database:** PostgreSQL 16 (Asynchronous via R2DBC)
* **Cache & Rate Limiting:** Redis 7 (Reactive)
* **Messaging & EDA:** Apache Kafka 3.7 (KRaft Mode - No Zookeeper)
* **Security:** Spring Security WebFlux (Stateless JWT Authentication, BCrypt)
* **API Gateway:** Spring Cloud Gateway
* **Orchestration & DevOps:** Kubernetes (Minikube), Docker, Docker Compose
* **Quality & Testing:** JUnit 5, Mockito, StepVerifier, WebTestClient, Testcontainers

---

## 🏛️ Architecture & Engineering Decisions

### 1. End-to-End Reactive Paradigm (Non-Blocking I/O)
Traditional Servlet-based architectures (Thread-per-request) suffer from bottlenecks during heavy I/O operations. WebFlux uses an event-driven model on top of **Netty**, allowing a few threads to handle thousands of simultaneous connections highly efficiently, with R2DBC ensuring non-blocking I/O at the persistence layer as well.

### 2. Stateless Security (JWT) & DDoS Protection
* **Reactive Auth:** Implementation of an asynchronous `WebFilter` to validate JWT tokens directly in the `ReactiveSecurityContextHolder` without blocking the pipeline.
* **Rate Limiting:** A reactive filter coupled with **Redis**. It uses atomic commands (`INCR` with time expiration) based on the client's IP, returning `429 Too Many Requests` to mitigate brute-force attacks.

### 3. Event-Driven Architecture (EDA) with Kafka
Deep domain decoupling using **Apache Kafka in KRaft mode**.
* **Producer (`user-service`):** Upon persisting a user, it emits a structured asynchronous event (`user.created.topic`) to the Kafka cluster in a *fire-and-forget* manner.
* **Consumer (`notification-service`):** An independent microservice with Event Replay capabilities (`auto.offset.reset = earliest`). It guarantees fault tolerance and zero data loss by consuming events resiliently, allowing state recovery in case of downtime.

### 4. Orchestration with Kubernetes & API Gateway
The infrastructure was designed to reflect isolated microservice patterns in production environments.
* **Spring Cloud Gateway:** Acts as the single entry point (Edge Service/BFF) for the cluster. It routes requests using Kubernetes internal DNS (`http://user-service:8081`), completely hiding the internal topology from external clients.
* **Kubernetes (K8s Manifests):**
    * **Network Isolation:** Critical services (PostgreSQL, Redis, Kafka) are exposed only internally via `ClusterIP`.
    * **Secret Management:** Usage of K8s `Secrets` for secure credential injection into database Pods, preventing exposure in the source code.
    * **Scalability (ReplicaSets):** Native configuration for multiple `user-service` replicas. Being a 100% *stateless* application, K8s load balancing perfectly distributes requests without the need for session affinity.

---

## 📊 Repository Structure
* `/gateway-service` — Single entry point and traffic routing.
* `/webflux-api` — Core service (`user-service`) for Auth, User CRUD, and Kafka event production.
* `/notification-service` — Satellite microservice acting as a Kafka consumer to dispatch notifications.
* `/k8s` — Declarative Kubernetes manifests.
  * `01-secrets.yaml` — Password and credential management.
  * `02-postgres.yaml` & `03-kafka.yaml` — Datastore/broker deployments using `ClusterIP`.
  * `04-user-service.yaml` & `05-gateway.yaml` — Scalable microservices and external exposure via `NodePort`.

<br><br><br>

---

<a id="versão-em-português"></a>
# 🇧🇷 Versão em Português

## 🚀 Sobre o Projeto
Este projeto é um ecossistema de microsserviços reativos, seguros e de alta performance. Desenvolvido com **Spring Boot (3.3+) / Spring WebFlux**, **PostgreSQL**, **Redis**, **Apache Kafka** e orquestrado via **Kubernetes**, foi projetado para explorar arquiteturas e padrões vistos na engenharia de software no mundo real.

A aplicação adota um modelo de programação não-bloqueante (Reactor/Netty), garantindo alta vazão (*throughput*) e baixo consumo de recursos sob concorrência extrema, aliado a uma arquitetura orientada a eventos e infraestrutura nativa de nuvem.

---

## 🛠️ Stack Tecnológica & Infraestrutura
* **Linguagem & Runtime:** Java 21 LTS
* **Framework Principal:** Spring Boot, Spring WebFlux, Spring Data R2DBC
* **Banco de Dados Relacional:** PostgreSQL 16 (Assíncrono via R2DBC)
* **Cache & Rate Limiting:** Redis 7 (Reativo)
* **Mensageria & EDA:** Apache Kafka 3.7 (Modo KRaft - Sem Zookeeper)
* **Segurança:** Spring Security WebFlux (Stateless JWT Authentication, BCrypt)
* **API Gateway:** Spring Cloud Gateway
* **Orquestração & DevOps:** Kubernetes (Minikube), Docker, Docker Compose
* **Qualidade & Testes:** JUnit 5, Mockito, StepVerifier, WebTestClient, Testcontainers

---

## 🏛️ Arquitetura & Decisões de Engenharia

### 1. Paradigma Reativo End-to-End (Non-Blocking I/O)
Arquiteturas tradicionais baseadas em Servlet (Thread-per-request) sofrem gargalos em operações pesadas de I/O. O WebFlux utiliza um modelo orientado a eventos sobre o **Netty**, permitindo que poucas threads gerenciem milhares de conexões simultâneas de forma altamente eficiente, com R2DBC garantindo I/O não bloqueante também na camada de persistência.

### 2. Segurança Estateless (JWT) & Proteção contra DDoS
* **Auth Reativa:** Implementação de `WebFilter` assíncrono para validação de tokens JWT diretamente no `ReactiveSecurityContextHolder` sem bloquear o pipeline.
* **Rate Limiting:** Filtro reativo acoplado ao **Redis**. Utiliza comandos atômicos (`INCR` com expiração por tempo) baseados no IP do cliente, retornando `429 Too Many Requests` para mitigar ataques de força bruta.

### 3. Arquitetura Orientada a Eventos (EDA) com Kafka
Desacoplamento profundo de domínios utilizando **Apache Kafka no modo KRaft**.
* **Produtor (`user-service`):** Ao persistir um usuário, emite um evento assíncrono estruturado (`user.created.topic`) no cluster Kafka de forma *fire-and-forget*.
* **Consumidor (`notification-service`):** Microsserviço independente com Event Replay (`auto.offset.reset = earliest`). Garante tolerância a falhas e zero perda de dados ao consumir eventos de forma resiliente, permitindo recuperar o estado em caso de downtime.

### 4. Orquestração com Kubernetes & API Gateway
A infraestrutura foi desenhada para refletir padrões de microsserviços isolados em ambientes de produção.
* **Spring Cloud Gateway:** Atua como porta de entrada única (Edge Service/BFF) para o cluster. Roteia requisições utilizando o DNS interno do Kubernetes (`http://user-service:8081`), ocultando completamente a topologia interna dos clientes externos.
* **Kubernetes (Manifestos K8s):**
    * **Isolamento de Rede:** Serviços críticos (PostgreSQL, Redis, Kafka) expostos apenas internamente via `ClusterIP`.
    * **Gestão de Segredos:** Uso de `Secrets` K8s para injeção segura de credenciais nos Pods de banco de dados, evitando exposição no código-fonte.
    * **Escalabilidade (ReplicaSets):** Configuração nativa para múltiplas réplicas do `user-service`. Por ser uma aplicação 100% *stateless*, o balanceamento de carga K8s distribui requisições perfeitamente sem necessidade de afinidade de sessão.

---

## 📊 Estrutura do Repositório
* `/gateway-service` — Ponto único de entrada e roteamento de tráfego.
* `/webflux-api` — Serviço core (`user-service`) para Auth, CRUD de Usuários e Produção de eventos Kafka.
* `/notification-service` — Microsserviço satélite consumidor do Kafka para disparo de notificações.
* `/k8s` — Manifestos Declarativos Kubernetes.
  * `01-secrets.yaml` — Gerenciamento de senhas e credenciais.
  * `02-postgres.yaml` & `03-kafka.yaml` — Deployments de datastores/brokers com `ClusterIP`.
  * `04-user-service.yaml` & `05-gateway.yaml` — Microsserviços escaláveis e exposição externa via `NodePort`.
