# CategoryV1Controller

- Tag Swagger: Danh mục sách (V1)
- Base path: /api/v1/categories

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/categories` | Lấy danh sách danh mục (có phân trang & lọc) | `JWT (mac dinh)` |
| GET | `/api/v1/categories/{id}` | Lấy chi tiết danh mục theo ID | `JWT (mac dinh)` |
| POST | `/api/v1/categories` | Tạo mới thể danh mục (Chỉ Thủ thư) | `JWT (mac dinh)` |
| PATCH | `/api/v1/categories/{id}` | Cập nhật danh mục theo ID (Chỉ Thủ thư) | `JWT (mac dinh)` |
| DELETE | `/api/v1/categories/{id}` | Xóa danh mục theo ID (Chỉ Thủ thư) | `JWT (mac dinh)` |
