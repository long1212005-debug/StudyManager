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

    public MonHoc getMonHocById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MonHoc WHERE id = ?", new String[]{String.valueOf(id)});
        MonHoc mh = null;
        if (cursor.moveToFirst()) {
            mh = cursorToMonHoc(cursor);
        }
        cursor.close();
        return mh;
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

    public boolean deleteMonHoc(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("MonHoc", "id = ?", new String[]{String.valueOf(id)}) > 0;
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
     * - Môn điểm F (0.0) chưa học lại sẽ tính là 0 (vẫn tính vào mẫu số chia CPA, nhưng không tính vào tín chỉ tích lũy).
     */
    public void syncGlobalStats() {
        syncGlobalStats(1);
    }

    public void syncGlobalStats(int sinhVienId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Truy vấn con: Nhóm theo tên môn và lấy MAX điểm hệ 4 thuộc các kỳ của sinh viên này
        String subQuery = "SELECT mh.ten_mon, MAX(mh.diem_tong_ket_4) as max_diem4, MAX(mh.so_tin_chi) as tin_chi " +
                          "FROM MonHoc mh " +
                          "INNER JOIN KyHoc kh ON mh.ky_hoc_id = kh.id " +
                          "WHERE kh.sinh_vien_id = " + sinhVienId + " AND mh.diem_chu IS NOT NULL AND mh.diem_chu != '' " +
                          "GROUP BY mh.ten_mon";

        Cursor cursor = db.rawQuery(
                "SELECT SUM(max_diem4 * tin_chi), SUM(tin_chi), SUM(CASE WHEN max_diem4 > 0 THEN tin_chi ELSE 0 END) FROM (" + subQuery + ")",
                null);

        float tongDiemTichLuy = 0;
        int tongTinChiChiaCpa = 0;
        int tongTinChiTichLuy = 0;

        if (cursor.moveToFirst() && cursor.getInt(1) > 0) {
            tongDiemTichLuy = cursor.getFloat(0);
            tongTinChiChiaCpa = cursor.getInt(1);
            tongTinChiTichLuy = cursor.getInt(2);
        }
        cursor.close();

        float cpa = tongTinChiChiaCpa > 0 ? (float) (Math.round((tongDiemTichLuy / tongTinChiChiaCpa) * 100.0) / 100.0) : 0;

        ContentValues values = new ContentValues();
        values.put("cpa_hien_tai", cpa);
        values.put("tong_tin_chi_tich_luy", tongTinChiTichLuy);

        db.update("SinhVien", values, "id = ?", new String[]{String.valueOf(sinhVienId)});
    }
}
