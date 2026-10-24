package vn.edu.studymanager.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import vn.edu.studymanager.model.database.DBHelper;
import vn.edu.studymanager.model.entity.SinhVien;

public class SinhVienDAO {
    private DBHelper dbHelper;

    public SinhVienDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // Lấy thông tin sinh viên (Lấy người đầu tiên tìm thấy)
    public SinhVien getSinhVien() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SinhVien sv = null;
        Cursor cursor = db.rawQuery("SELECT * FROM SinhVien LIMIT 1", null);

        if (cursor.moveToFirst()) {
            sv = new SinhVien(
                    cursor.getInt(0), // id
                    cursor.getString(1), // ho_ten
                    cursor.getString(2), // mssv
                    cursor.getFloat(3),  // cpa_hien_tai
                    cursor.getInt(4),    // tong_tin_chi_tich_luy
                    cursor.getFloat(5),  // muc_tieu_cpa
                    cursor.getString(6)  // truong_dao_tao
            );
        }
        cursor.close();
        return sv;
    }

    // Cập nhật thông tin sinh viên
    public boolean updateSinhVien(SinhVien sv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ho_ten", sv.getHoTen());
        values.put("mssv", sv.getMssv());
        values.put("cpa_hien_tai", sv.getCpaHienTai());
        values.put("tong_tin_chi_tich_luy", sv.getTongTinChiTichLuy());
        values.put("muc_tieu_cpa", sv.getMucTieuCpa());
        // Không update trường đào tạo nếu không cần thiết

        int rows = db.update("SinhVien", values, "id = ?", new String[]{String.valueOf(sv.getId())});
        return rows > 0;
    }

    public long insertSinhVien(SinhVien sv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ho_ten", sv.getHoTen());
        values.put("mssv", sv.getMssv());
        values.put("cpa_hien_tai", sv.getCpaHienTai());
        values.put("tong_tin_chi_tich_luy", sv.getTongTinChiTichLuy());
        values.put("muc_tieu_cpa", sv.getMucTieuCpa());
        values.put("truong_dao_tao", sv.getTruongDaoTao());
        return db.insert("SinhVien", null, values);
    }

    public SinhVien getSinhVienById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT id, ho_ten, mssv, cpa_hien_tai, " +
                "tong_tin_chi_tich_luy, muc_tieu_cpa, truong_dao_tao FROM SinhVien WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (!cursor.moveToFirst()) return null;
            return new SinhVien(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getFloat(3), cursor.getInt(4), cursor.getFloat(5), cursor.getString(6));
        }
    }

    public boolean deleteSinhVien(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("SinhVien", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}