#!/bin/bash

echo "🚀 Attus Task Management System - Quick Start"
echo "============================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker not found. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "✓ Docker found"

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose not found. Please install Docker Compose."
    echo "Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✓ Docker Compose found"
echo ""
echo "Starting services..."
echo ""

# Start services
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready (30 seconds)..."
sleep 30

# Check status
echo ""
echo "✓ Services started!"
echo ""
echo "🌐 Application URLs:"
echo "  - Frontend: http://localhost:4200"
echo "  - Backend:  http://localhost:8080"
echo "  - API Docs: http://localhost:8080/api/v1/tasks"
echo "  - Database: localhost:5432"
echo ""
echo "📝 Database Credentials:"
echo "  - Username: attus_user"
echo "  - Password: attus_password"
echo "  - Database: attus_db"
echo ""
echo "📖 For more info, see:"
echo "  - README.md"
echo "  - SETUP.md"
echo "  - docs/API.md"
echo ""
echo "💡 Useful commands:"
echo "  - View logs:       docker-compose logs -f"
echo "  - Stop services:   docker-compose down"
echo "  - Restart:         docker-compose restart"
echo ""
echo "✨ Ready to use! Open http://localhost:4200 in your browser"
