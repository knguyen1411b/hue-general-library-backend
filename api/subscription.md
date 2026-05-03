# SubscriptionV1Controller

- Tag Swagger: Gói cước (V1)
- Base path: /api/v1/subscriptions

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/subscriptions` | Lấy danh sách gói đăng ký có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/subscriptions/{id}` | Lấy chi tiết gói đăng ký theo ID | `JWT (mac dinh)` |
| GET | `/api/v1/subscriptions/key/{key}` | Lấy chi tiết gói đăng ký theo key | `JWT (mac dinh)` |
| POST | `/api/v1/subscriptions` | Tạo mới gói đăng ký | `JWT (mac dinh)` |
| PATCH | `/api/v1/subscriptions/{id}` | Cập nhật gói đăng ký theo ID | `JWT (mac dinh)` |
| DELETE | `/api/v1/subscriptions/{id}` | Xóa gói đăng ký theo ID | `JWT (mac dinh)` |
