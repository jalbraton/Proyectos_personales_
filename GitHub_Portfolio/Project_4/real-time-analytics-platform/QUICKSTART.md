# Quick Start Guide

## Prerequisites

- **Docker & Docker Compose**: v20.10+
- **Kubernetes**: v1.28+ (for production)
- **Node.js**: v18+
- **Python**: 3.11+
- **kubectl**: Latest version
- **Helm**: v3+ (optional)

## Local Development Setup

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/real-time-analytics-platform.git
cd real-time-analytics-platform
```

### 2. Environment Variables

Create `.env` file in root:

```bash
# Database
POSTGRES_USER=admin
POSTGRES_PASSWORD=your-secure-password
POSTGRES_DB=platform

# Redis
REDIS_URL=redis://redis:6379

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this

# Services
API_GATEWAY_URL=http://localhost:8000
AUTH_SERVICE_URL=http://localhost:8001
WEBSOCKET_SERVICE_URL=http://localhost:8004

# Frontend
REACT_APP_API_URL=http://localhost:8000
REACT_APP_WEBSOCKET_URL=http://localhost:8004
```

### 3. Start Services with Docker Compose

```bash
# Start all services
docker-compose up -d

# Check services status
docker-compose ps

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### 4. Access Applications

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8000
- **Auth Service**: http://localhost:8001
- **WebSocket Service**: http://localhost:8004
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin123)
- **Kibana**: http://localhost:5601
- **RabbitMQ**: http://localhost:15672 (admin/admin123)

## Development Workflow

### Backend Development (Python)

```bash
cd backend/auth-service

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run service
uvicorn src.main:app --reload --host 0.0.0.0 --port 8001
```

### Backend Development (Node.js)

```bash
cd backend/api-gateway

# Install dependencies
npm install

# Run service in development mode
npm run dev
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

## Testing

### Run All Tests

```bash
# Backend tests
cd backend/auth-service && pytest
cd backend/api-gateway && npm test
cd backend/websocket-service && npm test

# Frontend tests
cd frontend && npm test

# Integration tests
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

### Load Testing

```bash
# Install K6
brew install k6  # macOS
choco install k6  # Windows

# Run load tests
k6 run tests/load/api-load-test.js
```

## Production Deployment

### Kubernetes Deployment

```bash
# Create namespace
kubectl apply -f kubernetes/base/namespace.yaml

# Deploy secrets (update values first!)
kubectl apply -f kubernetes/base/secrets.yaml

# Deploy services
kubectl apply -f kubernetes/deployments/

# Check deployment status
kubectl get pods -n platform
kubectl get services -n platform
kubectl get ingress -n platform

# Scale services
kubectl scale deployment auth-service --replicas=5 -n platform
```

### Monitoring Setup

```bash
# Deploy Prometheus
kubectl apply -f monitoring/prometheus/

# Deploy Grafana
kubectl apply -f monitoring/grafana/

# Import dashboards
# Navigate to Grafana UI and import JSON dashboards from monitoring/grafana/dashboards/
```

## Common Commands

### Docker

```bash
# Rebuild specific service
docker-compose build auth-service
docker-compose up -d auth-service

# View service logs
docker-compose logs -f api-gateway

# Execute command in container
docker-compose exec auth-service bash

# Remove all containers and volumes
docker-compose down -v
```

### Kubernetes

```bash
# View pod logs
kubectl logs -f deployment/auth-service -n platform

# Describe pod
kubectl describe pod <pod-name> -n platform

# Execute command in pod
kubectl exec -it <pod-name> -n platform -- bash

# Port forward for debugging
kubectl port-forward service/auth-service 8001:8001 -n platform

# Rollback deployment
kubectl rollout undo deployment/auth-service -n platform
```

## Troubleshooting

### Service Not Starting

```bash
# Check logs
docker-compose logs <service-name>

# Check container status
docker ps -a

# Restart service
docker-compose restart <service-name>
```

### Database Connection Issues

```bash
# Check PostgreSQL
docker-compose exec postgres psql -U admin -d platform -c "SELECT version();"

# Check Redis
docker-compose exec redis redis-cli ping
```

### Port Already in Use

```bash
# Find process using port
lsof -i :8000  # macOS/Linux
netstat -ano | findstr :8000  # Windows

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

## Next Steps

1. **Configure Authentication**: Update JWT secrets and OAuth2 settings
2. **Setup Monitoring**: Configure Prometheus alerts and Grafana dashboards
3. **Enable HTTPS**: Setup SSL/TLS certificates with Let's Encrypt
4. **Configure Scaling**: Adjust HPA settings based on load
5. **Backup Strategy**: Setup automated database backups
6. **CI/CD Pipeline**: Configure GitHub Actions for automated deployment

## Support

- **Documentation**: See `/docs` folder
- **Issues**: GitHub Issues
- **Slack**: Join our Slack channel
- **Email**: support@yourcompany.com

## License

MIT License - See LICENSE file for details
