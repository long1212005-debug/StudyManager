package vn.edu.studymanager.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import vn.edu.studymanager.model.database.DBHelper;
import vn.edu.studymanager.model.entity.MonHoc;

import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {
    private DBHelper dbHelper;

    public MonHocDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<MonHoc> getAllMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MonHoc", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToMonHoc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<MonHoc> getMonHocByKy(int kyHocId) {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM MonHoc WHERE ky_hoc_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(kyHocId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToMonHoc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private MonHoc cursorToMonHoc(Cursor cursor) {
        Float diemTx3 = null;
        int tx3Index = cursor.getColumnIndex("diem_tx3");
        if (!cursor.isNull(tx3Index)) {
            diemTx3 = cursor.getFloat(tx3Index);
        }
        return new MonHoc(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getFloat(4),
                cursor.getFloat(5),
                diemTx3,
                cursor.getFloat(7),
                cursor.getFloat(8),
                cursor.getFloat(9),
                cursor.getString(10),
                cursor.getString(11)
        );
    }

    public long insertMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMonHocContentValues(mh);
        return db.insert("MonHoc", null, values);
    }

    public boolean updateMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMonHocContentValues(mh);
        return db.update("MonHoc", values, "id = ?", new String[]{String.valueOf(mh.getId())}) > 0;
    }

    public boolean deleteMonHocByKyHocId(int kyHocId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("MonHoc", "ky_hoc_id = ?", new String[]{String.valueOf(kyHocId)}) > 0;
    }

    private ContentValues getMonHocContentValues(MonHoc mh) {
        ContentValues values = new ContentValues();
        values.put("ky_hoc_id", mh.getKyHocId());
        values.put("ten_mon", mh.getTenMon());
        values.put("so_tin_chi", mh.getSoTinChi());
        values.put("diem_tx1", mh.getDiemTx1());
        values.put("diem_tx2", mh.getDiemTx2());
        if (mh.getDiemTx3() != null) values.put("diem_tx3", mh.getDiemTx3());
        else values.putNull("diem_tx3");
        values.put("diem_thi", mh.getDiemThi());
        values.put("diem_tong_ket_10", mh.getDiemTongKet10());
        values.put("diem_tong_ket_4", mh.getDiemTongKet4());
        values.put("diem_chu", mh.getDiemChu());
        values.put("trang_thai", mh.getTrangThai());
        return values;
    }

    /**
     * Đồng bộ CPA và Tín chỉ tích lũy theo quy tắc:
     * - Nếu có nhiều lần học (trùng tên môn), chỉ lấy điểm hệ 4 CAO NHẤT.
     * - Môn điểm F (0.0) chưa học lại sẽ tính là 0.
     */
    /**
     * Backward compatibility for the application's default student (id = 1).
     * For a different student, call syncGlobalStats(sinhVienId).
     */
    public void syncGlobalStats() {
        syncGlobalStats(1);
    }

    /**
     * Calculate CPA for one student only. For repeated course names, use the
     * highest grade-4 attempt; failing courses count toward the CPA divisor
     * but are not counted as earned credits.
     * NOTE: Grouping by name follows the pre-existing rule. A course code
     * should replace names as the stable identifier in a future migration.
     */
    public void syncGlobalStats(int sinhVienId) {
        if (sinhVienId <= 0) {
            throw new IllegalArgumentException("sinhVienId phai lon hon 0");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT COALESCE(SUM(best_score * credits), 0), " +
                "COALESCE(SUM(credits), 0), " +
                "COALESCE(SUM(CASE WHEN best_score > 0 THEN credits ELSE 0 END), 0) " +
                "FROM (SELECT LOWER(TRIM(m.ten_mon)) AS subject_key, " +
                "MAX(m.diem_tong_ket_4) AS best_score, " +
                "MAX(m.so_tin_chi) AS credits " +
                "FROM MonHoc m JOIN KyHoc k ON k.id = m.ky_hoc_id " +
                "WHERE k.sinh_vien_id = ? " +
                "AND m.diem_chu IS NOT NULL AND TRIM(m.diem_chu) != '' " +
                "AND m.diem_tong_ket_4 BETWEEN 0 AND 4 " +
                "AND m.so_tin_chi > 0 " +
                "GROUP BY LOWER(TRIM(m.ten_mon)))";

        double totalWeighted = 0;
        int attemptedCredits = 0;
        int earnedCredits = 0;
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sinhVienId)})) {
            if (cursor.moveToFirst()) {
                totalWeighted = cursor.getDouble(0);
                attemptedCredits = cursor.getInt(1);
                earnedCredits = cursor.getInt(2);
            }
        }
        double cpa = attemptedCredits > 0 ? totalWeighted / attemptedCredits : 0;
        ContentValues values = new ContentValues();
        values.put("cpa_hien_tai", cpa);
        values.put("tong_tin_chi_tich_luy", earnedCredits);
        db.update("SinhVien", values, "id = ?", new String[]{String.valueOf(sinhVienId)});
    }
}