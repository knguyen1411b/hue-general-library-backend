# BookV1Controller

- Tag Swagger: Quản lý đầu sách (V1)
- Base path: /api/v1/books

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/books` | Lấy danh sách đầu sách có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/books/{id}` | Xem chi tiết đầu sách | `JWT (mac dinh)` |
| POST | `/api/v1/books` | Tạo mới đầu sách (Gửi kèm file ảnh) | `JWT (mac dinh)` |
| PUT | `/api/v1/books/{id}` | Cập nhật thông tin đầu sách | `JWT (mac dinh)` |
| DELETE | `/api/v1/books/{id}` | Xóa đầu sách (Soft Delete) | `JWT (mac dinh)` |
