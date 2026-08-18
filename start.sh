#!/bin/bash

set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
BACKEND_LOG="$ROOT_DIR/backend.log"
FRONTEND_LOG="$ROOT_DIR/frontend.log"

echo "==> Verificando dependências..."
command -v java >/dev/null 2>&1 || { echo "Java não encontrado. Instale o Java 17+ antes de continuar."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "Maven não encontrado. Instale o Maven antes de continuar."; exit 1; }
command -v node >/dev/null 2>&1 || { echo "Node.js não encontrado. Instale o Node.js 18+ antes de continuar."; exit 1; }
command -v npm >/dev/null 2>&1 || { echo "npm não encontrado. Instale o npm antes de continuar."; exit 1; }

if [ -d "$BACKEND_DIR" ] && [ ! -d "$BACKEND_DIR/target" ]; then
  echo "==> Primeira execução: compilando backend..."
  cd "$BACKEND_DIR"
  mvn clean compile >/dev/null
fi

if [ -d "$FRONTEND_DIR" ] && [ ! -d "$FRONTEND_DIR/node_modules" ]; then
  echo "==> Instalando dependências do frontend..."
  cd "$FRONTEND_DIR"
  npm install
fi

if lsof -i :8080 >/dev/null 2>&1; then
  echo "==> Porta 8080 já está em uso. O backend não será iniciado novamente."
else
  echo "==> Iniciando backend em background..."
  cd "$BACKEND_DIR"
  nohup mvn spring-boot:run > "$BACKEND_LOG" 2>&1 &
  BACKEND_PID=$!
  echo "Backend iniciado com PID: $BACKEND_PID"
  echo "Log do backend: $BACKEND_LOG"
fi

for i in $(seq 1 20); do
  if curl -sf http://localhost:8080/api/v1/tasks >/dev/null 2>&1; then
    echo "==> Backend respondeu em http://localhost:8080"
    break
  fi
  sleep 2
done

if curl -sf http://localhost:8080/api/v1/tasks >/dev/null 2>&1; then
  echo "==> Iniciando frontend em foreground..."
  cd "$FRONTEND_DIR"
  npm start 2>&1 | tee "$FRONTEND_LOG"
else
  echo "==> Backend não respondeu em http://localhost:8080. Verifique o arquivo $BACKEND_LOG"
  exit 1
fi
