# AuthV1Controller

- Tag Swagger: Xác thực (V1)
- Base path: /api/v1/auth

## Endpoints

| Method | Path | Summary | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/sign-in` | Đăng nhập | `Public` |
| POST | `/api/v1/auth/refresh` | Làm mới token | `Public` |
| POST | `/api/v1/auth/sign-up` | Đăng ký tài khoản | `Public` |
| DELETE | `/api/v1/auth/sign-out` | Đăng xuất | `Public` |
| POST | `/api/v1/auth/change-password` | Đổi mật khẩu | `JWT (mac dinh)` |
| POST | `/api/v1/auth/forgot-password` | Gửi email quên mật khẩu | `Public` |
| POST | `/api/v1/auth/reset-password` | Đặt lại mật khẩu | `Public` |
