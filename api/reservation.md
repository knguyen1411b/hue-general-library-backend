# ReservationV1Controller

- Tag Swagger: Đặt trước sách (V1)
- Base path: /api/v1/reservations

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| POST | `/api/v1/reservations` | Tạo phiếu đặt trước sách | `JWT (mac dinh)` |
| GET | `/api/v1/reservations/{id}` | Lấy chi tiết phiếu đặt trước theo ID | `JWT (mac dinh)` |
| PUT | `/api/v1/reservations/{id}/confirm` | Xác nhận phiếu đặt trước | `JWT (mac dinh)` |
| PUT | `/api/v1/reservations/{id}/cancel` | Hủy phiếu đặt trước | `JWT (mac dinh)` |
| GET | `/api/v1/reservations` | Lấy danh sách phiếu đặt trước | `JWT (mac dinh)` |
