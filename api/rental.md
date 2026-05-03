# RentalV1Controller

- Tag Swagger: Phiếu mượn sách (V1)
- Base path: /api/v1/rentals

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/rentals` | Lấy danh sách phiếu mượn có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/rentals/{id}` | Xem chi tiết phiếu mượn | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/rentals` | Tạo phiếu mượn mới | `JWT (mac dinh)` |
| PUT | `/api/v1/rentals/{id}/return` | Xác nhận trả sách | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/rentals/{id}/renew-book` | Gia hạn sách | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/rentals/{id}/report-lost` | Báo mất sách | `hasRole('ADMIN') or hasRole('MANAGER')` |
| DELETE | `/api/v1/rentals/{id}` | Xóa phiếu mượn | `hasRole('ADMIN') or hasRole('MANAGER')` |
