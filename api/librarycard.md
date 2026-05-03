# LibraryCardV1Controller

- Tag Swagger: Thẻ thư viện (V1)
- Base path: /api/v1/library-cards

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/library-cards` | Lấy danh sách thẻ thư viện có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/library-cards/{id}` | Xem chi tiết thẻ thư viện | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/library-cards` | Tạo thẻ thư viện mới | `hasRole('ADMIN') or hasRole('MANAGER')` |
| PATCH | `/api/v1/library-cards/{id}` | Cập nhật thông tin thẻ thư viện | `JWT (mac dinh)` |
| POST | `/api/v1/library-cards/{id}/lock` | Khóa thẻ thư viện | `hasRole('ADMIN')` |
| POST | `/api/v1/library-cards/{id}/replace` | Làm lại thẻ thư viện mới | `hasRole('ADMIN')` |
| DELETE | `/api/v1/library-cards/{id}` | Xóa thẻ thư viện | `hasRole('ADMIN')` |
| POST | `/api/v1/library-cards/request` | Yêu cầu cấp thẻ vật lý mới | `JWT (mac dinh)` |
