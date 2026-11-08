# Week 4 – Bùi Huy Hào – Rà soát logic điểm và kiểm thử

Phạm vi: 02–08/11/2026, repository StudyManager, nhánh main được khảo sát ngày 05/11/2026.

## Vấn đề tìm thấy
1. `MonHocDAO.syncGlobalStats()` gộp tất cả sinh viên, gây lẫn CPA nếu có nhiều hồ sơ.
2. Hàm cũ cập nhật cố định `SinhVien.id = 1`, không hỗ trợ ID khác.
3. Hàm cũ tính tín chỉ tích lũy bằng tổng tín chỉ kể cả môn F (điểm 0).
4. Logic tính điểm cần lớp độc lập Android để kiểm thử; `TinhDiemCanThiActivity` hiện là lớp trống trên main.
5. `DBHelper.onUpgrade()` xóa dữ liệu: ghi nhận rủi ro nhưng chưa sửa vì cần thiết kế migration.

## Các thay đổi
- `DiemThiCalculator.java`: tính tổng kết và điểm thi cần đạt dựa trên trọng số nhập vào; kiểm tra điểm 0–10, trọng số tổng 1, dữ liệu TX3 tùy chọn.
- `DiemThiCalculatorTest.java`: 14 unit test JUnit4.
- `MonHocDAO.java`: sửa `syncGlobalStats(int sinhVienId)` lọc theo sinh viên, nhóm lần học lại theo tên môn, không tính F vào tín chỉ đã đạt; giữ hàm không tham số để tương thích sinh viên mặc định ID=1.
- `test-data/week4/audit_sqlite_week4.py`: 6 kiểm tra SQLite độc lập, không thao tác CSDL người dùng.

## Kết quả xác nhận ở môi trường soạn thảo
- Compile lớp calculator bằng OpenJDK 21: PASS.
- Thực thi 14 phương thức test bằng Java runner tương thích chú giải JUnit: 14/14 PASS.
- Kiểm tra SQLite độc lập Python 3: PASS 6 hạng mục.
- **Chưa chạy Android Studio/Gradle/Emulator tại máy người dùng; chưa xác nhận giao diện hoặc tích hợp app.**

## Bước cần làm tại máy người dùng
1. Android Studio > Terminal: `./gradlew.bat testDebugUnitTest` (PowerShell: `.\gradlew.bat testDebugUnitTest`).
2. `py -3 test-data/week4/audit_sqlite_week4.py`.
3. Kiểm tra `git diff`, build app, kiểm thử trên emulator với dữ liệu được tạo bởi DBHelper.
4. Push nhánh mới và tạo Pull Request để nhóm review; chỉ merge sau khi test được xác nhận.

## Cần thống nhất với nhóm trước khi sử dụng trong sản phẩm
- Hệ số trọng số phải theo cấu hình thực tế; dữ liệu seed hiện là 20/20/20/40, **không xác nhận đây là quy chế chính thức của HaUI**.
- Quy tắc điểm chữ A/B/... và chính sách GPA/CPA học lại, điểm F, môn không tính GPA phải đối chiếu quy chế đào tạo.
- `MonHoc` chưa có mã môn để nhận diện các lần học lại, tạm dùng tên môn chuẩn hóa.
- `syncGlobalStats()` cũ mặc định SV ID=1; khi dùng nhiều hồ sơ phải gọi `syncGlobalStats(sinhVienId)` đúng ID.
