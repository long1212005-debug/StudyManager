package vn.edu.studymanager.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import vn.edu.studymanager.adapters.MonHocAdapter;
import vn.edu.studymanager.model.dao.CauHinhTrongSoDAO;
import vn.edu.studymanager.model.dao.KyHocDAO;
import vn.edu.studymanager.model.dao.MonHocDAO;
import vn.edu.studymanager.model.entity.CauHinhTrongSo;
import vn.edu.studymanager.model.entity.KyHoc;
import vn.edu.studymanager.model.entity.MonHoc;
import vn.edu.studymanager.service.DiemThiCalculator;
import vn.edu.studymanager.service.ValidationResult;

public class ChiTietKyHocActivity extends AppCompatActivity implements MonHocAdapter.OnMonHocActionListener {

    public static final String EXTRA_KY_HOC_ID = "EXTRA_KY_HOC_ID";

    private Toolbar toolbar;
    private TextView tvTenKyHeader, tvTrangThaiKyBadge, tvGpaKyHeader, tvXepLoaiKy, tvTinChiKyHeader, tvSoMonInfo;
    private Button btnThemMonHoc;
    private View cardEmptyMonHoc;
    private RecyclerView rvMonHoc;

    private MonHocAdapter adapter;
    private List<MonHoc> danhSachMonHoc = new ArrayList<>();

    private KyHocDAO kyHocDAO;
    private MonHocDAO monHocDAO;
    private CauHinhTrongSoDAO cauHinhDAO;

    private int kyHocId = 1;
    private KyHoc currentKyHoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_ky_hoc);

        kyHocId = getIntent().getIntExtra(EXTRA_KY_HOC_ID, 1);

        initViews();
        setupToolbar();
        initDAOs();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTenKyHeader = findViewById(R.id.tvTenKyHeader);
        tvTrangThaiKyBadge = findViewById(R.id.tvTrangThaiKyBadge);
        tvGpaKyHeader = findViewById(R.id.tvGpaKyHeader);
        tvXepLoaiKy = findViewById(R.id.tvXepLoaiKy);
        tvTinChiKyHeader = findViewById(R.id.tvTinChiKyHeader);
        tvSoMonInfo = findViewById(R.id.tvSoMonInfo);
        btnThemMonHoc = findViewById(R.id.btnThemMonHoc);
        cardEmptyMonHoc = findViewById(R.id.cardEmptyMonHoc);

        rvMonHoc = findViewById(R.id.rvMonHoc);
        rvMonHoc.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonHocAdapter(this, danhSachMonHoc, this);
        rvMonHoc.setAdapter(adapter);

        btnThemMonHoc.setOnClickListener(v -> showThemMonHocDialog(null));
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

    private void loadData() {
        currentKyHoc = kyHocDAO.getKyHocById(kyHocId);
        if (currentKyHoc == null) {
            Toast.makeText(this, "Không tìm thấy thông tin kỳ học!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tự động tính toán lại GPA của kỳ từ môn học
        kyHocDAO.updateGpaKyHoc(kyHocId);
        currentKyHoc = kyHocDAO.getKyHocById(kyHocId);

        // Đọc danh sách môn học của kỳ
        danhSachMonHoc = monHocDAO.getMonHocByKy(kyHocId);
        adapter.updateData(danhSachMonHoc);

        // Cập nhật thông tin Header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(currentKyHoc.getTenKy());
        }
        tvTenKyHeader.setText(currentKyHoc.getTenKy());
        tvGpaKyHeader.setText(String.format("%.2f", currentKyHoc.getGpaKy()));
        tvTinChiKyHeader.setText(String.valueOf(currentKyHoc.getTongTinChiKy()));
        tvSoMonInfo.setText(danhSachMonHoc.size() + " môn học");

        // Xếp loại học lực theo GPA kỳ (HaUI)
        float gpa = currentKyHoc.getGpaKy();
        if (gpa >= 3.6f) {
            tvXepLoaiKy.setText("Học lực: Xuất sắc");
        } else if (gpa >= 3.2f) {
            tvXepLoaiKy.setText("Học lực: Giỏi");
        } else if (gpa >= 2.5f) {
            tvXepLoaiKy.setText("Học lực: Khá");
        } else if (gpa >= 2.0f) {
            tvXepLoaiKy.setText("Học lực: Trung bình");
        } else if (gpa > 0.0f) {
            tvXepLoaiKy.setText("Học lực: Yếu / Kém");
        } else {
            tvXepLoaiKy.setText("Chưa có điểm");
        }

        // Trạng thái kỳ (UI 3)
        if (currentKyHoc.isTrangThai()) {
            tvTrangThaiKyBadge.setText("✅ [ĐÃ HOÀN THÀNH]");
            tvTrangThaiKyBadge.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvTrangThaiKyBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_success_bg));
        } else {
            tvTrangThaiKyBadge.setText("⏳ [ĐANG HỌC]");
            tvTrangThaiKyBadge.setTextColor(ContextCompat.getColor(this, R.color.status_info));
            tvTrangThaiKyBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_info_bg));
        }

        // Xử lý Trạng thái rỗng (UI 8)
        if (danhSachMonHoc.isEmpty()) {
            cardEmptyMonHoc.setVisibility(View.VISIBLE);
            rvMonHoc.setVisibility(View.GONE);
        } else {
            cardEmptyMonHoc.setVisibility(View.GONE);
            rvMonHoc.setVisibility(View.VISIBLE);
        }
    }

    private void showThemMonHocDialog(MonHoc monHocToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_them_sua_mon_hoc, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputLayout tilTenMon = dialogView.findViewById(R.id.tilDialogTenMon);
        TextInputEditText edtTenMon = dialogView.findViewById(R.id.edtDialogTenMon);
        TextInputLayout tilSoTinChi = dialogView.findViewById(R.id.tilDialogSoTinChi);
        TextInputEditText edtSoTinChi = dialogView.findViewById(R.id.edtDialogSoTinChi);
        TextInputLayout tilTx1 = dialogView.findViewById(R.id.tilDialogTx1);
        TextInputEditText edtTx1 = dialogView.findViewById(R.id.edtDialogTx1);
        TextInputLayout tilTx2 = dialogView.findViewById(R.id.tilDialogTx2);
        TextInputEditText edtTx2 = dialogView.findViewById(R.id.edtDialogTx2);
        TextInputLayout tilTx3 = dialogView.findViewById(R.id.tilDialogTx3);
        TextInputEditText edtTx3 = dialogView.findViewById(R.id.edtDialogTx3);
        TextInputLayout tilDiemThi = dialogView.findViewById(R.id.tilDialogDiemThi);
        TextInputEditText edtDiemThi = dialogView.findViewById(R.id.edtDialogDiemThi);

        boolean isEdit = (monHocToEdit != null);
        tvTitle.setText(isEdit ? "Chỉnh sửa điểm môn học" : "Thêm môn học mới");

        if (isEdit) {
            edtTenMon.setText(monHocToEdit.getTenMon());
            edtSoTinChi.setText(String.valueOf(monHocToEdit.getSoTinChi()));
            if (monHocToEdit.getDiemTx1() > 0) edtTx1.setText(String.valueOf(monHocToEdit.getDiemTx1()));
            if (monHocToEdit.getDiemTx2() > 0) edtTx2.setText(String.valueOf(monHocToEdit.getDiemTx2()));
            if (monHocToEdit.getDiemTx3() != null && monHocToEdit.getDiemTx3() > 0) {
                edtTx3.setText(String.valueOf(monHocToEdit.getDiemTx3()));
            }
            if (monHocToEdit.getDiemThi() > 0) edtDiemThi.setText(String.valueOf(monHocToEdit.getDiemThi()));
        }

        builder.setPositiveButton(isEdit ? "Cập nhật" : "Lưu", null);
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom onClick để validate không tự động đóng dialog nếu lỗi
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            tilTenMon.setError(null);
            tilSoTinChi.setError(null);
            tilTx1.setError(null);
            tilTx2.setError(null);
            tilTx3.setError(null);
            tilDiemThi.setError(null);

            String tenMon = edtTenMon.getText().toString().trim();
            if (tenMon.isEmpty()) {
                tilTenMon.setError("Vui lòng nhập tên môn học!");
                edtTenMon.requestFocus();
                return;
            }

            int soTinChi;
            try {
                soTinChi = Integer.parseInt(edtSoTinChi.getText().toString().trim());
                if (soTinChi <= 0 || soTinChi > 15) {
                    tilSoTinChi.setError("Số tín chỉ phải từ 1 đến 15!");
                    edtSoTinChi.requestFocus();
                    return;
                }
            } catch (Exception e) {
                tilSoTinChi.setError("Số tín chỉ không hợp lệ!");
                edtSoTinChi.requestFocus();
                return;
            }

            // Kiểm tra trùng tên môn trong kỳ (nếu thêm mới)
            if (!isEdit) {
                for (MonHoc mh : danhSachMonHoc) {
                    if (mh.getTenMon().equalsIgnoreCase(tenMon)) {
                        tilTenMon.setError("Môn học này đã có trong kỳ!");
                        edtTenMon.requestFocus();
                        return;
                    }
                }
            }

            // Validate điểm số nếu có nhập
            Float tx1 = parseOptionalScore(edtTx1.getText().toString(), tilTx1, "TX1");
            if (tilTx1.getError() != null) return;

            Float tx2 = parseOptionalScore(edtTx2.getText().toString(), tilTx2, "TX2");
            if (tilTx2.getError() != null) return;

            Float tx3 = parseOptionalScore(edtTx3.getText().toString(), tilTx3, "TX3");
            if (tilTx3.getError() != null) return;

            Float diemThi = parseOptionalScore(edtDiemThi.getText().toString(), tilDiemThi, "Điểm thi");
            if (tilDiemThi.getError() != null) return;

            // Tính điểm tổng kết nếu đã có điểm thi
            float valTx1 = (tx1 != null) ? tx1 : 0.0f;
            float valTx2 = (tx2 != null) ? tx2 : 0.0f;
            float valDiemThi = (diemThi != null) ? diemThi : 0.0f;

            float tk10 = 0.0f;
            float tk4 = 0.0f;
            String diemChu = "";
            String trangThai = "Đang học";

            if (diemThi != null && diemThi > 0) {
                // Đọc cấu hình trọng số
                CauHinhTrongSo ch = cauHinhDAO.getCauHinh();
                float[] norm = (ch != null) ?
                        DiemThiCalculator.validateAndNormalizeTrongSo(ch.getTrongSoTx1(), ch.getTrongSoTx2(), ch.getTrongSoTx3(), ch.getTrongSoThi()) :
                        new float[]{0.2f, 0.2f, 0.2f, 0.4f};

                tk10 = DiemThiCalculator.tinhDiemTongKet10(valTx1, valTx2, tx3, valDiemThi, norm[0], norm[1], norm[2], norm[3]);
                tk4 = DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(tk10);
                diemChu = DiemThiCalculator.quyDoiDiemHe10SangDiemChu(tk10);
                trangThai = DiemThiCalculator.xacDinhTrangThaiMonHoc(tk10, valDiemThi);
            }

            if (isEdit) {
                monHocToEdit.setTenMon(tenMon);
                monHocToEdit.setSoTinChi(soTinChi);
                monHocToEdit.setDiemTx1(valTx1);
                monHocToEdit.setDiemTx2(valTx2);
                monHocToEdit.setDiemTx3(tx3);
                monHocToEdit.setDiemThi(valDiemThi);
                monHocToEdit.setDiemTongKet10(tk10);
                monHocToEdit.setDiemTongKet4(tk4);
                monHocToEdit.setDiemChu(diemChu);
                monHocToEdit.setTrangThai(trangThai);

                monHocDAO.updateMonHoc(monHocToEdit);
                Toast.makeText(this, "Đã cập nhật môn học thành công!", Toast.LENGTH_SHORT).show();
            } else {
                MonHoc newMh = new MonHoc(0, kyHocId, tenMon, soTinChi, valTx1, valTx2, tx3, valDiemThi, tk10, tk4, diemChu, trangThai);
                monHocDAO.insertMonHoc(newMh);
                Toast.makeText(this, "Đã thêm môn học mới thành công!", Toast.LENGTH_SHORT).show();
            }

            // Đồng bộ GPA và CPA
            kyHocDAO.updateGpaKyHoc(kyHocId);
            monHocDAO.syncGlobalStats();

            dialog.dismiss();
            loadData();
        });
    }

    private Float parseOptionalScore(String text, TextInputLayout til, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        ValidationResult res = DiemThiCalculator.validateDiem(text, fieldName, false);
        if (!res.isValid()) {
            til.setError(res.getMessage());
            return null;
        }
        return res.getParsedValue();
    }

    // Callbacks từ MonHocAdapter
    @Override
    public void onItemClick(MonHoc monHoc) {
        showThemMonHocDialog(monHoc);
    }

    @Override
    public void onEditScoreClick(MonHoc monHoc) {
        showThemMonHocDialog(monHoc);
    }

    @Override
    public void onCalculateExamClick(MonHoc monHoc) {
        Intent intent = new Intent(this, TinhDiemCanThiActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(MonHoc monHoc) {
        // UX 5: Hộp thoại xác nhận trước khi xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa môn học")
                .setMessage(String.format("Bạn có chắc chắn muốn xóa môn '%s' khỏi kỳ này không?\n\nĐiểm GPA và CPA tích lũy sẽ được tính toán lại.", monHoc.getTenMon()))
                .setPositiveButton("Xóa", (dialog, which) -> {
                    monHocDAO.deleteMonHocByKyHocId(monHoc.getId()); // Note: or delete by mon hoc id
                    // Xóa môn học bằng id
                    android.database.sqlite.SQLiteDatabase db = new vn.edu.studymanager.model.database.DBHelper(this).getWritableDatabase();
                    db.delete("MonHoc", "id = ?", new String[]{String.valueOf(monHoc.getId())});

                    kyHocDAO.updateGpaKyHoc(kyHocId);
                    monHocDAO.syncGlobalStats();
                    Toast.makeText(this, "Đã xóa môn học!", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
