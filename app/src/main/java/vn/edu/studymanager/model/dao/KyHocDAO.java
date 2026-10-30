package vn.edu.studymanager.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import vn.edu.studymanager.model.database.DBHelper;
import vn.edu.studymanager.model.entity.KyHoc;

import java.util.ArrayList;
import java.util.List;

public class KyHocDAO {
    private DBHelper dbHelper;

    public KyHocDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // Lấy tất cả kỳ
    public List<KyHoc> getAllKyHoc(int sinhVienId) {
        List<KyHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM KyHoc WHERE sinh_vien_id = ? ORDER BY id ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sinhVienId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new KyHoc(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getFloat(3), cursor.getInt(4), cursor.getInt(5) == 1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Lấy danh sách kỳ hiển thị (bỏ kỳ kỹ thuật nếu có)
    public List<KyHoc> getDisplayKyHoc(int sinhVienId) {
        List<KyHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM KyHoc WHERE sinh_vien_id = ? AND ten_ky != 'Dữ liệu từ Website' ORDER BY id ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sinhVienId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new KyHoc(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getFloat(3), cursor.getInt(4), cursor.getInt(5) == 1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long insertKyHoc(KyHoc kyHoc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sinh_vien_id", kyHoc.getSinhVienId());
        values.put("ten_ky", kyHoc.getTenKy());
        values.put("gpa_ky", kyHoc.getGpaKy());
        values.put("tong_tin_chi_ky", kyHoc.getTongTinChiKy());
        values.put("trang_thai", kyHoc.isTrangThai() ? 1 : 0);
        return db.insert("KyHoc", null, values);
    }

    public boolean updateKyHoc(KyHoc kyHoc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ten_ky", kyHoc.getTenKy());
        values.put("gpa_ky", kyHoc.getGpaKy());
        values.put("tong_tin_chi_ky", kyHoc.getTongTinChiKy());
        values.put("trang_thai", kyHoc.isTrangThai() ? 1 : 0);
        return db.update("KyHoc", values, "id = ?", new String[]{String.valueOf(kyHoc.getId())}) > 0;
    }

    public boolean deleteKyHoc(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("MonHoc", "ky_hoc_id = ?", new String[]{String.valueOf(id)});
        return db.delete("KyHoc", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public KyHoc getKyHocById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT id, sinh_vien_id, ten_ky, gpa_ky, " +
                "tong_tin_chi_ky, trang_thai FROM KyHoc WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (!cursor.moveToFirst()) return null;
            return new KyHoc(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                    cursor.getFloat(3), cursor.getInt(4), cursor.getInt(5) == 1);
        }
    }

    /**
     * Tự động tính toán lại GPA của một kỳ học từ các môn học và lưu vào bảng KyHoc.
     * Quy tắc: GPA kỳ = Tổng (điểm hệ 4 * số tín chỉ) / Tổng số tín chỉ có điểm.
     */
    public void updateGpaKyHoc(int kyHocId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Tính tổng điểm hệ 4 và tổng tín chỉ đã có điểm
        Cursor cursor = db.rawQuery(
                "SELECT SUM(diem_tong_ket_4 * so_tin_chi), SUM(so_tin_chi) FROM MonHoc " +
                        "WHERE ky_hoc_id = ? AND diem_chu IS NOT NULL AND diem_chu != ''",
                new String[]{String.valueOf(kyHocId)});

        float tongDiem = 0.0f;
        int tinChiCoDiem = 0;
        if (cursor.moveToFirst() && cursor.getInt(1) > 0) {
            tongDiem = cursor.getFloat(0);
            tinChiCoDiem = cursor.getInt(1);
        }
        cursor.close();

        float gpa = (tinChiCoDiem > 0) ? (float) (Math.round((tongDiem / tinChiCoDiem) * 100.0) / 100.0) : 0.0f;

        // 2. Tính tổng tín chỉ đăng ký của toàn kỳ
        Cursor cursorTotal = db.rawQuery(
                "SELECT SUM(so_tin_chi) FROM MonHoc WHERE ky_hoc_id = ?",
                new String[]{String.valueOf(kyHocId)});
        int tongTinChiKy = 0;
        if (cursorTotal.moveToFirst()) {
            tongTinChiKy = cursorTotal.getInt(0);
        }
        cursorTotal.close();

        // 3. Cập nhật lại vào bảng KyHoc
        ContentValues values = new ContentValues();
        values.put("gpa_ky", gpa);
        values.put("tong_tin_chi_ky", tongTinChiKy);
        db.update("KyHoc", values, "id = ?", new String[]{String.valueOf(kyHocId)});
    }
}
