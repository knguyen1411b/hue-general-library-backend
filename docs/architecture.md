# Architecture

## Tong quan

Du an theo cau truc layer co ban cua Spring Boot:

`Controller -> Service -> Repository -> Database`

Ngoai ra con co mot so thanh phan ho tro:

- `config`: cau hinh application, security, OpenAPI, du lieu khoi tao
- `common`: response wrapper, constants, exception handler, validation
- `core`: dich vu dung chung nhu upload file va gui mail
- `modules/auth`: xac thuc, JWT, refresh token, reset password
- `modules/user`: thong tin nguoi dung, bo loc, phan quyen quan tri

## Cac module chinh

### 1. Security

`AppSecurityConfig` cau hinh:

- Tat CSRF cho mo hinh stateless API
- Dung `JwtAuthenticationFilter` truoc `UsernamePasswordAuthenticationFilter`
- Cho phep truy cap cong khai:
  - `/`
  - Swagger docs
  - cac endpoint auth can thiet
- Moi request con lai yeu cau xac thuc

### 2. Auth module

Thanh phan chinh:

- `AuthV1Controller`
- `AuthServiceImpl`
- `JwtService`
- `AuthUtils`
- DTO cho sign-in, sign-up, refresh, forgot/reset password

Chuc nang:

- Dang nhap bang `username + password`
- Tao `accessToken`
- Tao `refreshToken` ngau nhien, luu hash vao DB
- Dat lai mat khau bang token hash

### 3. User module

Thanh phan chinh:

- `UserV1Controller`
- `UserServiceImpl`
- `UserRepository`
- `UserSpecification`
- `User`, `UserDTO`, `UserCreateDTO`, `UserUpdateDTO`, `MeUpdateDTO`

Chuc nang:

- Lay thong tin user hien tai
- Cap nhat thong tin user hien tai theo kieu patch
- Quan tri danh sach user, chi tiet, tao, sua, xoa mem

### 4. File va Mail

- `FileService`: upload anh len Cloudinary
- `MailService`: gui plain text email va HTML email cho reset password

## Entity chinh

`User` la entity trung tam, gom:

- thong tin dang nhap: `username`, `passwordHash`
- thong tin ca nhan: `fullName`, `email`, `phone`, `birthday`, `address`, `gender`
- thong tin dinh danh: `identityNumber`, `identityFrontUrl`, `identityBackUrl`
- phan quyen: `role`, `status`
- bao mat: `refreshTokenHash`, `refreshTokenExpiredAt`, `resetPasswordTokenHash`, `resetPasswordTokenExpiredAt`

## Response format

API duoc wrap theo mot trong 3 dang:

- `ApiResponse`: chi co `success`, `statusCode`, `message`
- `DataApiResponse<T>`: them truong `data`
- `PagedApiResponse<T>`: them `data[]` va `meta` cho pagination

## Exception handling

`GlobalExceptionHandler` xu ly:

- `AppException` cho loi nghiep vu
- loi validation
- loi khong du quyen / chua dang nhap
- loi he thong khac

## Khoi tao du lieu

`ApplicationInitializer` se tao tai khoan admin mac dinh tu bien moi truong:

- `SUPER_USER_USERNAME`
- `SUPER_USER_PASSWORD`
- `SUPER_USER_EMAIL`
- `SUPER_USER_FULL_NAME`

Neu user nay chua ton tai, he thong se tao moi khi app startup.
