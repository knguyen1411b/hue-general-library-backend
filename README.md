# Hue General Library Backend

Backend nay duoc xay dung bang Spring Boot de cung cap he thong xac thuc JWT va quan ly nguoi dung. Du an hien tap trung vao cac use case co ban: dang ky, dang nhap, refresh token, quen mat khau, cap nhat thong tin ca nhan, va quan tri user cho `ADMIN`/`MANAGER`.

## Tinh nang chinh

- Dang ky tai khoan voi upload avatar va anh giay to
- Dang nhap bang `username + password`
- Refresh token co rotate token moi
- Dang xuat bang cach vo hieu hoa refresh token
- Quen mat khau / dat lai mat khau qua email
- Lay va cap nhat thong tin user hien tai
- CRUD user o muc quan tri
- Swagger UI de test API

## Cong nghe

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Cloudinary
- Spring Mail

## Chay nhanh

1. Tao file `.env` tu `.env.example`
2. Dien day du bien moi truong cho database, JWT, mail, Cloudinary
3. Chay lenh:

```bash
./gradlew build
./gradlew bootRun
```

Neu dung Windows:

```bash
gradlew.bat build
gradlew.bat bootRun
```

Swagger UI mac dinh:

`http://localhost:<PORT>/swagger-ui/index.html`

## Cau truc tai lieu

- `docs/overview.md`: tong quan du an
- `docs/architecture.md`: kien truc he thong
- `docs/business-flow.md`: luong nghiep vu
- `docs/api.md`: danh sach endpoint
- `docs/setup.md`: huong dan setup local

## Luu y hien tai

- `ddl-auto` dang la `create-drop`, chi nen dung local/dev
- Flyway dang tat
- Gradle test task dang bi tat, vi vay `build` hien tai chu yeu xac nhan compile, package va formatting
- Link trong email reset password dang tro ve frontend local `http://localhost:3000/reset-password`
