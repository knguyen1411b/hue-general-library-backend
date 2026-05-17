# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog, and this project follows Semantic Versioning where applicable.

## [Unreleased]

### Added

- CI workflow for format checks, tests, and build.
- `docker-compose.yml` for local onboarding.
- `application-prod.yaml` for production profile defaults.
- `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, and `CHANGELOG.md`.

### Changed

- Hardened subscription service authorization to require `ADMIN` or `MANAGER`.
- Moved CORS origin patterns to environment-driven configuration.

## [0.0.1] - 2026-05-17

### Added

- Initial public documentation and security policy files.
