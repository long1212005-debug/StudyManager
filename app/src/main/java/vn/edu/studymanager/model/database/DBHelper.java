package vn.edu.studymanager.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gpa.db";
    private static final int DB_VERSION = 4; // Tăng version để nạp dữ liệu mẫu mới

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createSinhVien = "CREATE TABLE SinhVien (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ho_ten TEXT, " +
                "mssv TEXT, " +
                "cpa_hien_tai REAL, " +
                "tong_tin_chi_tich_luy INTEGER, " +
                "muc_tieu_cpa REAL, " +
                "truong_dao_tao TEXT DEFAULT 'HaUI'" +
                ")";
        db.execSQL(createSinhVien);

        String createKyHoc = "CREATE TABLE KyHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sinh_vien_id INTEGER, " +
                "ten_ky TEXT, " +
                "gpa_ky REAL, " +
                "tong_tin_chi_ky INTEGER, " +
                "trang_thai INTEGER, " +
                "FOREIGN KEY(sinh_vien_id) REFERENCES SinhVien(id)" +
                ")";
        db.execSQL(createKyHoc);

        String createMonHoc = "CREATE TABLE MonHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ky_hoc_id INTEGER, " +
                "ten_mon TEXT, " +
                "so_tin_chi INTEGER, " +
                "diem_tx1 REAL, " +
                "diem_tx2 REAL, " +
                "diem_tx3 REAL, " +
                "diem_thi REAL, " +
                "diem_tong_ket_10 REAL, " +
                "diem_tong_ket_4 REAL, " +
                "diem_chu TEXT, " +
                "trang_thai TEXT, " +
                "UNIQUE(ten_mon, ky_hoc_id), " +
                "FOREIGN KEY(ky_hoc_id) REFERENCES KyHoc(id)" +
                ")";
        db.execSQL(createMonHoc);

        String createTrongSo = "CREATE TABLE CauHinhTrongSo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ten_he_dao_tao TEXT, " +
                "trong_so_tx1 REAL, " +
                "trong_so_tx2 REAL, " +
                "trong_so_tx3 REAL, " +
                "trong_so_thi REAL" +
                ")";
        db.execSQL(createTrongSo);

        seedData(db);
    }

    public void seedData(SQLiteDatabase db) {
        // Tạo sinh viên mặc định
        db.execSQL("INSERT INTO SinhVien (ho_ten, mssv, cpa_hien_tai, tong_tin_chi_tich_luy, muc_tieu_cpa, truong_dao_tao) " +
                "VALUES ('Sinh viên HaUI', '2023604407', 3.35, 15, 3.20, 'Đại học Công nghiệp Hà Nội')");

        // Tạo cấu hình trọng số HaUI chuẩn (20-20-20-40)
        db.execSQL("INSERT INTO CauHinhTrongSo (ten_he_dao_tao, trong_so_tx1, trong_so_tx2, trong_so_tx3, trong_so_thi) " +
                "VALUES ('chính quy', 0.2, 0.2, 0.2, 0.4)");

        // Tạo kỳ học mẫu
        db.execSQL("INSERT INTO KyHoc (sinh_vien_id, ten_ky, gpa_ky, tong_tin_chi_ky, trang_thai) " +
                "VALUES (1, 'Kỳ 1 (2024 - 2025)', 3.45, 9, 1)");
        db.execSQL("INSERT INTO KyHoc (sinh_vien_id, ten_ky, gpa_ky, tong_tin_chi_ky, trang_thai) " +
                "VALUES (1, 'Kỳ 2 (2024 - 2025)', 3.20, 6, 0)");

        // Môn học mẫu Kỳ 1 (Đã hoàn thành)
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (1, 'Lập trình C/C++', 3, 8.5, 9.0, 8.5, 8.5, 8.6, 4.0, 'A', 'Qua môn')");
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (1, 'Cơ sở dữ liệu', 3, 7.5, 8.0, 8.0, 7.0, 7.5, 3.0, 'B', 'Qua môn')");
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (1, 'Toán rời rạc', 3, 8.0, 8.5, 8.0, 8.0, 8.1, 3.5, 'B+', 'Qua môn')");

        // Môn học mẫu Kỳ 2 (Đang học - có điểm TX để dùng thử tính điểm cần thi)
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (2, 'Phát triển ứng dụng Android', 3, 8.5, 8.0, 9.0, 0, 0, 0, '', 'Đang học')");
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (2, 'Mạng máy tính', 3, 7.0, 7.5, 8.0, 7.5, 7.5, 3.0, 'B', 'Qua môn')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CauHinhTrongSo");
        db.execSQL("DROP TABLE IF EXISTS MonHoc");
        db.execSQL("DROP TABLE IF EXISTS KyHoc");
        db.execSQL("DROP TABLE IF EXISTS SinhVien");
        onCreate(db);
    }
}
