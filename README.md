# Room Booking Backend

Backend service for room discovery, booking, and check-in workflows for MCI locations.
Built with Ktor, Exposed, and JWT auth, with PostgreSQL or in-memory H2 for persistence.

## Features
- JWT-based authentication and role-based access (admin and user flows).
- Room, building, booking, and equipment management.
- Check-in confirmations with MQTT publishing for room displays.
- Swagger UI and generated OpenAPI spec.

## Requirements
- JDK 17+
- Docker (optional, for Postgres/pgAdmin)

## Quick Start (Gradle, in-memory H2)
```bash
./gradlew run
```

App starts at `http://localhost:8080`.

H2 web console is started automatically at `http://localhost:8082`:
- JDBC URL: `jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;`
- User: `root`
- Password: empty

Seed data is inserted on every startup (see `src/main/kotlin/plugins/Seeding.kt`).

## Docker Compose (PostgreSQL + pgAdmin)
```bash
docker compose up --build
```

Services:
- API: `http://localhost:8080`
- Postgres: `localhost:5433` (db `roombooking`, user `admin`, password `password`)
- pgAdmin: `http://localhost:5050` (login `admin@mci.edu`, password `password`)

## Configuration
Defaults live in `src/main/resources/application.yaml`. Override with env vars:

| Variable          | Default                                         | Description                    |
|-------------------|-------------------------------------------------|--------------------------------|
| `PORT`            | `8080`                                          | HTTP port                      |
| `DB_MODE`         | `inmemory`                                      | `inmemory` or `postgres`       |
| `DB_URL`          | `jdbc:postgresql://localhost:5433/roombooking`  | Postgres JDBC URL              |
| `DB_USER`         | `admin`                                         | DB username                    |
| `DB_PASSWORD`     | `password`                                      | DB password                    |
| `DB_DRIVER`       | `org.postgresql.Driver`                         | JDBC driver                    |
| `JWT_SECRET`      | `verysecret`                                    | JWT signing secret             |
| `JWT_ISSUER`      | `https://roombooking-backend-l7kv.onrender.com` | JWT issuer                     |
| `JWT_AUDIENCE`    | `roombooking-clients`                           | JWT audience                   |
| `MQTT_BROKER_URL` | `tcp://localhost:1883`                          | MQTT broker URL                |
| `MQTT_CLIENT_ID`  | `RoomBookingBackend`                            | MQTT client ID                 |
| `ENABLE_OPENAPI`  | `true`                                          | Generate OpenAPI at build time |
| `SWAGGER_FILE`    | `openapi/open-api.json`                         | Swagger UI spec path           |

## Authentication
1. `POST /auth/login` with credentials.
2. Use the returned token as `Authorization: Bearer <token>`.

Example request:
```json
{
  "email": "admin@mci.edu",
  "password": "password"
}
```

## Seeded Accounts (for local/dev)
All seeded passwords are `password`.

Admin:
- `admin@mci.edu`

User:
- `student@mci.edu`

More seed data is listed in `SEED_DATA.md`.

## API Documentation
Swagger UI is available at:
- `http://localhost:8080/swagger`

## MQTT Integration
The service publishes room check-in codes to:
- Topic: `room/{roomId}/code`
- Payload: 4-digit code (string)
- QoS: 1
- Retained: true

Local broker example:
```bash
brew install mosquitto && mosquitto
mosquitto_sub -t "room/+/code" -v
```

## AI Disclosure
AI assistance was used to generate `README.md`, `SEED_DATA.md`, `src/main/kotlin/plugins/Seeding.kt`,
and the KDoc API documentation in the routes.
