# BookItemV1Controller

- Tag Swagger: Bản sách (V1)
- Base path: /api/v1/book-items

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/book-items` | Lấy danh sách bản sách có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/book-items/{id}` | Lấy chi tiết bản sách theo ID | `JWT (mac dinh)` |
| POST | `/api/v1/book-items` | Tạo mới bản sách | `JWT (mac dinh)` |
| PATCH | `/api/v1/book-items/{id}` | Cập nhật bản sách theo ID | `JWT (mac dinh)` |
| DELETE | `/api/v1/book-items/{id}` | Xóa bản sách theo ID | `JWT (mac dinh)` |
