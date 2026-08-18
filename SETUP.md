# Attus Development Setup

## Quick Start - Docker Compose (Recomendado)

```bash
# 1. Clonar repositório
git clone https://github.com/dosmoreno/attus.git
cd attus

# 2. Iniciar todos os serviços
docker-compose up -d

# 3. Aguardar inicialização (≈30 segundos)
docker-compose logs -f

# 4. Acessar aplicação
# Backend: http://localhost:8080
# Frontend: http://localhost:4200
```

## Setup Manual

### Pré-requisitos Instalação Local

```bash
# Verificar Java
java -version
# openjdk version "17.0.x"

# Verificar Node.js
node -v npm -v
# v18.x.x

# Verificar PostgreSQL
psql --version
# psql 14.x
```

### Passo 1: Configurar PostgreSQL

#### macOS
```bash
brew install postgresql@14
brew services start postgresql@14
```

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### Windows
- Download: https://www.postgresql.org/download/windows/
- Installer padrão

### Passo 2: Criar Banco de Dados

```bash
# Conectar como superuser
sudo -u postgres psql

# Executar no psql:
CREATE DATABASE attus_db;
CREATE USER attus_user WITH PASSWORD 'attus_password';
GRANT ALL PRIVILEGES ON DATABASE attus_db TO attus_user;

# Sair
\q
```

### Passo 3: Executar Schema

```bash
cd backend
psql -U attus_user -d attus_db < src/main/resources/schema.sql
```

### Passo 4: Executar Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run

# Logs esperados:
# Started AttusApplication in 2.5 seconds
# Tomcat started on port(s): 8080
```

### Passo 5: Executar Frontend

```bash
cd frontend
npm install
npm start

# Logs esperados:
# ✔ Compiled successfully.
# ⠙ Building...
# Your application is running here: http://localhost:4200
```

## Verificar Instalação

### Backend
```bash
curl http://localhost:8080/api/v1/tasks
# Deve retornar um array JSON vazio: []
```

### Frontend
```bash
# Abrir no navegador:
# http://localhost:4200
# Deve exibir interface de tarefas
```

## Desenvolvimento

### VSCode Extensions Recomendadas

```
- Java Extension Pack
- Spring Boot Extension Pack
- Angular Language Service
- Prettier - Code formatter
- ESLint
- REST Client
```

### Database Viewer

- DBeaver: https://dbeaver.io/
- pgAdmin: https://www.pgadmin.org/

```bash
# ou via Docker:
docker run --name pgadmin \
  -e PGADMIN_DEFAULT_EMAIL=admin@admin.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin \
  -p 5050:80 \
  dpage/pgadmin4
```

### API Testing

```bash
# Usar VS Code REST Client
# Criar arquivo: requests.http

### Get All Tasks
GET http://localhost:8080/api/v1/tasks

### Create Task
POST http://localhost:8080/api/v1/tasks
Content-Type: application/json

{
  "title": "Test Task",
  "description": "Test Description",
  "priority": "HIGH"
}

### Update Task
PUT http://localhost:8080/api/v1/tasks/1
Content-Type: application/json

{
  "status": "COMPLETED"
}

### Delete Task
DELETE http://localhost:8080/api/v1/tasks/1
```

## Troubleshooting

### Erro: "Connection refused" (PostgreSQL)

```bash
# Verificar se PostgreSQL está rodando
psql -U attus_user -d attus_db

# Se não conseguir conectar:
# macOS: brew services start postgresql@14
# Ubuntu: sudo systemctl start postgresql
# Windows: Services > PostgreSQL > Start
```

### Erro: "Cannot resolve symbol 'Task'"

```bash
cd backend
mvn clean compile
# Aguardar indexação do IDE
```

### Erro: "Port 8080 already in use"

```bash
# Encontrar processo
lsof -i :8080
# Matar processo
kill -9 <PID>

# Ou usar porta diferente:
PORT=8081 npm start
```

### Erro: "Cannot GET / on frontend"

```bash
npm cache clean --force
rm -rf node_modules dist
npm install
npm start
```

### Erro: "Module not found"

```bash
cd backend
mvn dependency:resolve
mvn dependency:tree

cd frontend
npm install --legacy-peer-deps
```

## Workflow Git

```bash
# Atualizar main
git checkout main
git pull origin main

# Criar feature branch
git checkout -b feature/amazing-feature

# Fazer mudanças...
git add .
git commit -m "Add amazing feature"

# Enviar
git push origin feature/amazing-feature

# Abrir Pull Request no GitHub
```

## Boas Práticas

### Backend
```bash
# Formatar código
mvn spotless:apply

# Verificar code coverage
mvn test jacoco:report
open target/site/jacoco/index.html

# Análise estática
mvn checkstyle:check
```

### Frontend
```bash
# Formatar código
npm run lint -- --fix

# Build para produção
npm run build
# Output em: dist/

# Análise de bundle
npm run build -- --stats-json
# npm install -g webpack-bundle-analyzer
# webpack-bundle-analyzer dist/stats.json
```

## Variáveis de Ambiente

### Backend (.env ou application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/attus_db
    username: attus_user
    password: attus_password
  jpa:
    hibernate:
      ddl-auto: validate
```

### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

## Scripts Úteis

### Backup do Banco
```bash
pg_dump -U attus_user -d attus_db > backup.sql
```

### Restaurar do Banco
```bash
psql -U attus_user -d attus_db < backup.sql
```

### Limpar Dados
```bash
psql -U attus_user -d attus_db -c "TRUNCATE tasks CASCADE;"
```

### Ver Logs do Container
```bash
docker-compose logs -f backend
docker-compose logs -f postgres
```

## Performance Tips

### Backend
- Usar Spring Boot DevTools: `mvn spring-boot:run -Dspring-boot.run.arguments=--debug`
- Monitorar connections: `SELECT * FROM pg_stat_activity;`
- Check slow queries: `EXPLAIN ANALYZE SELECT ...`

### Frontend
- Usar Chrome DevTools Performance tab
- Check Network tab para requests lentos
- Lighthouse audit: `npm audit`

## Recursos Adicionais

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Angular Docs](https://angular.io/docs)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Docker Docs](https://docs.docker.com/)

---

**Precisa de ajuda?** Abra uma issue ou veja a seção Troubleshooting! 🚀
