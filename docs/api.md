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

## Subscription APIs

Base path: `/api/subscriptions`

| Method | URL | Role(s) | Mô tả |
|--------|-----|---------|------|
| **GET** | `/api/subscriptions` | `ADMIN`, `LIBRARIAN` | Lấy danh sách các gói đăng ký (có phân trang). |
| **GET** | `/api/subscriptions/{id}` | `ADMIN`, `LIBRARIAN`, `USER` | Lấy chi tiết một gói đăng ký theo **UUID**. |
| **GET** | `/api/subscriptions/key/{key}` | `ADMIN`, `LIBRARIAN`, `USER` | Lấy chi tiết gói đăng ký theo **key** (không phân biệt chữ hoa/thường). |
| **POST** | `/api/subscriptions` | `ADMIN` | Tạo mới một gói đăng ký. |
| **PUT** | `/api/subscriptions/{id}` | `ADMIN` | Cập nhật thông tin gói đăng ký. |
| **DELETE** | `/api/subscriptions/{id}` | `ADMIN` | Xóa (đánh dấu **DELETED**) một gói đăng ký. |

#### Request / Response mẫu

**GET `/api/subscriptions`** (phân trang)

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Lấy danh sách gói đăng ký thành công",
  "data": [
    {
      "id": "c1a2b3d4‑e5f6‑7890‑abcd‑1234567890ab",
      "key": "PREMIUM",
      "name": "Gói Premium",
      "maxBooks": 10,
      "price": 199.99,
      "durationDays": 30,
      "status": "ACTIVE"
    }
    // … các mục khác
  ],
  "meta": {
    "page": 0,
    "size": 20,
    "totalPages": 1,
    "totalElements": 1
  }
}

** POST `/api/subscriptions`**

```json
{
  "key": "PREMIUM",
  "name": "Gói Premium",
  "maxBooks": 10,
  "price": 199.99,
  "durationDays": 30,
  "overdueFeePerDay": 1.5,
  "maxRenewals": 2,
  "compensationRate": 0.8
}

{
  "success": true,
  "statusCode": 201,
  "message": "Tạo gói đăng ký thành công",
  "data": {}
}

## User‑Subscription APIs

Base path: `/api/v1/usersubscriptions`

| Method | URL | Role(s) | Mô tả |
|--------|-----|---------|------|
| **GET** | `/api/v1/usersubscriptions` | `ADMIN`, `LIBRARIAN` | Lấy danh sách toàn bộ user‑subscription (có phân trang). |
| **GET** | `/api/v1/usersubscriptions/{id}` | `ADMIN`, `LIBRARIAN`, `USER` | Lấy chi tiết một user‑subscription theo **UUID**. |
| **GET** | `/api/v1/usersubscriptions/user/{userId}` | `ADMIN`, `LIBRARIAN`, `USER` | Lấy tất cả subscription của một người dùng. |
| **POST** | `/api/v1/usersubscriptions` | `ADMIN`, `LIBRARIAN` | Tạo mới một user‑subscription (đăng ký gói). |
| **PUT** | `/api/v1/usersubscriptions/{id}` | `ADMIN`, `LIBRARIAN` | Cập nhật thông tin (ngày bắt đầu, ngày kết thúc, trạng thái, …). |
| **PATCH** | `/api/v1/usersubscriptions/{id}/activate` | `ADMIN`, `LIBRARIAN` | Kích hoạt subscription. |
| **PATCH** | `/api/v1/usersubscriptions/{id}/expire` | `ADMIN`, `LIBRARIAN` | Đánh dấu subscription là **EXPIRED**. |
| **PATCH** | `/api/v1/usersubscriptions/{id}/cancel` | `ADMIN`, `LIBRARIAN` | Đánh dấu subscription là **CANCELED**. |
| **POST** | `/api/v1/usersubscriptions/{id}/renew` | `ADMIN`, `LIBRARIAN` | Gia hạn subscription (cập nhật start‑date & end‑date). |
| **GET** | `/api/v1/usersubscriptions/active/user/{userId}` | `ADMIN`, `LIBRARIAN`, `USER` | Lấy các subscription **ACTIVE** và chưa hết hạn của người dùng. |
| **GET** | `/api/v1/usersubscriptions/expired` | `ADMIN`, `LIBRARIAN` | Lấy danh sách các subscription đã hết hạn. |
| **GET** | `/api/v1/usersubscriptions/canceled` | `ADMIN`, `LIBRARIAN` | Lấy danh sách các subscription đã hủy. |
| **GET** | `/api/v1/usersubscriptions/count/active` | `ADMIN`, `LIBRARIAN` | Đếm tổng số subscription đang ACTIVE. |
| **GET** | `/api/v1/usersubscriptions/count/expired` | `ADMIN`, `LIBRARIAN` | Đếm tổng số subscription đã EXPIRED. |
| **GET** | `/api/v1/usersubscriptions/count/canceled` | `ADMIN`, `LIBRARIAN` | Đếm tổng số subscription đã CANCELED. |

#### Request / Response mẫu

**POST `/api/v1/usersubscriptions`** (đăng ký gói mới)

```json
{
  "userId": "a1b2c3d4‑e5f6‑7890‑abcd‑1234567890ab",
  "subscriptionId": "c1a2b3d4‑e5f6‑7890‑abcd‑1234567890ab",
  "startDate": "2024-04-01",
  "endDate": "2024-05-01",
  "maxBooks": 10,
  "price": 199.99
}

{
  "success": true,
  "statusCode": 201,
  "message": "Tạo user‑subscription thành công",
  "data": {}
}

GET `/api/v1/usersubscriptions/active/user/{userId}`** (danh sách subscription đang hoạt động)

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Lấy danh sách subscription đang hoạt động thành công",
  "data": [
    {
      "id": "d4e5f6a7‑b8c9‑0123‑def0‑1234567890ab",
      "userId": "a1b2c3d4‑e5f6‑7890‑abcd‑1234567890ab",
      "subscriptionId": "c1a2b3d4‑e5f6‑7890‑abcd‑1234567890ab",
      "startDate": "2024-04-01",
      "endDate": "2024-05-01",
      "status": "ACTIVE",
      "maxBooks": 10,
      "price": 199.99
    }
  ]
}
