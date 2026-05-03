# PaymentV1Controller

- Tag Swagger: Giao dịch thanh toán (V1)
- Base path: /api/v1/payments

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/payments` | Lấy danh sách giao dịch có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/payments/{id}` | Xem chi tiết giao dịch | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/payments` | Tạo giao dịch mới | `JWT (mac dinh)` |
| PUT | `/api/v1/payments/{id}/confirm` | Xác nhận thanh toán thành công | `hasRole('ADMIN') or hasRole('MANAGER')` |
| DELETE | `/api/v1/payments/{id}` | Xóa giao dịch | `hasRole('ADMIN') or hasRole('MANAGER')` |
