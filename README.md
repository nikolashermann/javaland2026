# Service-to-Service Authentication with Keycloak & Spring Security

Demo project for my talk **[Service-to-service authentication mit Keycloak und Spring Security](https://meine.doag.org/events/javaland/2026/agenda/#agendaId.6904)** at **JavaLand 2026**.

This repository demonstrates how to implement secure communication between two Spring Boot microservices (`api-client` and `api-server`) using OAuth2 Client Credentials flow with Keycloak as the Identity Provider.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/) or [Podman](https://podman.io/)
- [Terraform](https://www.terraform.io/downloads)

## Getting Started

### 1. Start Infrastructure
Start Keycloak and the application services using Docker Compose:

```bash
docker compose up -d
```

Keycloak will be available at `http://localhost:8081`. The admin credentials are `admin`/`admin`.

### 2. Configure Keycloak via Terraform
Initialize and apply the Terraform configuration to set up the realm, client, and scopes:

```bash
cd terraform
terraform init
terraform apply -auto-approve
```

The Terraform script will also generate a `client_secret` file in the `terraform` folder, which is mounted as a Docker secret for the `api-client`.

## Spring Profiles

The project supports two authentication methods, switchable via Spring profiles in `docker-compose.yml`:

- **`oauth2` (Default)**: Uses Keycloak and OAuth2 Client Credentials flow. Both services must be started with this profile.
- **`basic`**: Uses simple HTTP Basic Authentication. Useful for comparing implementations.

To change the profile, update the `-Dspring-boot.run.profiles` argument in the `command` section of `docker-compose.yml` for both `api-server` and `api-client`, then restart the containers:

```bash
docker compose up -d --force-recreate
```

## Testing the Project

### Web Interface
Open your browser and navigate to:
[http://localhost:8080](http://localhost:8080)

The `api-client` will fetch data from the `api-server` using the configured authentication and display a list of articles.

## Rotating Client Credentials

To rotate the client secret, you only need to regenerate the secret in Keycloak. The `api-client` will automatically pick up the new secret without a restart, as it reads the secret file on every request (via Docker secret mount):

### Regenerate the secret via Terraform
```bash
cd terraform
terraform apply -replace="random_password.api_client_javaland_01" -auto-approve
```
This will create a new random password and update the `client_secret` file.
