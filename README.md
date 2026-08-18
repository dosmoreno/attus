# Attus - Task Management System

Uma aplicação completa de gerenciamento de tarefas (TODO) implementada com **Java Spring Boot** no backend e **Angular** no frontend, com PostgreSQL como banco de dados.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Execução](#execução)
- [Funcionalidades](#funcionalidades)
- [Testes](#testes)
- [Documentação](#documentação)
- [Decisões de Design](#decisões-de-design)
- [Melhorias Futuras](#melhorias-futuras)

## 🎯 Visão Geral

O Attus é um sistema completo de gerenciamento de tarefas que demonstra boas práticas de engenharia de software, incluindo:

✅ **Backend robusto** com Spring Boot 3.x
✅ **Frontend moderno** com Angular 17
✅ **Banco de dados** PostgreSQL com migrations
✅ **Testes** unitários e de integração
✅ **Logging estruturado** para diagnóstico
✅ **API RESTful** bem documentada
✅ **Análise de incidente** com soluções

## 🏗️ Arquitetura

```
attus/
├── backend/                    # Spring Boot Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attus/
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── service/         # Business Logic
│   │   │   │   ├── repository/      # Data Access Layer
│   │   │   │   ├── entity/          # JPA Entities
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   └── exception/       # Custom Exceptions
│   │   │   └── resources/
│   │   │       ├── application.yml  # Configuration
│   │   │       └── schema.sql       # Database Schema
│   │   └── test/                    # Unit & Integration Tests
│   └── pom.xml                      # Maven Configuration
│
├── frontend/                   # Angular Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/              # TypeScript Interfaces
│   │   │   ├── services/            # HTTP Services
│   │   │   ├── components/
│   │   │   │   ├── task-list/       # Task List Component
│   │   │   │   └── task-form/       # Task Form Component
│   │   │   ├── app.component.ts     # Root Component
│   │   │   └── app.component.html
│   │   ├── main.ts                  # Entry Point
│   │   └── index.html
│   └── package.json
│
├── docs/
│   ├── API.md                       # API Documentation
│   ├── INCIDENT_ANALYSIS.md         # Incident Analysis (Part 2)
│   └── ARCHITECTURE.md              # Architecture Details
│
└── README.md
```

## 📦 Pré-requisitos

- **Java:** 17+
- **Node.js:** 18+ com npm
- **PostgreSQL:** 14+
- **Git:** para versionamento
- **Maven:** para build do backend (incluído)

## 🚀 Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/dosmoreno/attus.git
cd attus
```

### 2. Configurar PostgreSQL

#### Opção A: Instalar localmente
```bash
# macOS (com Homebrew)
brew install postgresql@14
brew services start postgresql@14

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# Criar banco de dados
sudo -u postgres createdb attus_db

# Criar usuário
sudo -u postgres psql -c "CREATE USER attus_user WITH PASSWORD 'attus_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE attus_db TO attus_user;"
```

#### Opção B: Usar Docker
```bash
docker run --name attus-postgres \
  -e POSTGRES_DB=attus_db \
  -e POSTGRES_USER=attus_user \
  -e POSTGRES_PASSWORD=attus_password \
  -p 5432:5432 \
  -d postgres:14
```

### 3. Executar Schema do Banco de Dados

```bash
# Conectar ao PostgreSQL
psql -U attus_user -d attus_db -h localhost

# Executar o schema
\i backend/src/main/resources/schema.sql

# Sair
\q
```

### 4. Compilar Backend

```bash
cd backend
mvn clean install
```

### 5. Instalar Dependências do Frontend

```bash
cd frontend
npm install
```

## ▶️ Execução

### Opção rápida: script de inicialização

Na raiz do projeto, execute:

```bash
chmod +x start.sh
./start.sh
```

Esse script:
- verifica se Java, Maven e Node.js estão instalados
- libera as portas 8080 e 4200 caso haja processos antigos
- inicia o backend em background
- espera a API responder em http://localhost:8080
- inicia o frontend em modo de desenvolvimento
- salva os logs em `backend.log` e `frontend.log`
- evita conflitos de porta ao reiniciar a aplicação

### Backend (Spring Boot)

```bash
cd backend

# Executar aplicação
mvn spring-boot:run

# A aplicação estará disponível em:
# http://localhost:8080
```

**Logs esperados:**
```
Started AttusApplication in 2.5 seconds
Tomcat started on port(s): 8080 (http)
```

### Frontend (Angular)

```bash
cd frontend

# Executar servidor de desenvolvimento
npm start

# A aplicação estará disponível em:
# http://localhost:4200
```

**Logs esperados:**
```
✔ Compiled successfully.
⠙ Building...
✔ Open Browser

http://localhost:4200
```

## ✨ Funcionalidades

### Gerenciamento de Tarefas

#### ✅ Criar Tarefa
- Título (obrigatório, máx 255 caracteres)
- Descrição (opcional, máx 1000 caracteres)
- Status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- Prioridade (LOW, MEDIUM, HIGH, CRITICAL)

#### ✅ Visualizar Tarefas
- Listar todas as tarefas
- Ordenar por prioridade e data de criação
- Visualizar detalhes da tarefa

#### ✅ Atualizar Tarefa
- Editar qualquer campo
- Atualizar status e prioridade
- Rastrear data de conclusão

#### ✅ Deletar Tarefa
- Remover tarefas com confirmação

#### ✅ Filtrar Tarefas
- Por status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- Por prioridade (LOW, MEDIUM, HIGH, CRITICAL)
- Busca por texto (título e descrição)

#### ✅ Validações
- Validação no frontend (tempo real)
- Validação no backend (segurança)
- Mensagens de erro claras

## 🧪 Testes

### Backend - Testes Unitários

```bash
cd backend

# Executar todos os testes
mvn test

# Executar com cobertura
mvn test jacoco:report
# Relatório: target/site/jacoco/index.html

# Testes inclusos:
# - TaskServiceTest: Lógica de serviço
# - TaskControllerTest: Endpoints REST
# - TaskRepositoryTest: Camada de dados
```

**Exemplo de saída:**
```
Tests run: 21, Failures: 0, Skipped: 0, Time elapsed: 3.5 seconds
```

### Frontend - Testes Unitários

```bash
cd frontend

# Executar testes
npm test

# Executar com cobertura
npm test -- --code-coverage
```

### Teste Manual - cURL

```bash
# Criar tarefa
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","priority":"HIGH"}'

# Listar tarefas
curl http://localhost:8080/api/v1/tasks

# Filtrar por status
curl "http://localhost:8080/api/v1/tasks/filter/status?status=PENDING"

# Atualizar tarefa
curl -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED"}'

# Deletar tarefa
curl -X DELETE http://localhost:8080/api/v1/tasks/1
```

## 📚 Documentação

### API Documentation
Veja [docs/API.md](docs/API.md) para:
- Endpoints completos
- Formatos de request/response
- Exemplos de uso
- Códigos de erro

### Incident Analysis (Parte 2)
Veja [docs/INCIDENT_ANALYSIS.md](docs/INCIDENT_ANALYSIS.md) para:
- Análise de erro recorrente
- Root causes identificadas
- Correções implementadas
- Medidas de prevenção
- Métricas de melhoria

### Script de Inicialização
Veja [start.sh](start.sh) para:
- subir backend e frontend com um único comando
- verificar dependências do ambiente
- iniciar o backend em background
- registrar logs em `backend.log` e `frontend.log`
- facilitar execução local e validação rápida

## 🎨 Decisões de Design

### Backend

#### 1. Spring Boot 3.x
- **Razão:** Framework maduro e robusto para aplicações enterprise
- **Vantagem:** Ecosistema rico, comunidade ativa, boas práticas

#### 2. JPA/Hibernate
- **Razão:** ORM poderoso para abstração de banco de dados
- **Vantagem:** Migrations automáticas, type safety, lazy loading

#### 3. DTOs (Data Transfer Objects)
- **Razão:** Desacoplar entidades de banco de dados da API
- **Vantagem:** Maior segurança, flexibilidade, versionamento

#### 4. Service Layer
- **Razão:** Centralizar lógica de negócio
- **Vantagem:** Reutilização, testabilidade, separação de responsabilidades

#### 5. Logging com SLF4J
- **Razão:** Logging estruturado e extensível
- **Vantagem:** Fácil integração com ferramentas de observabilidade

### Frontend

#### 1. Angular 17 com Standalone Components
- **Razão:** Framework moderno com performance otimizada
- **Vantagem:** Componentes reutilizáveis, type safety com TypeScript

#### 2. Reactive Forms
- **Razão:** Melhor controle sobre validação e estado
- **Vantagem:** Validação em tempo real, testes mais simples

#### 3. HTTP Client com Interceptors
- **Razão:** Centralizar lógica de comunicação com backend
- **Vantagem:** Tratamento global de erros, autenticação

#### 4. CSS Puro (sem frameworks)
- **Razão:** Demonstrar habilidades de styling
- **Vantagem:** Sem dependências, total controle, menor bundle size

### Banco de Dados

#### 1. PostgreSQL
- **Razão:** RDBMS robusto e confiável
- **Vantagem:** ACID compliance, índices eficientes, suporte a JSON

#### 2. Índices Estratégicos
- **Razão:** Otimizar queries de filtro
- **Vantagem:** Performance, redução de latência

## 📈 Melhorias Futuras

### Curto Prazo
- [ ] Autenticação (JWT)
- [ ] Autorização (Role-based access control)
- [ ] Paginação em listagem de tarefas
- [ ] Soft delete (deletar logicamente)
- [ ] Auditoria (quem criou, quem modificou)

### Médio Prazo
- [ ] WebSocket para real-time updates
- [ ] Notificações por email
- [ ] Compartilhamento de tarefas entre usuários
- [ ] Colaboração em tarefas
- [ ] Histórico de mudanças

### Longo Prazo
- [ ] Mobile app (React Native)
- [ ] Integração com calendário (Google Calendar, Outlook)
- [ ] IA para estimativa de tempo
- [ ] Analytics e relatórios
- [ ] Microserviços (separar tasks, users, notifications)

## 🐛 Troubleshooting

### Erro: Connection refused (PostgreSQL)
```bash
# Verificar se PostgreSQL está rodando
psql -U attus_user -d attus_db -h localhost

# Se Docker:
docker ps | grep attus-postgres
```

### Erro: Cannot resolve symbol 'Task'
```bash
# Rebuildar Maven
cd backend
mvn clean compile
```

### Erro: Cannot GET / (Frontend)
```bash
# Verificar porta
npm start -- --port 4200

# Limpar cache
npm cache clean --force
npm install
```

## 📝 Licença

MIT License - veja [LICENSE](LICENSE) para detalhes

## 👤 Autor

**Dosmoreno** - [GitHub](https://github.com/dosmoreno)

---

**Desenvolvido como teste técnico demonstrando boas práticas de engenharia de software** 🚀