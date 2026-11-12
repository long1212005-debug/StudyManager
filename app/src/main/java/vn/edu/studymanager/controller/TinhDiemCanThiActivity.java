package vn.edu.studymanager.controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import vn.edu.studymanager.R;
import vn.edu.studymanager.model.calculator.DiemThiCalculator;
import vn.edu.studymanager.model.dao.CauHinhTrongSoDAO;
import vn.edu.studymanager.model.entity.CauHinhTrongSo;

/**
 * Công cụ tính điểm cần thi sử dụng thuật toán đã kiểm thử ở tuần 4.
 * Không tự suy diễn các ngưỡng điểm chữ/điều kiện qua môn của HaUI.
 */
public class TinhDiemCanThiActivity extends AppCompatActivity {
    private EditText edtTx1, edtTx2, edtTx3, edtMucTieu;
    private TextView tvTrongSo, tvKetQua;
    private CauHinhTrongSo cauHinh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinh_diem_can_thi);

        edtTx1 = findViewById(R.id.edtTx1);
        edtTx2 = findViewById(R.id.edtTx2);
        edtTx3 = findViewById(R.id.edtTx3);
        edtMucTieu = findViewById(R.id.edtMucTieu);
        tvTrongSo = findViewById(R.id.tvTrongSo);
        tvKetQua = findViewById(R.id.tvKetQua);
        Button btnTinh = findViewById(R.id.btnTinhDiem);
        Button btnBack = findViewById(R.id.btnQuayLai);

        cauHinh = new CauHinhTrongSoDAO(this).getCauHinh();
        if (cauHinh == null) {
            tvTrongSo.setText("Chưa có cấu hình trọng số trong SQLite.");
            tvKetQua.setText("Không thể tính khi thiếu cấu hình. Hãy kiểm tra cơ sở dữ liệu.");
            btnTinh.setEnabled(false);
        } else {
            tvTrongSo.setText(String.format(Locale.getDefault(),
                    "Trọng số từ SQLite: TX1 %.0f%% · TX2 %.0f%% · TX3 %.0f%% · Thi %.0f%%",
                    cauHinh.getTrongSoTx1() * 100,
                    cauHinh.getTrongSoTx2() * 100,
                    cauHinh.getTrongSoTx3() * 100,
                    cauHinh.getTrongSoThi() * 100));
        }
        btnTinh.setOnClickListener(v -> tinhDiem());
        btnBack.setOnClickListener(v -> finish());
    }

    private double parseScore(EditText field, String name) {
        String value = field.getText().toString().trim().replace(',', '.');
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập " + name);
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " phải là số hợp lệ");
        }
    }

    private void tinhDiem() {
        if (cauHinh == null) return;
        try {
            double tx1 = parseScore(edtTx1, "TX1");
            double tx2 = parseScore(edtTx2, "TX2");
            String tx3Text = edtTx3.getText().toString().trim();
            Double tx3 = tx3Text.isEmpty() ? null : parseScore(edtTx3, "TX3");
            double mucTieu = parseScore(edtMucTieu, "điểm tổng kết mục tiêu");
            double canThi = DiemThiCalculator.tinhDiemThiCanDat(
                    tx1, tx2, tx3, mucTieu,
                    cauHinh.getTrongSoTx1(), cauHinh.getTrongSoTx2(),
                    cauHinh.getTrongSoTx3(), cauHinh.getTrongSoThi());

            if (canThi > 10.0000001) {
                tvKetQua.setText(String.format(Locale.getDefault(),
                        "Không thể đạt mục tiêu: cần khoảng %.2f điểm thi (vượt 10).", canThi));
            } else if (canThi <= 0.0000001) {
                tvKetQua.setText("Với điểm thành phần hiện tại, không cần thêm điểm thi để đạt mục tiêu đã nhập.");
            } else {
                // Làm tròn lên 0,01 để không trả về mức thấp hơn điểm thực tế cần đạt.
                double displayed = Math.ceil((canThi - 1e-8) * 100.0) / 100.0;
                tvKetQua.setText(String.format(Locale.getDefault(),
                        "Điểm thi cần đạt ít nhất: %.2f / 10 (theo trọng số hiện tại).", displayed));
            }
        } catch (IllegalArgumentException e) {
            tvKetQua.setText("Dữ liệu chưa hợp lệ: " + e.getMessage());
        }
    }
}
