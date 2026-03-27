# Setup Guide

## Yeu cau

- JDK 21
- PostgreSQL
- Tai khoan Cloudinary
- Tai khoan email SMTP

Khuyen nghi:

- Dung `gradlew` di kem thay vi cai Gradle global

## 1. Tao file moi truong

Copy `.env.example` thanh `.env` va dien gia tri phu hop.

Danh sach bien can co:

### Thong tin ung dung

- `APPLICATION_NAME`
- `APPLICATION_VERSION`

### Super admin

- `SUPER_USER_USERNAME`
- `SUPER_USER_PASSWORD`
- `SUPER_USER_EMAIL`
- `SUPER_USER_FULL_NAME`

### JWT

- `JWT_SECRET`
- `JWT_ACCESS_EXPIRATION`
- `JWT_REFRESH_EXPIRATION`

`JWT_ACCESS_EXPIRATION` va `JWT_REFRESH_EXPIRATION` dang duoc su dung theo don vi milliseconds.

### Database

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Thread pool

- `THREAD_POOL_CORE_SIZE`
- `THREAD_POOL_MAX_SIZE`
- `THREAD_POOL_QUEUE_CAPACITY`

### Server

- `PORT`
- `TOMCAT_MAX_THREADS`
- `TOMCAT_MIN_SPARE_THREADS`
- `TOMCAT_MAX_QUEUE_CAPACITY`

### Mail

- `MAIL_USERNAME`
- `MAIL_PASSWORD`

### Cloudinary

- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `CLOUDINARY_FOLDER`

## 2. Khoi dong database

Dam bao PostgreSQL dang chay va database trong `SPRING_DATASOURCE_URL` da san sang.

## 3. Chay ung dung

Windows:

```bash
gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

Build jar:

```bash
./gradlew build
```

## 4. Kiem tra nhanh

- Swagger UI: `http://localhost:<PORT>/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:<PORT>/v3/api-docs`

## 5. Luu y moi truong dev

- `spring.jpa.hibernate.ddl-auto=create-drop`: database se duoc tao va xoa lai theo vong doi app
- `spring.flyway.enabled=false`: migration chua duoc su dung
- Neu muon test upload file, can cau hinh Cloudinary dung
- Neu muon test forgot/reset password, can cau hinh mail dung
