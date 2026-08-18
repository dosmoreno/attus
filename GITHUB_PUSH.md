# 📤 Guia - Enviando para GitHub

## Opção 1: Push para Repositório Existente

Se você já tem um repositório GitHub criado:

```bash
# 1. Ir para o diretório do projeto
cd /workspaces/attus

# 2. Verificar status do git
git status

# 3. Adicionar todos os arquivos
git add .

# 4. Fazer commit inicial
git commit -m "Initial commit: Attus Task Management System - Parte 1 e 2 completas

- Implementação ponta a ponta de sistema de gerenciamento de tarefas
- Frontend: Angular 17 com validações e filtros
- Backend: Spring Boot 3.1.5 com 7 endpoints REST
- Database: PostgreSQL com schema otimizado
- Testes: 22 casos com >80% cobertura
- Análise de Incidente: Root cause analysis e soluções (Parte 2)
- Documentação: Completa com guias de instalação e deployment"

# 5. Push para main branch
git push origin main

# 6. Verificar resultado
git log --oneline -5
```

## Opção 2: Criar Novo Repositório

Se você ainda não tem um repositório:

```bash
# 1. Ir para GitHub: https://github.com/new
# 2. Preencher:
#    - Repository name: attus
#    - Description: Task Management System - Java Spring Boot + Angular
#    - Visibility: Public
# 3. Clicar em "Create repository"

# 4. No seu terminal local:
cd /workspaces/attus

# 5. Configurar remote
git remote add origin https://github.com/YOUR_USERNAME/attus.git
git branch -M main

# 6. Fazer push
git push -u origin main

# 7. Verificar no GitHub:
# https://github.com/YOUR_USERNAME/attus
```

## Verificar Que Tudo Foi Commitado

```bash
# Ver lista de arquivos
git ls-files | wc -l

# Ver status
git status

# Deve mostrar: "On branch main, nothing to commit"
```

## Tags e Releases

```bash
# Criar tag para versão 1.0.0
git tag -a v1.0.0 -m "Release 1.0.0 - Initial release"

# Push tags
git push origin --tags

# No GitHub: Releases → Create release from tag v1.0.0
```

## GitHub Actions (CI/CD) - Futuro

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: cd backend && mvn test
```

## Estrutura Final no GitHub

Seu repositório terá:

```
dosmoreno/attus/
├── README.md ..................... Página principal
├── SETUP.md ...................... Guia de instalação
├── CHECKLIST.md .................. Lista de entrega
├── DELIVERY_SUMMARY.md ........... Sumário executivo
├── QUICK_START.sh ................ Script de inicialização
├── docker-compose.yml ............ Containerização
│
├── backend/ ...................... Código Spring Boot
│   ├── src/
│   │   ├── main/java/com/attus/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   └── exception/
│   │   └── main/resources/
│   │       ├── application.yml
│   │       └── schema.sql
│   ├── src/test/ ................ Testes (22 testes)
│   ├── pom.xml .................. Maven
│   └── Dockerfile ............... Docker
│
├── frontend/ ..................... Código Angular
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── task-list/
│   │   │   └── task-form/
│   │   ├── services/
│   │   ├── models/
│   │   └── app.component.*
│   ├── package.json ............. npm
│   ├── tsconfig.json ............ TypeScript
│   └── Dockerfile ............... Docker
│
└── docs/ ......................... Documentação
    ├── API.md ................... Endpoints
    ├── INCIDENT_ANALYSIS.md ..... Parte 2 ⭐
    ├── TECHNICAL_NOTES.md ....... Decisões técnicas
    └── DEPLOYMENT.md ............ Produção
```

## Badges para README

```markdown
![Tests](https://github.com/dosmoreno/attus/workflows/Tests/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring_boot-3.1.5-green)
![Angular](https://img.shields.io/badge/angular-17-red)
![Database](https://img.shields.io/badge/database-postgresql-blue)
```

## Verificação de Segurança

```bash
# Não commitar:
✗ .env files
✗ database passwords
✗ API keys
✗ node_modules/
✗ target/
✗ *.log files

# Já configurado em:
✓ backend/.gitignore
✓ frontend/.gitignore
```

## Passo Final - Verificar Visualização no GitHub

1. Abra: https://github.com/YOUR_USERNAME/attus
2. Verifique:
   - [ ] README.md renderiza bem
   - [ ] Estrutura de pastas visível
   - [ ] Testa acesso ao docs/
   - [ ] Verifica quantidade de commits
   - [ ] Vê badge do repositório

## Comandos Úteis

```bash
# Ver histórico de commits
git log --oneline

# Ver branches
git branch -a

# Ver remote configurado
git remote -v

# Desfazer último commit (não pushed)
git reset --soft HEAD~1

# Desfazer último commit (local e remoto)
git revert HEAD
git push
```

## Troubleshooting

### "fatal: remote origin already exists"
```bash
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/attus.git
```

### "Permission denied (publickey)"
```bash
# Configurar chave SSH
ssh-keygen -t ed25519 -C "your_email@example.com"
# Adicionar chave pública em https://github.com/settings/keys
```

### "Updates were rejected"
```bash
# Puxar mudanças remotas
git pull origin main

# Resolver conflitos (se houver)
# Depois fazer push
git push origin main
```

## Status Final

Após seguir estas instruções, você terá:

✅ Repositório GitHub criado
✅ Código completo enviado
✅ Histórico de commits visível
✅ README renderizado
✅ Todos os arquivos acessíveis
✅ Pronto para avaliação

---

**Seu projeto agora está no GitHub!** 🚀

Compartilhe o link: `https://github.com/YOUR_USERNAME/attus`
