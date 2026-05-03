# UserSubscriptionV1Controller

- Tag Swagger: Đăng ký gói cước (V1)
- Base path: /api/v1/user-subscriptions

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| GET | `/api/v1/user-subscriptions` | Lấy danh sách đăng ký người dùng có phân trang | `JWT (mac dinh)` |
| GET | `/api/v1/user-subscriptions/{id}` | Lấy chi tiết đăng ký người dùng theo ID | `hasRole('ADMIN') or hasRole('MANAGER')` |
| POST | `/api/v1/user-subscriptions` | Tạo mới đăng ký người dùng | `JWT (mac dinh)` |
| PATCH | `/api/v1/user-subscriptions/{id}` | (chua khai bao summary) | `JWT (mac dinh)` |
| GET | `/api/v1/user-subscriptions/user/{userId}` | Lấy danh sách đăng ký theo userId | `hasRole('ADMIN') or hasRole('MANAGER')` |
| GET | `/api/v1/user-subscriptions/subscription/{subscriptionId}` | Lấy danh sách đăng ký theo subscriptionId | `JWT (mac dinh)` |
| GET | `/api/v1/user-subscriptions/status/{status}` | Lấy danh sách đăng ký theo trạng thái | `JWT (mac dinh)` |
| GET | `/api/v1/user-subscriptions/user/{userId}/active` | Lấy danh sách đăng ký đang active của user | `JWT (mac dinh)` |
| POST | `/api/v1/user-subscriptions/{id}/activate` | Kích hoạt đăng ký | `JWT (mac dinh)` |
| POST | `/api/v1/user-subscriptions/{id}/expire` | Hết hạn đăng ký | `JWT (mac dinh)` |
| POST | `/api/v1/user-subscriptions/{id}/cancel` | Hủy đăng ký | `JWT (mac dinh)` |
| POST | `/api/v1/user-subscriptions/{id}/renew` | Gia hạn đăng ký | `JWT (mac dinh)` |
| DELETE | `/api/v1/user-subscriptions/{id}` | Xóa đăng ký người dùng theo ID | `JWT (mac dinh)` |
