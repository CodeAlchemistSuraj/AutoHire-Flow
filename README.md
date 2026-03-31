# AutoHire-Flow - AI Career Co-Pilot Backend

**Production-Ready Spring Boot 3 Backend | Phase 1 (0-10k Users)**

AutoHire-Flow is an intelligent career management platform that leverages AI to match resumes with job opportunities and provides personalized cover letter generation. This is the backend API implementation following Clean Architecture principles.

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (Eclipse Temurin or OpenJDK)
- **Maven 3.9+**
- **Docker & Docker Compose** (for containerized setup)
- **PostgreSQL 16** with pgvector extension
- **Redis 7+**
- **Ollama** (for local AI inference)

### Local Development Setup

#### 1. Clone and Navigate

```bash
git clone <repository-url>
cd AutoHire-Flow
```

#### 2. Using Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# Check service health
docker-compose ps

# View logs
docker-compose logs -f app

# To stop
docker-compose down
```

#### 3. Manual Setup (Without Docker)

**Start PostgreSQL:**
```bash
# macOS with Homebrew
brew install postgresql@16
brew services start postgresql@16

# Or use Docker
docker run -d \
  --name postgres-autohire \
  -e POSTGRES_DB=autohire \
  -e POSTGRES_USER=autohire_user \
  -e POSTGRES_PASSWORD=autohire_pass \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

**Start Redis:**
```bash
# macOS with Homebrew
brew install redis
brew services start redis

# Or use Docker
docker run -d \
  --name redis-autohire \
  -p 6379:6379 \
  -e REDIS_PASSWORD=redis_pass \
  redis:7-alpine
```

**Start Ollama:**
```bash
# Download from https://ollama.ai
# Then start the server
ollama serve

# In another terminal, pull required models
ollama pull nomic-embed-text
ollama pull mistral
```

**Build and Run Application:**
```bash
# Activate dev profile
export SPRING_PROFILES_ACTIVE=dev

# Build
mvn clean package

# Run
java -jar target/autohire-flow-1.0.0-SNAPSHOT.jar
```

### Environment Variables

Create `.env` file in project root:

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=autohire
DB_USERNAME=autohire_user
DB_PASSWORD=autohire_pass

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=redis_pass

# Ollama
OLLAMA_URL=http://localhost:11434

# JWT
JWT_SECRET=your-secret-key-here

# Application
SPRING_PROFILES_ACTIVE=dev
```

## 📋 Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│        Web Layer (Controllers)          │
├─────────────────────────────────────────┤
│   Application Layer (Use Cases, DTOs)   │
├─────────────────────────────────────────┤
│  Domain Layer (Entities, Exceptions)    │
├─────────────────────────────────────────┤
│  Infrastructure Layer (Persistence,     │
│  External Services, Configuration)      │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.autohire.flow/
├── domain/                  # Domain models, exceptions
│   ├── model/             # User, Resume, JobPosting, etc.
│   ├── service/           # Domain business logic
│   └── exception/         # Domain exceptions
├── application/            # Use case implementations
│   ├── port/              # Interfaces (Dependency Inversion)
│   │   ├── incoming/      # Input ports (Use Cases)
│   │   └── outgoing/      # Output ports (Abstract dependencies)
│   ├── dto/               # DTOs for request/response
│   └── usecase/           # Use case implementations
├── infrastructure/         # External implementations
│   ├── persistence/       # JPA entities, repositories, adapters
│   ├── ai/               # Ollama integration services
│   ├── web/              # Controllers, GlobalExceptionHandler
│   ├── config/           # Spring configuration classes
│   ├── client/           # External API clients
│   └── storage/          # File storage implementations
└── common/                # Utilities, constants, annotations
    ├── util/              # JwtUtil, PasswordUtil, etc.
    ├── constant/          # AppConstants
    └── annotation/        # Custom annotations
```

## 🔌 API Endpoints

### Authentication

```bash
# Register
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "name": "John Doe"
}

# Login
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

### Resume Management

```bash
# Upload Resume
POST /api/v1/resume/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <pdf-file>
```

### Job Matching

```bash
# Calculate Match Score
POST /api/v1/matcher/score
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 123
}

# Get User's Matches
GET /api/v1/matches?status=PENDING&page=0&size=20
Authorization: Bearer <token>
```

### Cover Letter Generation

```bash
# Generate Cover Letter
POST /api/v1/cover-letter/generate
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 123,
  "tone": "PROFESSIONAL"
}
```

### Application Tracking

```bash
# Track Application
POST /api/v1/applications/track
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 123,
  "status": "APPLIED",
  "notes": "Applied via online portal"
}
```

### Feedback

```bash
# Submit Feedback
POST /api/v1/feedback/submit
Authorization: Bearer <token>
Content-Type: application/json

{
  "matchResultId": 456,
  "feedbackType": "SALARY_LOW",
  "comments": "Position salary below market rate"
}
```

## 📊 Database Schema

### Key Tables

- **users** - User accounts with authentication
- **resumes** - Uploaded resumes with pgvector embeddings
- **job_postings** - Job opportunities with embeddings
- **match_results** - Resume-job matches with scores
- **cover_letters** - Generated cover letters
- **feedback** - User feedback on matches
- **applications** - Application tracking
- **audit_log** - Audit trail for compliance

### Vector Indexes

- HNSW indexes on resume and job embeddings for fast similarity search
- Cosine similarity metric for semantic matching

## 🔐 Security

### Authentication & Authorization

- **JWT Tokens**: 24-hour expiry
- **Password Hashing**: BCrypt with salt
- **HTTPS/TLS**: All production traffic encrypted
- **CORS**: Configured for frontend domain

### Rate Limiting

- **Standard Endpoints**: 100 requests/minute per user
- **AI Endpoints**: 30 requests/minute per user (intensive processing)

### Data Protection

- **PII Encryption**: Personal data at rest (AES-256)
- **No Sensitive Data in Logs**: Passwords, tokens redacted
- **SQL Injection Prevention**: Parameterized queries
- **CSRF Protection**: Enabled for state-changing operations

## 📈 Performance & Monitoring

### Metrics Available

- **API Latency**: P95 < 300ms for standard endpoints
- **AI Processing**: P95 < 3s for matching/cover letter
- **Throughput**: Support for 50+ concurrent requests
- **Availability**: 99.9% target uptime

### Monitoring Endpoints

```bash
# Application Health
GET http://localhost:8080/actuator/health

# Metrics (Prometheus format)
GET http://localhost:8080/actuator/prometheus

# API Documentation (Swagger UI)
GET http://localhost:8080/swagger-ui.html
```

## 🧪 Testing

### Run Tests

```bash
#Unit and Integration Tests
mvn test

# Specific test class
mvn test -Dtest=UserServiceTest

# Test Coverage
mvn jacoco:report
```

### Test Containers

Integration tests use TestContainers for:
- PostgreSQL database
- Redis cache
- Ollama AI service (mocked)

## 🚀 Deployment

### Production Deployment

```bash
# Build Docker image
docker build -t autohire-flow:1.0.0 .

# Run with production profile
docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=<prod-db-host> \
  -e DB_PASSWORD=<prod-password> \
  -e JWT_SECRET=<prod-secret> \
  -p 8080:8080 \
  autohire-flow:1.0.0
```

### Kubernetes Deployment

```bash
# Apply configuration
kubectl apply -f kubernetes/

# Check deployment
kubectl get pods -l app=autohire-flow

# View logs
kubectl logs -f deployment/autohire-flow
```

### Database Migrations

Migrations run automatically on application startup using Flyway:
- `V1__init.sql` - Initial schema with pgvector support
- `V2__add_vectors.sql` - Vector indexes for performance
- `V3__add_audit.sql` - Audit logging tables (Phase 2)

## 🤖 AI Integration (Ollama)

### Models Used

- **nomic-embed-text** (768 dimensions)
  - Fast, efficient embeddings
  - Ideal for semantic similarity
  
- **Mistral** (7B parameters)
  - Balanced performance/quality
  - Good for cover letter generation

### Embedding Process

1. Resume text → tokenization → embedding vector (768-dim)
2. Job description → tokenization → embedding vector (768-dim)
3. Cosine similarity → match score (0-100%)

### Cover Letter Generation

1. User prompt from job + resume → context building
2. LLM generates 3-paragraph cover letter
3. Quality validation (min 200 words, 3 paragraphs)

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
1. Test (mvn test)
2. SonarQube Analysis
3. Build Docker Image
4. Push to Container Registry
5. Deploy to Staging
6. Integration Tests
7. Deploy to Production
```

## 📚 Documentation

- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Production Specification**: See `PRODUCTION_SPECIFICATION.md`

## Built with Spring Boot 3, Clean Architecture, and Ollama AI
