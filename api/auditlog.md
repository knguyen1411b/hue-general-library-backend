# AuditLogV1Controller

- Tag Swagger: Nhật ký hệ thống (V1)
- Base path: /api/v1/audit-logs

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/audit-logs` | Lấy danh sách nhật ký hệ thống | `JWT (mac dinh)` |
| GET | `/api/v1/audit-logs/{id}` | Lấy chi tiết nhật ký hệ thống | `hasRole('ADMIN')` |
