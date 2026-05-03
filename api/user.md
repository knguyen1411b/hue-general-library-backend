# UserV1Controller

- Tag Swagger: Người dùng (V1)
- Base path: /api/v1/users

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/users/me` | Lấy thông tin người dùng hiện tại | `JWT (mac dinh)` |
| PATCH | `/api/v1/users/me` | Cập nhật thông tin người dùng hiện tại | `JWT (mac dinh)` |
| GET | `/api/v1/users` | Lấy danh sách người dùng có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/users/{id}` | Lấy chi tiết người dùng theo ID | `JWT (mac dinh)` |
| POST | `/api/v1/users` | Tạo mới người dùng | `JWT (mac dinh)` |
| PATCH | `/api/v1/users/{id}` | Cập nhật người dùng theo ID | `JWT (mac dinh)` |
| DELETE | `/api/v1/users/{id}` | Xóa người dùng theo ID | `JWT (mac dinh)` |
| POST | `/api/v1/users/me/report-lost-card` | Báo mất thẻ thư viện | `JWT (mac dinh)` |
