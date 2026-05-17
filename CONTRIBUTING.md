# Contributing Guide

Thanks for your interest in improving this project.

## Development Setup

1. Fork and clone the repository.
2. Create a local `.env` from `.env.example`.
3. Run services locally:
   - `./gradlew bootRun`
   - or `docker compose up --build`

## Branch Naming

Use descriptive branch names:

- `feat/<short-topic>`
- `fix/<short-topic>`
- `chore/<short-topic>`

## Commit Style

Keep commits small and focused.

Recommended prefixes:

- `feat:` new functionality
- `fix:` bug fix
- `refactor:` internal changes without behavior change
- `docs:` documentation updates
- `test:` tests only

## Pull Request Checklist

- Code builds successfully
- `./gradlew spotlessCheck` passes
- `./gradlew test` passes
- Changes are documented in `README.md` when needed
- `CHANGELOG.md` is updated for user-facing changes

## Coding Principles

- Keep changes minimal and goal-driven
- Avoid unrelated refactors
- Add tests for bug fixes or behavior changes when feasible
- Preserve existing project style and conventions
