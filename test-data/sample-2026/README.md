# StudyManager – Dữ liệu mẫu phục vụ kiểm thử

**Nguồn đối chiếu:** Repository `long1212005-debug/StudyManager`, nhánh `main` ngày 01/11/2026; file `model/database/DBHelper.java`, cơ sở dữ liệu `gpa.db` phiên bản 3.

## Nội dung
- `StudyManager_TEST.db`: SQLite độc lập với 3 sinh viên giả lập, 5 học kỳ, 17 môn học và 2 cấu hình trọng số.
- `00_Tao_CSDL_test.sql`: tạo 4 bảng khớp với `DBHelper.java`.
- `01_Du_lieu_mau_INSERT.sql`: 27 lệnh INSERT dùng khi tạo CSDL kiểm thử mới.
- `*.csv`: bản xem nhanh theo bảng; `02_Ket_qua_kiem_tra.txt`: kiểm thử SQLite độc lập.

## Cách sử dụng an toàn
- **Không chạy** `01_Du_lieu_mau_INSERT.sql` nhiều lần trên cùng một database: ID cố định có thể gây lỗi khóa chính.
- **Không chép đè** `gpa.db` của ứng dụng Android đang dùng: database thực tế có thể chứa dữ liệu người dùng.
- Có thể mở `StudyManager_TEST.db` với DB Browser for SQLite để xem và chụp minh chứng.
- Có thể tạo database mới bằng cách chạy `00_Tao_CSDL_test.sql`, sau đó `01_Du_lieu_mau_INSERT.sql`.
- `DBHelper.onCreate()` có thêm 1 sinh viên mặc định và 1 cấu hình: các số lượng trong file SQLite mẫu **không tính** 2 bản ghi này.
- `cpa_hien_tai`, `gpa_ky` và điểm tổng kết là dữ liệu minh họa đã nhập sẵn, **không phải kết quả đã xác minh bằng logic tính điểm của ứng dụng**.
- Kiểm thử SQLite độc lập không đồng nghĩa đã kiểm thử giao diện Android hoặc DAO.

## Thông tin đóng góp
Đây là dữ liệu mẫu chuẩn bị để hỗ trợ công việc được giao cho Trường Nguyên; người trực tiếp tạo/push trên Git có thể là thành viên hỗ trợ. Cần ghi nhận đóng góp đúng thực tế trong Pull Request.
