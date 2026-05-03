# FineV1Controller

- Tag Swagger: Phiếu phạt (V1)
- Base path: /api/v1/fines

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/fines` | Lấy danh sách phiếu phạt có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/fines/{id}` | Xem chi tiết phiếu phạt | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/fines` | Tạo phiếu phạt mới | `hasRole('ADMIN') or hasRole('MANAGER')` |
| PUT | `/api/v1/fines/{id}/pay` | Xác nhận thanh toán phiếu phạt | `hasRole('ADMIN') or hasRole('MANAGER')` |
| DELETE | `/api/v1/fines/{id}` | Xóa phiếu phạt | `hasRole('ADMIN') or hasRole('MANAGER')` |
