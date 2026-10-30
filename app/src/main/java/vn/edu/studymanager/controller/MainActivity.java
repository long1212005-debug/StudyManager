package vn.edu.studymanager.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import vn.edu.studymanager.adapters.KyHocAdapter;
import vn.edu.studymanager.model.dao.KyHocDAO;
import vn.edu.studymanager.model.dao.MonHocDAO;
import vn.edu.studymanager.model.dao.SinhVienDAO;
import vn.edu.studymanager.model.entity.KyHoc;
import vn.edu.studymanager.model.entity.SinhVien;

public class MainActivity extends AppCompatActivity implements KyHocAdapter.OnKyHocActionListener {

    private Toolbar toolbar;
    private TextView tvTenSinhVien, tvMssvVaTruong;
    private Button btnEditMucTieu;

    private TextView tvCpaHienTai, tvTongTinChiTichLuy, tvMucTieuCpa, tvHocLucBadge;
    private View btnNavTinhDiem;

    private Button btnThemKyHoc;
    private View cardEmptyKyHoc;
    private RecyclerView rvKyHoc;

    private KyHocAdapter adapter;
    private List<KyHoc> danhSachKyHoc = new ArrayList<>();

    private SinhVienDAO sinhVienDAO;
    private KyHocDAO kyHocDAO;
    private MonHocDAO monHocDAO;
    private SinhVien currentSinhVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        initDAOs();
        setupRecyclerView();
        setupActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn làm mới dữ liệu từ SQLite khi quay lại màn hình chính (UX 2)
        loadDashboardData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTenSinhVien = findViewById(R.id.tvTenSinhVien);
        tvMssvVaTruong = findViewById(R.id.tvMssvVaTruong);
        btnEditMucTieu = findViewById(R.id.btnEditMucTieu);

        tvCpaHienTai = findViewById(R.id.tvCpaHienTai);
        tvTongTinChiTichLuy = findViewById(R.id.tvTongTinChiTichLuy);
        tvMucTieuCpa = findViewById(R.id.tvMucTieuCpa);
        tvHocLucBadge = findViewById(R.id.tvHocLucBadge);

        btnNavTinhDiem = findViewById(R.id.btnNavTinhDiem);
        btnThemKyHoc = findViewById(R.id.btnThemKyHoc);
        cardEmptyKyHoc = findViewById(R.id.cardEmptyKyHoc);
        rvKyHoc = findViewById(R.id.rvKyHoc);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initDAOs() {
        sinhVienDAO = new SinhVienDAO(this);
        kyHocDAO = new KyHocDAO(this);
        monHocDAO = new MonHocDAO(this);
    }

    private void setupRecyclerView() {
        rvKyHoc.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KyHocAdapter(this, danhSachKyHoc, this);
        rvKyHoc.setAdapter(adapter);
    }

    private void setupActionListeners() {
        btnNavTinhDiem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TinhDiemCanThiActivity.class);
            startActivity(intent);
        });

        btnThemKyHoc.setOnClickListener(v -> showThemSuaKyHocDialog(null));

        btnEditMucTieu.setOnClickListener(v -> showEditMucTieuDialog());
    }

    private void loadDashboardData() {
        // Đồng bộ CPA từ các môn học
        monHocDAO.syncGlobalStats();

        // 1. Tải thông tin sinh viên
        currentSinhVien = sinhVienDAO.getSinhVien();
        if (currentSinhVien == null) {
            currentSinhVien = new SinhVien(1, "Sinh viên HaUI", "2023604407", 0.0f, 0, 3.20f, "Đại học Công nghiệp Hà Nội");
            sinhVienDAO.insertSinhVien(currentSinhVien);
        }

        tvTenSinhVien.setText(currentSinhVien.getHoTen());
        tvMssvVaTruong.setText("MSSV: " + currentSinhVien.getMssv() + " • " + currentSinhVien.getTruongDaoTao());

        // 2. Hiển thị Dashboard CPA
        float cpa = currentSinhVien.getCpaHienTai();
        tvCpaHienTai.setText(String.format("%.2f", cpa));
        tvTongTinChiTichLuy.setText("Tín chỉ tích lũy: " + currentSinhVien.getTongTinChiTichLuy() + " tín chỉ");
        tvMucTieuCpa.setText(String.format("Mục tiêu: %.2f", currentSinhVien.getMucTieuCpa()));

        // Phân loại xếp loại học lực theo CPA tích lũy (Quy chế HaUI)
        if (cpa >= 3.6f) {
            tvHocLucBadge.setText("🏆 [XUẤT SẮC]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.status_success));
        } else if (cpa >= 3.2f) {
            tvHocLucBadge.setText("⭐ [GIỎI]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.primary));
        } else if (cpa >= 2.5f) {
            tvHocLucBadge.setText("👍 [KHÁ]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
        } else if (cpa >= 2.0f) {
            tvHocLucBadge.setText("📋 [TRUNG BÌNH]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else if (cpa > 0.0f) {
            tvHocLucBadge.setText("⚠️ [YẾU / KÉM]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.status_error));
        } else {
            tvHocLucBadge.setText("[CHƯA CÓ ĐIỂM]");
            tvHocLucBadge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }

        // 3. Tải danh sách kỳ học
        danhSachKyHoc = kyHocDAO.getDisplayKyHoc(currentSinhVien.getId());
        adapter.updateData(danhSachKyHoc);

        // Trạng thái rỗng (UI 8)
        if (danhSachKyHoc == null || danhSachKyHoc.isEmpty()) {
            cardEmptyKyHoc.setVisibility(View.VISIBLE);
            rvKyHoc.setVisibility(View.GONE);
        } else {
            cardEmptyKyHoc.setVisibility(View.GONE);
            rvKyHoc.setVisibility(View.VISIBLE);
        }
    }

    private void showThemSuaKyHocDialog(KyHoc kyHocToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_them_sua_ky_hoc, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogKyHocTitle);
        TextInputLayout tilTenKy = dialogView.findViewById(R.id.tilDialogTenKy);
        TextInputEditText edtTenKy = dialogView.findViewById(R.id.edtDialogTenKy);
        CheckBox chkHoanThanh = dialogView.findViewById(R.id.chkDialogKyHoanThanh);

        boolean isEdit = (kyHocToEdit != null);
        tvTitle.setText(isEdit ? "Chỉnh sửa kỳ học" : "Thêm kỳ học mới");

        if (isEdit) {
            edtTenKy.setText(kyHocToEdit.getTenKy());
            chkHoanThanh.setChecked(kyHocToEdit.isTrangThai());
        }

        builder.setPositiveButton(isEdit ? "Cập nhật" : "Lưu", null);
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            tilTenKy.setError(null);
            String tenKy = edtTenKy.getText().toString().trim();
            if (tenKy.isEmpty()) {
                tilTenKy.setError("Vui lòng nhập tên kỳ học!");
                edtTenKy.requestFocus();
                return;
            }

            boolean daHoanThanh = chkHoanThanh.isChecked();

            if (isEdit) {
                kyHocToEdit.setTenKy(tenKy);
                kyHocToEdit.setTrangThai(daHoanThanh);
                kyHocDAO.updateKyHoc(kyHocToEdit);
                Toast.makeText(this, "Đã cập nhật kỳ học thành công!", Toast.LENGTH_SHORT).show();
            } else {
                KyHoc newKy = new KyHoc(0, currentSinhVien.getId(), tenKy, 0.0f, 0, daHoanThanh);
                kyHocDAO.insertKyHoc(newKy);
                Toast.makeText(this, "Đã thêm kỳ học mới thành công!", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            loadDashboardData();
        });
    }

    private void showEditMucTieuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thiết lập mục tiêu CPA");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_sua_ky_hoc, null);
        TextView tvTitle = view.findViewById(R.id.tvDialogKyHocTitle);
        tvTitle.setVisibility(View.GONE);
        TextInputLayout til = view.findViewById(R.id.tilDialogTenKy);
        til.setHint("Mục tiêu CPA (0.0 - 4.0)");
        TextInputEditText edt = view.findViewById(R.id.edtDialogTenKy);
        edt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edt.setText(String.valueOf(currentSinhVien.getMucTieuCpa()));

        CheckBox chk = view.findViewById(R.id.chkDialogKyHoanThanh);
        chk.setVisibility(View.GONE);

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                float target = Float.parseFloat(edt.getText().toString().trim().replace(',', '.'));
                if (target < 0 || target > 4.0f) {
                    Toast.makeText(this, "Mục tiêu CPA phải từ 0.0 đến 4.0!", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentSinhVien.setMucTieuCpa(target);
                sinhVienDAO.updateSinhVien(currentSinhVien);
                Toast.makeText(this, "Đã cập nhật mục tiêu CPA!", Toast.LENGTH_SHORT).show();
                loadDashboardData();
            } catch (Exception e) {
                Toast.makeText(this, "Định dạng số không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Callbacks từ KyHocAdapter
    @Override
    public void onItemClick(KyHoc kyHoc) {
        Intent intent = new Intent(this, ChiTietKyHocActivity.class);
        intent.putExtra(ChiTietKyHocActivity.EXTRA_KY_HOC_ID, kyHoc.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(KyHoc kyHoc) {
        showThemSuaKyHocDialog(kyHoc);
    }

    @Override
    public void onDeleteClick(KyHoc kyHoc) {
        // UX 5: Hộp thoại xác nhận quan trọng
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa kỳ học")
                .setMessage(String.format("Xóa kỳ '%s' sẽ xóa toàn bộ các môn học và điểm số trong kỳ này.\n\nBạn có chắc chắn muốn xóa không?", kyHoc.getTenKy()))
                .setPositiveButton("Xóa kỳ học", (dialog, which) -> {
                    kyHocDAO.deleteKyHoc(kyHoc.getId());
                    monHocDAO.syncGlobalStats();
                    Toast.makeText(this, "Đã xóa kỳ học thành công!", Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
