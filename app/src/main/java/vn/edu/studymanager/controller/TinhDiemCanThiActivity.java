package vn.edu.studymanager.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import vn.edu.studymanager.R;
import vn.edu.studymanager.adapters.MucTieuAdapter;
import vn.edu.studymanager.model.dao.CauHinhTrongSoDAO;
import vn.edu.studymanager.model.dao.KyHocDAO;
import vn.edu.studymanager.model.dao.MonHocDAO;
import vn.edu.studymanager.model.entity.CauHinhTrongSo;
import vn.edu.studymanager.model.entity.KetQuaTinhDiem;
import vn.edu.studymanager.model.entity.KyHoc;
import vn.edu.studymanager.model.entity.MonHoc;
import vn.edu.studymanager.service.DiemThiCalculator;
import vn.edu.studymanager.service.ValidationResult;

public class TinhDiemCanThiActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // Nhóm 1: Môn học & Kỳ học
    private Spinner spnKyHoc, spnMonHoc;
    private TextView tvEmptyMonHoc, tvSoTinChiInfo;
    private TextInputLayout tilTenMon;
    private TextInputEditText edtTenMon;

    // Nhóm 2: Điểm thành phần
    private CheckBox chkCoTx3;
    private TextInputLayout tilTx1, tilTx2, tilTx3;
    private TextInputEditText edtTx1, edtTx2, edtTx3;

    // Nhóm 3: Cấu hình trọng số
    private TextView tvTongTrongSoStatus;
    private TextInputLayout tilWTx1, tilWTx2, tilWTx3, tilWThi;
    private TextInputEditText edtWTx1, edtWTx2, edtWTx3, edtWThi;
    private Button btnDefaultTrongSo, btnSaveTrongSo;

    // Nhóm 4: Mục tiêu điểm số
    private Spinner spnMucTieuChu;
    private TextInputLayout tilMucTieu10;
    private TextInputEditText edtMucTieu10;

    // Nút hành động chính
    private Button btnTinhDiem;

    // Khối kết quả & Trạng thái rỗng
    private View cardEmptyState, cardKetQua, cardBangMucTieu;
    private TextView tvDiemQuaTrinh, tvDiemThiCanDat, tvKetQuaIcon, tvTrangThaiBadge, tvLoiKhuyen, tvDiemToiDa;
    private LinearLayout layoutKetQuaHeader;
    private Button btnSavePredictedToDB;

    // Bảng danh sách mục tiêu
    private RecyclerView rvBangMucTieu;
    private MucTieuAdapter mucTieuAdapter;

    // DAOs
    private KyHocDAO kyHocDAO;
    private MonHocDAO monHocDAO;
    private CauHinhTrongSoDAO cauHinhDAO;

    // Dữ liệu nội bộ
    private List<KyHoc> danhSachKyHoc = new ArrayList<>();
    private List<MonHoc> danhSachMonHoc = new ArrayList<>();
    private MonHoc currentSelectedMonHoc = null;
    private KetQuaTinhDiem currentKetQua = null;

    private boolean isUpdatingFromCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinh_diem_can_thi);

        initViews();
        setupToolbar();
        initDAOs();
        setupKyHocAndMonHocSpinners();
        setupTrongSoRealtimeValidation();
        setupMucTieuSpinner();
        setupActionListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        spnKyHoc = findViewById(R.id.spnKyHoc);
        spnMonHoc = findViewById(R.id.spnMonHoc);
        tvEmptyMonHoc = findViewById(R.id.tvEmptyMonHoc);
        tvSoTinChiInfo = findViewById(R.id.tvSoTinChiInfo);
        tilTenMon = findViewById(R.id.tilTenMon);
        edtTenMon = findViewById(R.id.edtTenMon);

        chkCoTx3 = findViewById(R.id.chkCoTx3);
        tilTx1 = findViewById(R.id.tilTx1);
        edtTx1 = findViewById(R.id.edtTx1);
        tilTx2 = findViewById(R.id.tilTx2);
        edtTx2 = findViewById(R.id.edtTx2);
        tilTx3 = findViewById(R.id.tilTx3);
        edtTx3 = findViewById(R.id.edtTx3);

        tvTongTrongSoStatus = findViewById(R.id.tvTongTrongSoStatus);
        tilWTx1 = findViewById(R.id.tilWTx1);
        edtWTx1 = findViewById(R.id.edtWTx1);
        tilWTx2 = findViewById(R.id.tilWTx2);
        edtWTx2 = findViewById(R.id.edtWTx2);
        tilWTx3 = findViewById(R.id.tilWTx3);
        edtWTx3 = findViewById(R.id.edtWTx3);
        tilWThi = findViewById(R.id.tilWThi);
        edtWThi = findViewById(R.id.edtWThi);
        btnDefaultTrongSo = findViewById(R.id.btnDefaultTrongSo);
        btnSaveTrongSo = findViewById(R.id.btnSaveTrongSo);

        spnMucTieuChu = findViewById(R.id.spnMucTieuChu);
        tilMucTieu10 = findViewById(R.id.tilMucTieu10);
        edtMucTieu10 = findViewById(R.id.edtMucTieu10);

        btnTinhDiem = findViewById(R.id.btnTinhDiem);

        cardEmptyState = findViewById(R.id.cardEmptyState);
        cardKetQua = findViewById(R.id.cardKetQua);
        cardBangMucTieu = findViewById(R.id.cardBangMucTieu);
        tvDiemQuaTrinh = findViewById(R.id.tvDiemQuaTrinh);
        tvDiemThiCanDat = findViewById(R.id.tvDiemThiCanDat);
        tvKetQuaIcon = findViewById(R.id.tvKetQuaIcon);
        tvTrangThaiBadge = findViewById(R.id.tvTrangThaiBadge);
        tvLoiKhuyen = findViewById(R.id.tvLoiKhuyen);
        tvDiemToiDa = findViewById(R.id.tvDiemToiDa);
        layoutKetQuaHeader = findViewById(R.id.layoutKetQuaHeader);
        btnSavePredictedToDB = findViewById(R.id.btnSavePredictedToDB);

        rvBangMucTieu = findViewById(R.id.rvBangMucTieu);
        rvBangMucTieu.setLayoutManager(new LinearLayoutManager(this));
        mucTieuAdapter = new MucTieuAdapter(this, new ArrayList<>());
        rvBangMucTieu.setAdapter(mucTieuAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initDAOs() {
        kyHocDAO = new KyHocDAO(this);
        monHocDAO = new MonHocDAO(this);
        cauHinhDAO = new CauHinhTrongSoDAO(this);
    }

    private void setupKyHocAndMonHocSpinners() {
        List<String> kyHocNames = new ArrayList<>();
        kyHocNames.add("-- Tất cả kỳ học --");

        try {
            danhSachKyHoc = kyHocDAO.getAllKyHoc(1);
            if (danhSachKyHoc != null) {
                for (KyHoc kh : danhSachKyHoc) {
                    kyHocNames.add(kh.getTenKy());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> kyHocAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kyHocNames);
        spnKyHoc.setAdapter(kyHocAdapter);

        spnKyHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                capNhatDanhSachMonHocTheoKy(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void capNhatDanhSachMonHocTheoKy(int kyHocSpinnerPos) {
        try {
            if (kyHocSpinnerPos == 0) {
                danhSachMonHoc = monHocDAO.getAllMonHoc();
            } else if (kyHocSpinnerPos - 1 < danhSachKyHoc.size()) {
                int kyId = danhSachKyHoc.get(kyHocSpinnerPos - 1).getId();
                danhSachMonHoc = monHocDAO.getMonHocByKy(kyId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            danhSachMonHoc = new ArrayList<>();
        }

        List<String> monHocNames = new ArrayList<>();
        monHocNames.add(getString(R.string.tu_nhap_mon));

        if (danhSachMonHoc != null && !danhSachMonHoc.isEmpty()) {
            tvEmptyMonHoc.setVisibility(View.GONE);
            for (MonHoc mh : danhSachMonHoc) {
                monHocNames.add(mh.getTenMon() + " (" + mh.getSoTinChi() + " tín chỉ)");
            }
        } else {
            // UI 8: Trạng thái rỗng
            if (kyHocSpinnerPos > 0) {
                tvEmptyMonHoc.setVisibility(View.VISIBLE);
            } else {
                tvEmptyMonHoc.setVisibility(View.GONE);
            }
        }

        ArrayAdapter<String> monHocAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monHocNames);
        spnMonHoc.setAdapter(monHocAdapter);

        spnMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentSelectedMonHoc = null;
                    edtTenMon.setEnabled(true);
                    edtTenMon.setText("");
                    tvSoTinChiInfo.setText("");
                } else if (position - 1 < danhSachMonHoc.size()) {
                    currentSelectedMonHoc = danhSachMonHoc.get(position - 1);
                    edtTenMon.setText(currentSelectedMonHoc.getTenMon());
                    edtTenMon.setEnabled(false);

                    String info = "Tín chỉ: " + currentSelectedMonHoc.getSoTinChi();
                    if (currentSelectedMonHoc.getTrangThai() != null && !currentSelectedMonHoc.getTrangThai().isEmpty()) {
                        info += " | Trạng thái: " + currentSelectedMonHoc.getTrangThai();
                    }
                    tvSoTinChiInfo.setText(info);

                    // Tự động nạp điểm thành phần đã có (UX 3)
                    if (currentSelectedMonHoc.getDiemTx1() > 0) {
                        edtTx1.setText(String.valueOf(currentSelectedMonHoc.getDiemTx1()));
                    } else {
                        edtTx1.setText("");
                    }

                    if (currentSelectedMonHoc.getDiemTx2() > 0) {
                        edtTx2.setText(String.valueOf(currentSelectedMonHoc.getDiemTx2()));
                    } else {
                        edtTx2.setText("");
                    }

                    if (currentSelectedMonHoc.getDiemTx3() != null && currentSelectedMonHoc.getDiemTx3() > 0) {
                        chkCoTx3.setChecked(true);
                        edtTx3.setText(String.valueOf(currentSelectedMonHoc.getDiemTx3()));
                    } else {
                        chkCoTx3.setChecked(false);
                        edtTx3.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupTrongSoRealtimeValidation() {
        loadTrongSoFromDatabase();

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdatingFromCode) {
                    kiemTraVaHienThiTongTrongSo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtWTx1.addTextChangedListener(watcher);
        edtWTx2.addTextChangedListener(watcher);
        edtWTx3.addTextChangedListener(watcher);
        edtWThi.addTextChangedListener(watcher);

        btnDefaultTrongSo.setOnClickListener(v -> {
            isUpdatingFromCode = true;
            if (chkCoTx3.isChecked()) {
                edtWTx1.setText("20");
                edtWTx2.setText("20");
                edtWTx3.setText("20");
                edtWThi.setText("40");
            } else {
                edtWTx1.setText("30");
                edtWTx2.setText("20");
                edtWTx3.setText("0");
                edtWThi.setText("50");
            }
            isUpdatingFromCode = false;
            kiemTraVaHienThiTongTrongSo();
            Toast.makeText(this, "Đã khôi phục trọng số mặc định", Toast.LENGTH_SHORT).show();
        });

        btnSaveTrongSo.setOnClickListener(v -> luuTrongSoLamMacDinh());
    }

    private void kiemTraVaHienThiTongTrongSo() {
        try {
            float w1 = getFloatFromInput(edtWTx1);
            float w2 = getFloatFromInput(edtWTx2);
            float w3 = chkCoTx3.isChecked() ? getFloatFromInput(edtWTx3) : 0.0f;
            float wThi = getFloatFromInput(edtWThi);

            float tong = w1 + w2 + w3 + wThi;

            if (Math.abs(tong - 100.0f) <= 0.01f || Math.abs(tong - 1.0f) <= 0.005f) {
                tvTongTrongSoStatus.setText("✅ Tổng trọng số: 100% (Hợp lệ)");
                tvTongTrongSoStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            } else {
                float displayTong = (tong <= 2.0f) ? tong * 100.0f : tong;
                float lech = 100.0f - displayTong;
                if (lech > 0) {
                    tvTongTrongSoStatus.setText(String.format("⚠️ Tổng trọng số: %.1f%% (Đang thiếu %.1f%%)", displayTong, lech));
                } else {
                    tvTongTrongSoStatus.setText(String.format("⚠️ Tổng trọng số: %.1f%% (Đang thừa %.1f%%)", displayTong, Math.abs(lech)));
                }
                tvTongTrongSoStatus.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            }
        } catch (Exception e) {
            tvTongTrongSoStatus.setText("⚠️ Vui lòng nhập số hợp lệ cho trọng số");
            tvTongTrongSoStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error));
        }
    }

    private void loadTrongSoFromDatabase() {
        try {
            CauHinhTrongSo config = cauHinhDAO.getCauHinh();
            if (config != null) {
                isUpdatingFromCode = true;
                float w1 = (config.getTrongSoTx1() <= 1.0f) ? config.getTrongSoTx1() * 100 : config.getTrongSoTx1();
                float w2 = (config.getTrongSoTx2() <= 1.0f) ? config.getTrongSoTx2() * 100 : config.getTrongSoTx2();
                float w3 = (config.getTrongSoTx3() <= 1.0f) ? config.getTrongSoTx3() * 100 : config.getTrongSoTx3();
                float wThi = (config.getTrongSoThi() <= 1.0f) ? config.getTrongSoThi() * 100 : config.getTrongSoThi();

                edtWTx1.setText(formatNumber(w1));
                edtWTx2.setText(formatNumber(w2));
                edtWTx3.setText(formatNumber(w3));
                edtWThi.setText(formatNumber(wThi));
                isUpdatingFromCode = false;
                kiemTraVaHienThiTongTrongSo();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mặc định
        isUpdatingFromCode = true;
        edtWTx1.setText("20");
        edtWTx2.setText("20");
        edtWTx3.setText("20");
        edtWThi.setText("40");
        isUpdatingFromCode = false;
        kiemTraVaHienThiTongTrongSo();
    }

    private void luuTrongSoLamMacDinh() {
        // UX 5: Xác nhận trước khi cập nhật
        try {
            float w1 = getFloatFromInput(edtWTx1);
            float w2 = getFloatFromInput(edtWTx2);
            float w3 = chkCoTx3.isChecked() ? getFloatFromInput(edtWTx3) : 0.0f;
            float wThi = getFloatFromInput(edtWThi);

            float[] norm = DiemThiCalculator.validateAndNormalizeTrongSo(w1, w2, chkCoTx3.isChecked() ? w3 : null, wThi);

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận lưu trọng số")
                    .setMessage(String.format("Bạn có muốn lưu bộ trọng số (TX1: %.0f%%, TX2: %.0f%%, TX3: %.0f%%, Thi: %.0f%%) làm cấu hình mặc định trong CSDL không?",
                            norm[0] * 100, norm[1] * 100, norm[2] * 100, norm[3] * 100))
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        CauHinhTrongSo ch = new CauHinhTrongSo(1, "chính quy", norm[0], norm[1], norm[2], norm[3]);
                        boolean ok = cauHinhDAO.saveOrUpdateCauHinh(ch);
                        if (ok) {
                            Toast.makeText(this, "Đã lưu trọng số mặc định thành công vào CSDL!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi khi lưu vào CSDL!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupMucTieuSpinner() {
        String[] options = {
                "A (>= 8.5) - Xuất sắc / Giỏi",
                "B+ (>= 8.0) - Khá giỏi",
                "B (>= 7.0) - Khá",
                "C+ (>= 6.5) - Trung bình khá",
                "C (>= 5.5) - Trung bình",
                "D+ (>= 5.0) - Trung bình yếu",
                "D (>= 4.0) - Qua môn",
                "Tùy chỉnh điểm hệ 10"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spnMucTieuChu.setAdapter(adapter);

        spnMucTieuChu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: edtMucTieu10.setText("8.5"); break;
                    case 1: edtMucTieu10.setText("8.0"); break;
                    case 2: edtMucTieu10.setText("7.0"); break;
                    case 3: edtMucTieu10.setText("6.5"); break;
                    case 4: edtMucTieu10.setText("5.5"); break;
                    case 5: edtMucTieu10.setText("5.0"); break;
                    case 6: edtMucTieu10.setText("4.0"); break;
                    case 7:
                        edtMucTieu10.requestFocus();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupActionListeners() {
        chkCoTx3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilTx3.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tilWTx3.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                edtTx3.setText("");
                edtWTx3.setText("0");
            } else {
                if ("0".equals(edtWTx3.getText().toString().trim())) {
                    edtWTx3.setText("20");
                }
            }
            kiemTraVaHienThiTongTrongSo();
        });

        btnTinhDiem.setOnClickListener(v -> thucHienTinhDiem());

        btnSavePredictedToDB.setOnClickListener(v -> luuDiemThiDuKienVaoCSDL());
    }

    private void thucHienTinhDiem() {
        // Reset errors
        tilTenMon.setError(null);
        tilTx1.setError(null);
        tilTx2.setError(null);
        tilTx3.setError(null);
        tilWTx1.setError(null);
        tilWTx2.setError(null);
        tilWTx3.setError(null);
        tilWThi.setError(null);
        tilMucTieu10.setError(null);

        // Kiểm tra Tên môn học
        String tenMon = edtTenMon.getText().toString().trim();
        if (tenMon.isEmpty()) {
            tilTenMon.setError("Vui lòng nhập tên môn học hoặc chọn từ danh sách!");
            edtTenMon.requestFocus();
            return;
        }

        // 1. Kiểm tra dữ liệu đầu vào điểm thành phần
        ValidationResult valTx1 = DiemThiCalculator.validateDiem(edtTx1.getText().toString(), "TX1", true);
        if (!valTx1.isValid()) {
            tilTx1.setError(valTx1.getMessage());
            edtTx1.requestFocus();
            return;
        }

        ValidationResult valTx2 = DiemThiCalculator.validateDiem(edtTx2.getText().toString(), "TX2", true);
        if (!valTx2.isValid()) {
            tilTx2.setError(valTx2.getMessage());
            edtTx2.requestFocus();
            return;
        }

        Float tx3Score = null;
        if (chkCoTx3.isChecked()) {
            ValidationResult valTx3 = DiemThiCalculator.validateDiem(edtTx3.getText().toString(), "TX3", true);
            if (!valTx3.isValid()) {
                tilTx3.setError(valTx3.getMessage());
                edtTx3.requestFocus();
                return;
            }
            tx3Score = valTx3.getParsedValue();
        }

        // 2. Kiểm tra xử lý trọng số
        float w1, w2, w3, wThi;
        try {
            w1 = parseNumber(edtWTx1.getText().toString(), "Trọng số TX1");
        } catch (Exception e) {
            tilWTx1.setError(e.getMessage());
            edtWTx1.requestFocus();
            return;
        }

        try {
            w2 = parseNumber(edtWTx2.getText().toString(), "Trọng số TX2");
        } catch (Exception e) {
            tilWTx2.setError(e.getMessage());
            edtWTx2.requestFocus();
            return;
        }

        try {
            w3 = chkCoTx3.isChecked() ? parseNumber(edtWTx3.getText().toString(), "Trọng số TX3") : 0.0f;
        } catch (Exception e) {
            tilWTx3.setError(e.getMessage());
            edtWTx3.requestFocus();
            return;
        }

        try {
            wThi = parseNumber(edtWThi.getText().toString(), "Trọng số Thi");
        } catch (Exception e) {
            tilWThi.setError(e.getMessage());
            edtWThi.requestFocus();
            return;
        }

        // Kiểm tra hợp lệ tổng trọng số
        try {
            DiemThiCalculator.validateAndNormalizeTrongSo(w1, w2, chkCoTx3.isChecked() ? w3 : null, wThi);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            tilWThi.setError(e.getMessage());
            return;
        }

        // 3. Kiểm tra điểm mục tiêu
        float mucTieu;
        try {
            mucTieu = parseNumber(edtMucTieu10.getText().toString(), "Điểm mục tiêu");
        } catch (Exception e) {
            tilMucTieu10.setError(e.getMessage());
            edtMucTieu10.requestFocus();
            return;
        }

        ValidationResult valMucTieu = DiemThiCalculator.validateDiemMucTieu(mucTieu);
        if (!valMucTieu.isValid()) {
            tilMucTieu10.setError(valMucTieu.getMessage());
            edtMucTieu10.requestFocus();
            return;
        }

        // 4. Lập trình tính điểm cần thi
        currentKetQua = DiemThiCalculator.tinhDiemCanThi(
                valTx1.getParsedValue(),
                valTx2.getParsedValue(),
                tx3Score,
                w1, w2, chkCoTx3.isChecked() ? w3 : null, wThi,
                mucTieu
        );

        // 5. Hiển thị kết quả lên giao diện
        hienThiKetQua(currentKetQua);
    }

    private void hienThiKetQua(KetQuaTinhDiem kq) {
        cardEmptyState.setVisibility(View.GONE);
        cardKetQua.setVisibility(View.VISIBLE);
        cardBangMucTieu.setVisibility(View.VISIBLE);

        tvDiemQuaTrinh.setText(String.format("%.2f", kq.getDiemQuaTrinh()));
        tvDiemToiDa.setText(String.format("%.2f (Điểm %s)", kq.getDiemTongKetToiDa(), kq.getDiemChuToiDa()));
        tvLoiKhuyen.setText(kq.getLoiKhuyen());

        // UI 3: Màu sắc và Kiểu chữ: Trạng thái không chỉ dựa vào màu sắc mà có Icon và Nhãn chữ in hoa
        if (kq.isDaDuDiem()) {
            tvKetQuaIcon.setText("ℹ️");
            tvDiemThiCanDat.setText("ĐÃ ĐỦ ĐIỂM");
            tvDiemThiCanDat.setTextSize(26);
            tvDiemThiCanDat.setTextColor(ContextCompat.getColor(this, R.color.status_info));
            tvTrangThaiBadge.setText("[ĐÃ ĐỦ ĐIỂM] - Chỉ cần thi >= 4.0 chống điểm liệt");
            tvTrangThaiBadge.setTextColor(ContextCompat.getColor(this, R.color.status_info));
            layoutKetQuaHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.status_info_bg));
        } else if (!kq.isKhaThi()) {
            tvKetQuaIcon.setText("❌");
            tvDiemThiCanDat.setText(String.format("%.1f", kq.getDiemThiLamTron()));
            tvDiemThiCanDat.setTextSize(36);
            tvDiemThiCanDat.setTextColor(ContextCompat.getColor(this, R.color.status_error));
            tvTrangThaiBadge.setText("[BẤT KHẢ THI] - Cần điểm thi > 10.0");
            tvTrangThaiBadge.setTextColor(ContextCompat.getColor(this, R.color.status_error));
            layoutKetQuaHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.status_error_bg));
        } else {
            tvDiemThiCanDat.setText(String.format("%.1f / 10", kq.getDiemThiLamTron()));
            tvDiemThiCanDat.setTextSize(36);

            if (kq.getDiemThiLamTron() > 8.0f) {
                tvKetQuaIcon.setText("⚠️");
                tvDiemThiCanDat.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
                tvTrangThaiBadge.setText("[CẦN NỖ LỰC CAO] - Mục tiêu thử thách lớn");
                tvTrangThaiBadge.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
                layoutKetQuaHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.status_warning_bg));
            } else {
                tvKetQuaIcon.setText("✅");
                tvDiemThiCanDat.setTextColor(ContextCompat.getColor(this, R.color.status_success));
                tvTrangThaiBadge.setText("[KHẢ THI] - Mục tiêu trong tầm tay");
                tvTrangThaiBadge.setTextColor(ContextCompat.getColor(this, R.color.status_success));
                layoutKetQuaHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.status_success_bg));
            }
        }

        // Cập nhật bảng ma trận dự đoán
        mucTieuAdapter.updateData(kq.getDanhSachMucTieu());

        // Cuộn màn hình tới kết quả
        cardKetQua.post(() -> cardKetQua.getParent().requestChildFocus(cardKetQua, cardKetQua));
    }

    private void luuDiemThiDuKienVaoCSDL() {
        if (currentKetQua == null) {
            Toast.makeText(this, "Vui lòng tính điểm cần thi trước khi lưu!", Toast.LENGTH_SHORT).show();
            return;
        }

        float diemThiToSave = currentKetQua.getDiemThiLamTron();
        if (diemThiToSave < 0) diemThiToSave = 4.0f; // Điểm thi tối thiểu chống liệt
        if (diemThiToSave > 10.0f) diemThiToSave = 10.0f;

        final float finalDiemThi = diemThiToSave;
        String tenMon = edtTenMon.getText().toString().trim();

        // UX 5: Xác nhận thao tác quan trọng
        new AlertDialog.Builder(this)
                .setTitle("Lưu điểm thi dự kiến")
                .setMessage(String.format("Bạn có muốn lưu điểm thi dự kiến %.1f vào môn học '%s' không?\n\nĐiểm tổng kết hệ 10, hệ 4 và CPA tích lũy sẽ được tự động tính toán lại.",
                        finalDiemThi, tenMon))
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    thucHienLuuDiemVaoDB(finalDiemThi, tenMon);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void thucHienLuuDiemVaoDB(float diemThi, String tenMon) {
        try {
            float tx1 = getFloatFromInput(edtTx1);
            float tx2 = getFloatFromInput(edtTx2);
            Float tx3 = chkCoTx3.isChecked() ? getFloatFromInput(edtTx3) : null;

            float w1 = getFloatFromInput(edtWTx1);
            float w2 = getFloatFromInput(edtWTx2);
            Float w3 = chkCoTx3.isChecked() ? getFloatFromInput(edtWTx3) : null;
            float wThi = getFloatFromInput(edtWThi);
            float[] norm = DiemThiCalculator.validateAndNormalizeTrongSo(w1, w2, w3, wThi);

            float tk10 = DiemThiCalculator.tinhDiemTongKet10(tx1, tx2, tx3, diemThi, norm[0], norm[1], norm[2], norm[3]);
            float tk4 = DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(tk10);
            String diemChu = DiemThiCalculator.quyDoiDiemHe10SangDiemChu(tk10);
            String trangThai = DiemThiCalculator.xacDinhTrangThaiMonHoc(tk10, diemThi);

            if (currentSelectedMonHoc != null) {
                // Cập nhật môn đã có trong CSDL
                currentSelectedMonHoc.setDiemTx1(tx1);
                currentSelectedMonHoc.setDiemTx2(tx2);
                currentSelectedMonHoc.setDiemTx3(tx3);
                currentSelectedMonHoc.setDiemThi(diemThi);
                currentSelectedMonHoc.setDiemTongKet10(tk10);
                currentSelectedMonHoc.setDiemTongKet4(tk4);
                currentSelectedMonHoc.setDiemChu(diemChu);
                currentSelectedMonHoc.setTrangThai(trangThai);

                boolean updated = monHocDAO.updateMonHoc(currentSelectedMonHoc);
                if (updated) {
                    monHocDAO.syncGlobalStats(); // Đồng bộ CPA
                    Toast.makeText(this, String.format("Đã cập nhật điểm môn '%s' thành công!\nĐiểm TK10: %.2f | Điểm chữ: %s",
                            tenMon, tk10, diemChu), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Không thể cập nhật môn học!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Môn tự nhập: Lấy kỳ học đang chọn hoặc kỳ đầu tiên
                int kyId = 1;
                int pos = spnKyHoc.getSelectedItemPosition();
                if (pos > 0 && pos - 1 < danhSachKyHoc.size()) {
                    kyId = danhSachKyHoc.get(pos - 1).getId();
                } else if (!danhSachKyHoc.isEmpty()) {
                    kyId = danhSachKyHoc.get(0).getId();
                }

                MonHoc newMh = new MonHoc(0, kyId, tenMon, 3, tx1, tx2, tx3, diemThi, tk10, tk4, diemChu, trangThai);
                long newId = monHocDAO.insertMonHoc(newMh);
                if (newId > 0) {
                    monHocDAO.syncGlobalStats();
                    Toast.makeText(this, String.format("Đã thêm mới môn '%s' vào CSDL thành công!\nĐiểm TK10: %.2f | Điểm chữ: %s",
                            tenMon, tk10, diemChu), Toast.LENGTH_LONG).show();
                    // Làm mới danh sách
                    capNhatDanhSachMonHocTheoKy(spnKyHoc.getSelectedItemPosition());
                } else {
                    Toast.makeText(this, "Môn học có thể đã tồn tại trong kỳ này!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private float getFloatFromInput(TextInputEditText edt) {
        String txt = edt.getText().toString().trim().replace(',', '.');
        if (txt.isEmpty()) return 0.0f;
        return Float.parseFloat(txt);
    }

    private float parseNumber(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập " + fieldName + "!");
        }
        try {
            return Float.parseFloat(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " không đúng định dạng số!");
        }
    }

    private String formatNumber(float val) {
        if (val == (int) val) {
            return String.valueOf((int) val);
        }
        return String.valueOf(val);
    }
}
