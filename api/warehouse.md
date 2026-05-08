# WarehouseV1Controller

- Tag Swagger: Kho (V1)
- Base path: /api/v1/warehouse

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/warehouse/tree` | Lấy cấu trúc kho hàng dạng cây | `JWT (mac dinh)` |
| POST | `/api/v1/warehouse/floors` | Tạo tầng kho | `JWT (mac dinh)` |
| PUT | `/api/v1/warehouse/floors/{id}` | Cập nhật tầng kho | `JWT (mac dinh)` |
| DELETE | `/api/v1/warehouse/floors/{id}` | Xóa tầng kho | `JWT (mac dinh)` |
| GET | `/api/v1/warehouse/floors/{id}` | Lấy chi tiết tầng kho | `JWT (mac dinh)` |
| GET | `/api/v1/warehouse/floors` | Lấy danh sách tầng kho | `JWT (mac dinh)` |
| POST | `/api/v1/warehouse/aisles` | Tạo dãy kệ | `JWT (mac dinh)` |
| PUT | `/api/v1/warehouse/aisles/{id}` | Cập nhật dãy kệ | `JWT (mac dinh)` |
| DELETE | `/api/v1/warehouse/aisles/{id}` | Xóa dãy kệ | `JWT (mac dinh)` |
| GET | `/api/v1/warehouse/aisles/{id}` | Lấy chi tiết dãy kệ | `JWT (mac dinh)` |
| GET | `/api/v1/warehouse/aisles` | Lấy danh sách dãy kệ | `JWT (mac dinh)` |
| POST | `/api/v1/warehouse/shelves` | Thêm kệ mới | `JWT (mac dinh)` |
| DELETE | `/api/v1/warehouse/shelves/{id}` | Xóa kệ theo ID | `JWT (mac dinh)` |
| GET | `/api/v1/warehouse/shelves` | Lấy danh sách kệ | `JWT (mac dinh)` |
