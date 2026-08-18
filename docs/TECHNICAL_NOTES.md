# Notas Técnicas do Projeto Attus

## Resumo

Este documento fornece uma visão geral das decisões técnicas, trade-offs e possíveis melhorias futuras para o projeto Attus.

## 0. Script de Inicialização do Projeto

### Objetivo
O projeto inclui um script chamado [start.sh](../start.sh) para facilitar a subida local do backend e do frontend em uma única execução.

### Como usar

```bash
cd /caminho/para/o/projeto
chmod +x start.sh
./start.sh
```

### O que o script faz
- valida se Java, Maven, Node.js e npm estão instalados
- libera automaticamente as portas 8080 e 4200 para evitar conflitos
- inicia o backend em background
- aguarda a API responder em http://localhost:8080
- inicia o frontend em modo de desenvolvimento
- grava os logs em `backend.log` e `frontend.log` na raiz do projeto
- permite reinicialização limpa sem falhar por processos antigos ocupando as portas

### Logs
O script salva a saída do backend em `backend.log` e a do frontend em `frontend.log`, permitindo acompanhar a inicialização sem perder a visão do terminal principal.

### Quando usar
- para subir a aplicação localmente pela primeira vez
- para reexecutar o ambiente rápido após alterações
- para validar integração backend + frontend sem repetir comandos manualmente

## 1. Decisões Tecnológicas

### 1.1 Stack Escolhido

**Backend:**
- Java 17 com Spring Boot 3.1.5
- JPA/Hibernate para ORM
- PostgreSQL 14+ para persistência
- Maven para build e dependency management

**Frontend:**
- Angular 17 com Standalone Components
- TypeScript 5.2+ para type safety
- RxJS para programação reativa
- CSS puro sem dependências de UI frameworks

**Banco de Dados:**
- PostgreSQL com suporte a JSONB
- Índices estratégicos para queries frequentes

### 1.2 Justificativas

#### Spring Boot
- ✅ Maduro e confiável
- ✅ Comunidade ativa
- ✅ Excelente documentação
- ✅ Integração fácil com ferramentas
- ❌ Overhead em aplicações very lightweight

#### Angular 17
- ✅ Type safety com TypeScript
- ✅ Componentes standalone reduzem boilerplate
- ✅ Performance otimizada
- ✅ Melhor suporte a acessibilidade
- ❌ Curva de aprendizado maior

#### PostgreSQL
- ✅ ACID compliance
- ✅ Escalabilidade
- ✅ Performance com índices
- ✅ Excelente suporte comunitário
- ❌ Requer mais recurso que SQLite

## 2. Padrões de Design Implementados

### 2.1 Backend

#### DTO (Data Transfer Object)
```
Entity (BD) → DTO → JSON (API)
```
**Vantagem:** Desacoplamento, segurança, flexibilidade de versionamento

#### Service Layer Pattern
```
Controller → Service → Repository → Entity
```
**Vantagem:** Separação de responsabilidades, reutilização, testabilidade

#### Exception Handling
- Custom exceptions (TaskNotFoundException)
- Global exception handler (@ExceptionHandler)
- Structured error responses

### 2.2 Frontend

#### Component-Based Architecture
```
AppComponent
  └─ TaskListComponent
      ├─ TaskFormComponent (modal)
      └─ TaskDetailComponent (futura)
```

#### Reactive Programming
- RxJS Observables
- Async pipe para auto-unsubscribe
- Error handling via catchError

#### Separation of Concerns
- Components: UI logic
- Services: Business logic
- Models: Type definitions

## 3. Decisões de Arquitetura

### 3.1 Monolithic vs Microservices

**Decisão:** Monolithic

**Razões:**
- Projeto começando, complexidade gerenciável
- Fácil deployment
- Menor latência inter-serviços

**Possível migração futura:** Separar em serviços de Tasks, Users, Notifications

### 3.2 Database Strategy

**Decisão:** Relational (PostgreSQL)

**Razões:**
- ACID compliance necessário
- Queries complexas e join operations
- Dados altamente estruturados

**Trade-off:** Performance em escala muito alta (considerar NoSQL para logs/eventos)

### 3.3 API Design

**Decisão:** REST com JSON

**Razões:**
- Standard da indústria
- Stateless
- Fácil integração com frontend

**Alternativa considerada:** GraphQL
- ✅ Maior flexibilidade
- ❌ Mais complexo inicialmente
- ❌ Cache mais complicado

## 4. Trade-offs

### 4.1 Testabilidade vs Simplicidade

**Escolha:** Maximizar testabilidade
- Service layer com lógica reutilizável
- DTOs que facilitam mock
- Testes unitários com Mockito
- Testes de integração com Testcontainers

**Trade-off:** Mais código, mais arquivos

### 4.2 Performance vs Legibilidade

**Escolha:** Legibilidade com otimizações estratégicas
- Código claro e bem documentado
- Índices no banco de dados onde necessário
- Connection pooling configurado
- N+1 query problem evitado

**Trade-off:** Não implementar todas as micro-otimizações

### 4.3 Framework CSS vs CSS Puro

**Escolha:** CSS Puro

**Razões:**
- Demonstra skills de styling
- Controle total
- Menor bundle size
- Sem dependências desnecessárias

**Trade-off:** Mais tempo em styling, sem componentes prontos

### 4.4 Autenticação

**Escolha:** Sem autenticação (escopo do teste)

**Razões:**
- Simplificar para foco em funcionalidades core
- Não é requisito do teste técnico

**Implementação futura:** JWT com Spring Security

## 5. Padrões Seguidos

### 5.1 SOLID Principles

- **S**ingle Responsibility: TaskService, TaskController separados
- **O**pen/Closed: Fácil adicionar novos endpoints
- **L**iskov Substitution: DTOs intercambiáveis
- **I**nterface Segregation: Endpoints específicos
- **D**ependency Inversion: Injeção de dependência

### 5.2 DRY (Don't Repeat Yourself)

- Service layer centraliza lógica
- DTOs reutilizáveis
- Base classes para testes

### 5.3 Clean Code

- Nomes descritivos
- Funções/métodos pequenos e focados
- Comentários apenas onde necessário
- Logging estruturado

## 6. Considerações de Segurança

### Implementadas
- ✅ Input validation (backend)
- ✅ SQL injection prevention (prepared statements via JPA)
- ✅ CORS configurado
- ✅ Error messages não expõem detalhes internos
- ✅ Constraints no banco de dados

### Não Implementadas (Escopo)
- ❌ Autenticação (JWT)
- ❌ Rate limiting
- ❌ HTTPS/SSL
- ❌ Encryption de dados sensíveis
- ❌ Audit logging completo

## 7. Observabilidade

### Implementado
- ✅ Structured logging com SLF4J
- ✅ Log levels apropriados
- ✅ Identificação de transações
- ✅ Stack traces em erros

### Futuro
- ❌ Distributed tracing (Jaeger/Zipkin)
- ❌ Metrics (Prometheus)
- ❌ Health checks detalhados
- ❌ APM (Application Performance Monitoring)

## 8. Performance

### Otimizações Implementadas
- ✅ Índices no banco de dados
- ✅ Connection pooling (HikariCP)
- ✅ Transações gerenciadas
- ✅ Lazy loading em queries
- ✅ Read-only transactions onde apropriado

### Possíveis Melhorias
- [ ] Redis para cache
- [ ] Pagination nas listagens
- [ ] Lazy loading no frontend
- [ ] Compression de responses HTTP
- [ ] CDN para assets estáticos

## 9. Escalabilidade

### Atual
- Single instance backend
- Single instance frontend
- Single PostgreSQL instance

### Para Crescimento
- Load balancer (Nginx/HAProxy)
- Multiple backend instances
- Database replication
- Message queue (RabbitMQ/Kafka)
- Microservices

## 10. Deployment

### Atual
- Manual ou via Docker
- Environment variables para config

### Futuro
- Kubernetes
- CI/CD pipeline (GitHub Actions)
- Blue-green deployment
- Canary releases
- Infrastructure as Code (Terraform)

## 11. Lições Aprendidas

### O que Funcionou Bem
- Separação clara de responsabilidades
- DTOs facilitam testing
- Standalone components do Angular reduzem complexidade
- Testcontainers para testes realistas

### O que Poderia ser Melhor
- Adicionar autenticação desde o início
- Pagination desde o início
- More comprehensive error handling
- API versioning estratégia

## 12. Melhorias Futuras Prioritárias

### P0 (Critical)
1. Autenticação e autorização
2. Rate limiting
3. Health checks
4. Monitoring/alerting

### P1 (High)
1. Pagination
2. Soft delete
3. Audit logging completo
4. API versioning

### P2 (Medium)
1. WebSocket para real-time
2. File upload/attachments
3. Email notifications
4. Full-text search

### P3 (Low)
1. Mobile app
2. Dark mode
3. i18n (internationalization)
4. Advanced analytics

## 13. Referências e Recursos

- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Angular Style Guide](https://angular.io/guide/styleguide)
- [PostgreSQL Tuning](https://wiki.postgresql.org/wiki/Performance_Optimization)
- [REST API Design](https://restfulapi.net/)
- [The Twelve-Factor App](https://12factor.net/)

---

**Última atualização:** Janeiro 2024
**Versão:** 1.0.0
