# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spare parts management system built with Spring Boot 4.0.3 and Java 21.

## Build & Test Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SparepartsApplicationTests

# Run a single test method
./mvnw test -Dtest=SparepartsApplicationTests#contextLoads

# Run the application
./mvnw spring-boot:run
```

## Architecture

- **Base package:** `sn.symmetry.spareparts`
- **Build:** Maven with Lombok annotation processing
- **Database:** PostgreSQL with Flyway migrations in `src/main/resources/db/migration/`
- **Caching:** Redis via Spring Data Redis
- **API:** Spring Data REST for automatic repository-based REST endpoints, plus Spring Web MVC
- **Data access:** Spring Data JPA and JDBC
- **Config:** `src/main/resources/application.yaml`
