# ODA Payment Service
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/OpenDonationAssistant/oda-payment-service)

---

## Running with Docker

The service is published as a Docker image to GitHub Container Registry.

### Pull and Run

```bash
docker pull ghcr.io/opendonationassistant/oda-payment-service:latest

docker run -d \
  --name oda-payment-service \
  -e RABBITMQ_HOST=<rabbitmq-host> \
  -e JDBC_URL=<jdbc-url> \
  -e JDBC_USER=<db-username> \
  -e JDBC_PASSWORD=<db-password> \
  -p 8080:8080 \
  ghcr.io/opendonationassistant/oda-payment-service:latest
```

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `RABBITMQ_HOST` | RabbitMQ server hostname | `localhost` |
| `JDBC_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/postgres?currentSchema=payment` |
| `JDBC_USER` | Database username | `postgres` |
| `JDBC_PASSWORD` | Database password | `postgres` |

### Example docker-compose.yml

```yaml
version: '3.8'
services:
  payment-service:
    image: ghcr.io/opendonationassistant/oda-payment-service:latest
    environment:
      - RABBITMQ_HOST=rabbitmq
      - JDBC_URL=jdbc:postgresql://postgres:5432/postgres?currentSchema=payment
      - JDBC_USER=postgres
      - JDBC_PASSWORD=postgres
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq

  postgres:
    image: postgres:16
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - postgres-data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  postgres-data:
```


