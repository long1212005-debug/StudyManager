# Tuần 5 – Kiểm thử, hoàn thiện và bàn giao (09–15/11/2026)

Phân công theo Google Docs nhóm: **Bùi Huy Hào** – chạy test case, ghi kết quả/lỗi còn lại; viết phần triển khai/kiểm thử.

## Phần bổ sung trong bộ patch

- Hoàn thiện `TinhDiemCanThiActivity` từ lớp rỗng thành màn hình nhập TX1/TX2/TX3/mục tiêu, đọc trọng số từ SQLite; gọi trực tiếp `DiemThiCalculator` có sẵn ở tuần 4.
- Thêm layout công cụ, khai báo Manifest và nối nút `Công cụ` ở Dashboard.
- Thêm 8 JUnit regression tests vào `DiemThiCalculatorAcceptanceTest`.
- Thêm 18 kịch bản nghiệm thu, tất cả được đánh dấu **Chưa chạy** cho đến khi kiểm tra thực tế.
- Thêm script kiểm tra tĩnh 6 thành phần của bản tích hợp.

## Hướng dẫn

1. Tại root dự án: `git status`; lưu hoặc commit thay đổi hiện có trước khi chuyển nhánh.
2. `git switch main && git pull origin main && git switch -c feature/hao-week5-acceptance`
3. Giải nén ZIP và chạy: `py -3 "C:\\Users\\huyha\\Downloads\\HaoWeek5\\Apply-Week5-Hao.py"` trong root dự án.
4. `./gradlew.bat testDebugUnitTest assembleDebug` (trên Windows PowerShell dùng `.\\gradlew.bat`).
5. `py -3 .\\test-data\\week5\\audit_project_week5.py`
6. Mở Android Emulator/thiết bị, thực hiện thủ công các test case trong CSV, điền trạng thái + ảnh minh chứng.
7. `git status`, `git diff --check`; chỉ `git add` các file của tuần 5; bản sao lưu MainActivity/Manifest nằm trong thư mục tạm, ngoài repository.
8. Tạo PR sang `main` sau khi pass kiểm thử và review các lỗi còn lại.

## Vấn đề cần nhóm xử lý trước khi giao bản cuối

- Dashboard `MainActivity.refreshDisplay()` tự tính CPA từ kỳ học, khác nguồn dữ liệu CPA/tín chỉ tích lũy trong `SinhVien` do `MonHocDAO.syncGlobalStats` cập nhật; cần đối chiếu nghiệp vụ, không tự kết luận đã đồng nhất.
- WebView sync `KyHocDAO.deleteDisplayKyHoc` xóa kỳ trước, nhưng DB bật khóa ngoại và có thể còn môn con; cần kiểm tra sync lặp và sửa trong PR do người phụ trách cùng xác nhận.
- `ThongKe.java` và `LoTrinhHocTapActivity.java` cần kiểm tra mức hoàn thiện với người được phân công; không đánh dấu PASS nếu chưa chạy được.
- `DBHelper.onUpgrade()` đang DROP bảng; tuyệt đối không tăng DB_VERSION chỉ để thử nghiệm trên dữ liệu thật.

## Trạng thái kiểm thử

Đã có log tuần 4 trên máy người thực hiện: Gradle BUILD SUCCESSFUL; SQLite 6/6 PASS. **Chưa có log Gradle/Emulator của phần tích hợp tuần 5** tại thời điểm đóng gói; vui lòng chạy và ghi kết quả thực tế.
