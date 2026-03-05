# Security remediation notes

## Scope
- Target dependencies: direct dependencies from `pom.xml` (Maven, Spring Boot)
- Severity threshold: **CRITICAL + HIGH** (default)
- Transitive dependencies: excluded from CVE scan per policy (can be mitigated via parent/BOM upgrades when practical)

## Findings
- `validate_cves` reported **no known CVEs** for the direct deps explicitly versioned in `pom.xml`:
  - `org.springframework.boot:spring-boot-starter-parent@3.1.4`
  - `io.jsonwebtoken:jjwt-* @0.12.3`
  - `org.openapitools:jackson-databind-nullable@0.2.6`

## Planned hardening
Even with no CVEs reported by the scanner for these coordinates, best practice is to keep Spring Boot on the latest patch release within the same minor line (3.1.x) or migrate to a newer compatible minor (3.2/3.3) if the project allows it.

This repo’s `pom.xml` currently uses:
- Java 21
- Spring Boot 3.1.4

Upgrading Spring Boot parent to a newer patch version may address indirectly-managed library CVEs (Tomcat, Jackson, SnakeYAML, etc.) without changing code.

## Build caveat
The current automated environment lacks Java (`java` not found / `JAVA_HOME` missing), so `mvn` commands can’t be executed here to verify the build. Once Java is available, run:
- `mvn -q clean test`

