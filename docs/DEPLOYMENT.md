# Deployment Guide - Attus

## Índice

- [Production Checklist](#production-checklist)
- [Docker Deployment](#docker-deployment)
- [Cloud Platforms](#cloud-platforms)
- [Database Migration](#database-migration)
- [Monitoring](#monitoring)

## Production Checklist

### Backend
- [ ] Desabilitar debug mode (`spring.devtools.restart.enabled: false`)
- [ ] Configurar logging level para WARN/ERROR
- [ ] Usar database connection pooling (HikariCP)
- [ ] Implementar autenticação JWT
- [ ] Adicionar rate limiting
- [ ] Configurar CORS adequadamente
- [ ] Use HTTPS/SSL
- [ ] Secrets management (não usar hardcoded)
- [ ] Health checks configurados
- [ ] Monitoring e alerting

### Frontend
- [ ] Build otimizado (`npm run build`)
- [ ] Remover console logs
- [ ] Configurar API endpoint correto
- [ ] Cache busting com versioning
- [ ] Compressão Gzip habilitada
- [ ] Minificação CSS/JS
- [ ] Service Worker (PWA)
- [ ] Analytics integrado

## Docker Deployment

### Build Images

```bash
# Backend
cd backend
docker build -t attus-backend:latest .

# Frontend
cd frontend
docker build -t attus-frontend:latest .

# PostgreSQL (usar imagem oficial)
docker pull postgres:14
```

### Docker Registry

```bash
# Login
docker login

# Tag
docker tag attus-backend:latest username/attus-backend:latest
docker tag attus-frontend:latest username/attus-frontend:latest

# Push
docker push username/attus-backend:latest
docker push username/attus-frontend:latest
```

### Docker Compose Production

```bash
# Usar docker-compose.yml
docker-compose -f docker-compose.yml up -d

# Com volumes persistentes
docker run -d \
  --name attus-postgres \
  -e POSTGRES_DB=attus_db \
  -e POSTGRES_USER=attus_user \
  -e POSTGRES_PASSWORD=<SECURE_PASSWORD> \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:14

# Verificar status
docker-compose ps
docker-compose logs -f
```

## Cloud Platforms

### AWS

#### ECS (Elastic Container Service)
```bash
# Criar cluster
aws ecs create-cluster --cluster-name attus-cluster

# Push images para ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin [ACCOUNT].dkr.ecr.us-east-1.amazonaws.com

docker tag attus-backend:latest [ACCOUNT].dkr.ecr.us-east-1.amazonaws.com/attus-backend:latest
docker push [ACCOUNT].dkr.ecr.us-east-1.amazonaws.com/attus-backend:latest
```

#### RDS para Database
```bash
# Criar RDS PostgreSQL instance
aws rds create-db-instance \
  --db-instance-identifier attus-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --allocated-storage 20
```

### Google Cloud

#### Cloud Run
```bash
gcloud run deploy attus-backend \
  --image gcr.io/PROJECT_ID/attus-backend:latest \
  --platform managed \
  --region us-central1 \
  --set-env-vars SPRING_DATASOURCE_URL=<DATABASE_URL>
```

#### Cloud SQL
```bash
gcloud sql instances create attus-db \
  --database-version POSTGRES_14 \
  --tier db-f1-micro \
  --region us-central1
```

### Heroku

```bash
# Login
heroku login

# Create app
heroku create attus-backend
heroku create attus-frontend

# Add PostgreSQL add-on
heroku addons:create heroku-postgresql:hobby-dev -a attus-backend

# Deploy
git push heroku main

# View logs
heroku logs --tail
```

### Azure

```bash
# Login
az login

# Create App Service
az appservice plan create \
  --name AttusAppPlan \
  --resource-group myResourceGroup

# Deploy via GitHub Actions
# .github/workflows/azure-deploy.yml
```

## Database Migration

### Backup Produção

```bash
# PostgreSQL backup
pg_dump -U attus_user -d attus_db -h prod.db.server.com > backup_$(date +%Y%m%d).sql

# Compactar
gzip backup_*.sql

# Enviar para storage seguro
aws s3 cp backup_*.sql.gz s3://backups/attus/
```

### Restaurar

```bash
# Descompactar
gunzip backup_*.sql.gz

# Restaurar
psql -U attus_user -d attus_db < backup_*.sql
```

### Schema Migrations

```bash
# Com Flyway
mvn flyway:info
mvn flyway:migrate

# Com Liquibase
mvn liquibase:update
```

## Monitoring

### Application Monitoring

```bash
# Spring Boot Actuator
curl http://localhost:8080/actuator/health

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Setup Prometheus

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'attus-backend'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

### Grafana Dashboard

```bash
# Adicionar data source Prometheus
# Importar dashboard para Spring Boot
# ID: 12900
```

### Logging

```bash
# ELK Stack (Elasticsearch, Logstash, Kibana)
# Enviar logs via Logback

# Sentry para error tracking
# Adicionar Sentry SDK

# Datadog
# Integration com Spring Boot
```

### Alerting

```bash
# PagerDuty integration
# Email alerts
# Slack notifications
```

## CI/CD Pipeline

### GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
      - run: mvn test
      
  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - run: docker build -t attus-backend:${{ github.sha }} .
      - run: docker push ${{ secrets.REGISTRY }}/attus-backend:${{ github.sha }}
      
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - run: kubectl set image deployment/attus-backend backend=${{ secrets.REGISTRY }}/attus-backend:${{ github.sha }}
```

### GitLab CI

```yaml
# .gitlab-ci.yml
stages:
  - test
  - build
  - deploy

test:
  stage: test
  script:
    - mvn test

build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

deploy:
  stage: deploy
  script:
    - kubectl set image deployment/attus backend=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
```

## Performance Tuning

### Database Tuning

```sql
-- Connection pooling
-- max_connections = 200
-- shared_buffers = 256MB

-- Index analysis
ANALYZE;
REINDEX;

-- Query optimization
EXPLAIN ANALYZE SELECT * FROM tasks WHERE status = 'PENDING';
```

### Backend Tuning

```properties
# JVM Options
-Xmx2048m -Xms1024m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200

# Spring
server.tomcat.threads.max=200
spring.datasource.hikari.maximum-pool-size=20
```

### Frontend Optimization

```bash
# Code splitting
ng build --prod --optimization --build-optimizer

# Bundle analysis
npm install -g webpack-bundle-analyzer
webpack-bundle-analyzer dist/stats.json

# Lazy loading
# Lazy load modules não críticos
```

## Security Hardening

```bash
# HTTPS/SSL
# Use Let's Encrypt

# Firewall
# Whitelist IPs
# Block port scanning

# Secrets Management
# Use HashiCorp Vault
# AWS Secrets Manager
# Google Secret Manager

# Security headers
# Content-Security-Policy
# X-Frame-Options: DENY
# X-Content-Type-Options: nosniff

# Rate Limiting
# Implement throttling
# DDoS protection (CloudFlare)

# Database
# Enable SSL
# Restrict connections
# Regular backups
```

## Disaster Recovery

### RTO/RPO Goals

- **RTO (Recovery Time Objective):** 30 minutos
- **RPO (Recovery Point Objective):** 1 hora

### Backup Strategy

```bash
# Daily backups
0 2 * * * pg_dump attus_db | gzip > /backups/db_$(date +%Y%m%d).sql.gz

# Weekly full backup
0 3 * * 0 pg_basebackup -D /backups/base_$(date +%Y%m%d) -Ft

# Cross-region replication
pg_basebackup -h primary.db -D /replica/data
```

### Failover

```bash
# Promote replica
pg_ctl promote -D /var/lib/postgresql/data

# Update backend connection string
SPRING_DATASOURCE_URL=jdbc:postgresql://replica.db:5432/attus_db

# Verify replication
psql -c "SELECT * FROM pg_stat_replication;"
```

---

**Última atualização:** Janeiro 2024
**Versão:** 1.0.0

Para questões específicas de deployment, abra uma issue! 🚀
