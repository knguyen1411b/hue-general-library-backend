# Business Flow

## 1. Dang ky tai khoan

1. Client gui `POST /api/v1/auth/sign-up` duoi dang `multipart/form-data`
2. He thong validate:
   - username
   - email
   - phone
   - CCCD/CMND
   - file anh dinh kem
3. Tao `User`, ma hoa mat khau
4. Luu user vao DB de sinh `id`
5. Upload avatar va anh giay to len Cloudinary
6. Cap nhat URL file vao user va luu lai

## 2. Dang nhap

1. Client gui `username` va `password`
2. He thong tim user theo username
3. So khop mat khau da ma hoa
4. Kiem tra trang thai tai khoan:
   - `ACTIVE`: cho phep dang nhap
   - `INACTIVE`, `LOCKED`, `DELETED`: tu choi
5. Tao `accessToken`
6. Tao `refreshToken` ngau nhien, hash token roi luu vao DB
7. Tra ve cap token cho client

## 3. Refresh token

1. Client gui `refreshToken`
2. He thong hash token dau vao
3. Tim user theo `refreshTokenHash`
4. Kiem tra:
   - token co ton tai hay khong
   - tai khoan con hop le hay khong
   - token da het han hay chua
5. Neu hop le:
   - tao access token moi
   - rotate refresh token moi
6. Neu khong hop le:
   - tra ve loi `401`

## 4. Dang xuat

1. Client gui `DELETE /api/v1/auth/sign-out` kem access token
2. He thong xoa `refreshTokenHash` va `refreshTokenExpiredAt`
3. Cac refresh token cu se khong con su dung duoc

## 5. Quen mat khau

1. Client gui email qua `POST /api/v1/auth/forgot-password`
2. Neu email khong ton tai:
   - he thong khong throw loi
   - van tra ve thanh cong de tranh lo thong tin tai khoan
3. Neu email ton tai:
   - tao reset token ngau nhien
   - hash token va luu vao DB kem thoi gian het han
   - gui email HTML chua link reset

## 6. Dat lai mat khau

1. Client gui `token` va `newPassword`
2. He thong hash token dau vao
3. Tim user theo `resetPasswordTokenHash`
4. Kiem tra token:
   - khong ton tai: loi `401`
   - het han: loi `401`, dong thoi xoa reset token trong DB
5. Neu hop le:
   - ma hoa mat khau moi
   - set `passwordChanged = true`
   - xoa reset token
   - xoa refresh token cu de buoc dang nhap lai

## 7. Cap nhat thong tin ca nhan

1. User da dang nhap gui `PATCH /api/v1/users/me`
2. He thong cap nhat theo kieu patch:
   - chi truong nao co du lieu moi cap nhat
   - truong nao bo trong thi giu nguyen
3. Neu cap nhat email/phone:
   - kiem tra trung truoc khi luu
4. Neu doi avatar:
   - upload file moi len Cloudinary

## 8. Quan tri nguoi dung

Chi `ADMIN` hoac `MANAGER` moi duoc:

- xem danh sach user
- xem chi tiet user
- tao user
- cap nhat user
- xoa mem user

Xoa user hien tai la xoa mem bang cach set `status = DELETED`.
