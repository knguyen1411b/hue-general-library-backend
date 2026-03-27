# API Reference

Base path: `/api/v1`

Swagger UI mac dinh:

- `/swagger-ui/index.html`
- `/v3/api-docs`

## Response format

### Success co data

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Thong bao",
  "data": {}
}
```

### Success co pagination

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Thong bao",
  "data": [],
  "meta": {
    "page": 0,
    "size": 20,
    "totalPages": 1,
    "totalElements": 1
  }
}
```

### Error don gian

```json
{
  "success": false,
  "statusCode": 401,
  "message": "Thong bao loi"
}
```

## Auth APIs

### POST `/api/v1/auth/sign-in`

Content-Type: `application/json`

Request:

```json
{
  "username": "admin",
  "password": "Password123"
}
```

Response data:

- `accessToken`
- `refreshToken`
- `accessTokenExpiration`
- `refreshTokenExpiration`

### POST `/api/v1/auth/refresh`

Content-Type: `application/json`

Request:

```json
{
  "refreshToken": "raw-refresh-token"
}
```

### POST `/api/v1/auth/sign-up`

Content-Type: `multipart/form-data`

Fields:

- `username` (required)
- `password` (required)
- `fullName` (required)
- `email` (required)
- `phone`
- `gender`
- `birthday`
- `address`
- `avatar` (image)
- `identityNumber` (required)
- `identityFront` (required, image)
- `identityBack` (required, image)

### DELETE `/api/v1/auth/sign-out`

Yeu cau Bearer token.

### POST `/api/v1/auth/change-password`

Yeu cau Bearer token.

Content-Type: `application/json`

Request:

```json
{
  "currentPassword": "Password123",
  "newPassword": "Newpass123"
}
```

### POST `/api/v1/auth/forgot-password`

Content-Type: `application/json`

Request:

```json
{
  "email": "user@example.com"
}
```

Luu y:

- Neu email khong ton tai, API van tra ve thanh cong de tranh lo thong tin tai khoan

### POST `/api/v1/auth/reset-password`

Content-Type: `application/json`

Request:

```json
{
  "token": "reset-token-from-email",
  "newPassword": "Newpass123"
}
```

## User APIs

Tat ca endpoint duoi day yeu cau Bearer token.

### GET `/api/v1/users/me`

Lay thong tin user hien tai.

### PATCH `/api/v1/users/me`

Content-Type: `multipart/form-data`

Patch fields:

- `password`
- `fullName`
- `email`
- `phone`
- `gender`
- `birthday`
- `address`
- `avatar`

Chi cac field duoc gui len moi bi cap nhat.

### GET `/api/v1/users`

Yeu cau role `ADMIN` hoac `MANAGER`.

Query params:

- `q`: tim theo username, email, fullName, phone, address
- `status`: `ACTIVE | INACTIVE | LOCKED | DELETED`
- `role`: `ADMIN | MANAGER | USER`
- params pagination mac dinh cua Spring (`page`, `size`, `sort`)

### GET `/api/v1/users/{id}`

Yeu cau role `ADMIN` hoac `MANAGER`.

### POST `/api/v1/users`

Yeu cau role `ADMIN` hoac `MANAGER`.

Content-Type: `multipart/form-data`

Fields:

- `username` (required)
- `password` (required)
- `fullName` (required)
- `email` (required)
- `phone`
- `gender`
- `birthday`
- `address`
- `avatar`
- `identityNumber` (required)
- `identityFront` (required)
- `identityBack` (required)
- `status` (required)
- `role` (required)

### PATCH `/api/v1/users/{id}`

Yeu cau role `ADMIN` hoac `MANAGER`.

Content-Type: `multipart/form-data`

Patch fields:

- `password`
- `fullName`
- `email`
- `phone`
- `gender`
- `birthday`
- `address`
- `avatar`
- `status`
- `role`

### DELETE `/api/v1/users/{id}`

Yeu cau role `ADMIN` hoac `MANAGER`.

Thuc te la xoa mem bang cach set `status = DELETED`.
