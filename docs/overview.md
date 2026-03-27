# Project Overview

## Muc tieu

Day la backend cho bai toan quan ly nguoi dung va xac thuc bang JWT. He thong cung cap cac chuc nang chinh:

- Dang ky tai khoan bang `multipart/form-data`
- Dang nhap, cap lai access token, dang xuat
- Quen mat khau va dat lai mat khau qua email
- Lay va cap nhat thong tin ca nhan
- Quan tri danh sach nguoi dung cho `ADMIN` va `MANAGER`

## Tech stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- JWT (`jjwt`)
- Spring Data JPA
- PostgreSQL
- Cloudinary de luu anh
- Spring Mail de gui email
- springdoc OpenAPI / Swagger UI
- Spotless de format code

## Pham vi hien tai

- Access token duoc tao tu `userId`
- Refresh token va reset password token duoc luu duoi dang hash trong database
- API tra ve response wrapper thong nhat
- Co tao tai khoan admin mac dinh khi khoi dong neu chua ton tai

## Luu y quan trong

- Cau hinh JPA dang de `create-drop`, phu hop moi truong local/dev, khong nen dung nguyen trang cho production
- Flyway hien dang tat (`spring.flyway.enabled=false`)
- Task test trong Gradle dang bi tat, nen `gradlew build` hien tai chi kiem tra compile + package + formatting
- Link email reset password dang tro ve frontend local: `http://localhost:3000/reset-password`

## Tai lieu lien quan

- `docs/architecture.md`: kien truc va cac thanh phan chinh
- `docs/business-flow.md`: luong nghiep vu
- `docs/api.md`: tong hop endpoint va request/response
- `docs/setup.md`: huong dan chay du an
