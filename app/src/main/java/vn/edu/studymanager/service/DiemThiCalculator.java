package vn.edu.studymanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import vn.edu.studymanager.model.entity.KetQuaTinhDiem;
import vn.edu.studymanager.model.entity.MucTieuDuDoan;

public class DiemThiCalculator {

    public static final float DIEM_LIET = 4.0f; // Quy chế HaUI điểm thi < 4.0 là học lại
    public static final float EPSILON = 0.015f; // Dung sai kiểm tra tổng trọng số

    /**
     * Kiểm tra và chuẩn hóa trọng số.
     * Cho phép nhập dạng phần trăm (tổng ~ 100) hoặc dạng thập phân (tổng ~ 1.0).
     */
    public static float[] validateAndNormalizeTrongSo(float wTx1, float wTx2, Float wTx3, float wThi) {
        float valTx3 = (wTx3 != null) ? wTx3 : 0.0f;

        if (wTx1 < 0 || wTx2 < 0 || valTx3 < 0 || wThi < 0) {
            throw new IllegalArgumentException("Các trọng số không được là số âm!");
        }

        if (wThi <= 0) {
            throw new IllegalArgumentException("Trọng số thi phải lớn hơn 0!");
        }

        float tong = wTx1 + wTx2 + valTx3 + wThi;

        // Nếu nhập theo thang 100% (ví dụ 20 + 20 + 20 + 40 = 100)
        if (Math.abs(tong - 100.0f) <= 1.0f) {
            return new float[]{wTx1 / 100.0f, wTx2 / 100.0f, valTx3 / 100.0f, wThi / 100.0f};
        }

        // Nếu nhập theo thang 1.0 (ví dụ 0.2 + 0.2 + 0.2 + 0.4 = 1.0)
        if (Math.abs(tong - 1.0f) <= EPSILON) {
            return new float[]{wTx1, wTx2, valTx3, wThi};
        }

        // Tổng không khớp 100% hoặc 1.0
        float tongPhanTram = (tong <= 2.0f) ? tong * 100.0f : tong;
        throw new IllegalArgumentException(String.format(
                "Tổng trọng số phải bằng 100%% (hoặc 1.0). Hiện tại tổng là: %.1f%% (lệch %.1f%%)",
                tongPhanTram, Math.abs(100.0f - tongPhanTram)
        ));
    }

    /**
     * Kiểm tra tính hợp lệ của điểm thành phần nhập vào.
     */
    public static ValidationResult validateDiem(String input, String tenDauDiem, boolean isRequired) {
        if (input == null || input.trim().isEmpty()) {
            if (isRequired) {
                return ValidationResult.error("Vui lòng nhập điểm " + tenDauDiem + "!");
            }
            return ValidationResult.success(0.0f);
        }

        String sanitized = input.trim().replace(',', '.');
        float score;
        try {
            score = Float.parseFloat(sanitized);
        } catch (NumberFormatException e) {
            return ValidationResult.error("Điểm " + tenDauDiem + " không đúng định dạng số!");
        }

        if (score < 0.0f || score > 10.0f) {
            return ValidationResult.error(String.format("Điểm %s phải từ 0.0 đến 10.0 (hiện tại: %.2f)!", tenDauDiem, score));
        }

        return ValidationResult.success(score);
    }

    /**
     * Kiểm tra điểm mục tiêu hệ 10.
     */
    public static ValidationResult validateDiemMucTieu(float diemMucTieu) {
        if (diemMucTieu < 4.0f || diemMucTieu > 10.0f) {
            return ValidationResult.error(String.format("Điểm mục tiêu hệ 10 phải từ 4.0 đến 10.0 (hiện tại: %.2f)!", diemMucTieu));
        }
        return ValidationResult.success(diemMucTieu);
    }

    /**
     * Tính điểm quá trình: TX1*w1 + TX2*w2 + TX3*w3
     */
    public static float tinhDiemQuaTrinh(float tx1, float tx2, Float tx3, float wTx1, float wTx2, float wTx3) {
        float valTx3 = (tx3 != null) ? tx3 : 0.0f;
        float dqt = (tx1 * wTx1) + (tx2 * wTx2) + (valTx3 * wTx3);
        return round(dqt, 2);
    }

    /**
     * Tính điểm tổng kết hệ 10 từ các điểm thành phần và điểm thi.
     */
    public static float tinhDiemTongKet10(float tx1, float tx2, Float tx3, float diemThi, float w1, float w2, float w3, float wThi) {
        float dqt = tinhDiemQuaTrinh(tx1, tx2, tx3, w1, w2, w3);
        float tk = dqt + (diemThi * wThi);
        if (tk > 10.0f) tk = 10.0f;
        return round(tk, 2);
    }

    /**
     * Quy đổi điểm hệ 10 sang hệ 4 (theo quy chế tín chỉ HaUI).
     */
    public static float quyDoiDiemHe10SangDiemHe4(float diem10) {
        if (diem10 >= 8.5f) return 4.0f;
        if (diem10 >= 8.0f) return 3.5f;
        if (diem10 >= 7.0f) return 3.0f;
        if (diem10 >= 6.5f) return 2.5f;
        if (diem10 >= 5.5f) return 2.0f;
        if (diem10 >= 5.0f) return 1.5f;
        if (diem10 >= 4.0f) return 1.0f;
        return 0.0f;
    }

    /**
     * Xác định trạng thái môn học (Qua môn / Trượt môn).
     */
    public static String xacDinhTrangThaiMonHoc(float diem10, float diemThi) {
        if (diemThi < DIEM_LIET || diem10 < 4.0f) {
            return "Trượt";
        }
        return "Qua môn";
    }

    /**
     * Lập trình tính điểm thi cần đạt và tạo bảng dự đoán cho các mức điểm.
     */
    public static KetQuaTinhDiem tinhDiemCanThi(
            float tx1, float tx2, Float tx3,
            float rawWTx1, float rawWTx2, Float rawWTx3, float rawWThi,
            float diemMucTieu) {

        float[] w = validateAndNormalizeTrongSo(rawWTx1, rawWTx2, rawWTx3, rawWThi);
        float w1 = w[0];
        float w2 = w[1];
        float w3 = w[2];
        float wThi = w[3];

        KetQuaTinhDiem ketQua = new KetQuaTinhDiem();
        ketQua.setDiemMucTieu(diemMucTieu);
        ketQua.setDiemChuMucTieu(quyDoiDiemHe10SangDiemChu(diemMucTieu));

        // 1. Tính điểm quá trình
        float diemQT = tinhDiemQuaTrinh(tx1, tx2, tx3, w1, w2, w3);
        ketQua.setDiemQuaTrinh(diemQT);

        // 2. Điểm tổng kết tối đa có thể đạt được nếu thi 10 điểm
        float maxTK10 = round(diemQT + (10.0f * wThi), 2);
        if (maxTK10 > 10.0f) maxTK10 = 10.0f;
        ketQua.setDiemTongKetToiDa(maxTK10);
        ketQua.setDiemChuToiDa(quyDoiDiemHe10SangDiemChu(maxTK10));

        // 3. Tính điểm thi cần đạt: (diemMucTieu - diemQT) / wThi
        float canThi = (diemMucTieu - diemQT) / wThi;
        ketQua.setDiemThiCanDat(round(canThi, 2));

        // Làm tròn lên 1 chữ số thập phân (đảm bảo an toàn đạt mục tiêu)
        float canThiLamTron = (float) (Math.ceil(canThi * 10.0) / 10.0);
        ketQua.setDiemThiLamTron(canThiLamTron);

        // 4. Đánh giá tính khả thi và lời khuyên
        if (canThi <= 0.0f) {
            ketQua.setKhaThi(true);
            ketQua.setDaDuDiem(true);
            ketQua.setLoiKhuyen(String.format(
                    "Điểm quá trình của bạn là %.2f (rất cao). Bạn đã chắc chắn đạt mục tiêu %s ngay cả khi thi 0 điểm! " +
                    "Tuy nhiên, theo quy chế đào tạo, bạn vẫn cần đạt điểm thi tối thiểu >= %.1f để không bị điểm liệt.",
                    diemQT, ketQua.getDiemChuMucTieu(), DIEM_LIET
            ));
        } else if (canThi > 10.0f) {
            ketQua.setKhaThi(false);
            ketQua.setDaDuDiem(false);
            ketQua.setLoiKhuyen(String.format(
                    "Mục tiêu này không khả thi (cần thi %.2f > 10). " +
                    "Với điểm quá trình hiện tại là %.2f, điểm tổng kết tối đa bạn có thể đạt được là %.2f (Điểm chữ: %s).",
                    canThi, diemQT, maxTK10, ketQua.getDiemChuToiDa()
            ));
        } else {
            ketQua.setKhaThi(true);
            ketQua.setDaDuDiem(false);

            if (canThiLamTron < DIEM_LIET) {
                ketQua.setLoiKhuyen(String.format(
                        "Bạn chỉ cần thi đạt %.1f điểm. Tuy nhiên, lưu ý điểm thi phải đạt tối thiểu >= %.1f để chống điểm liệt qua môn.",
                        canThiLamTron, DIEM_LIET
                ));
            } else if (canThiLamTron <= 6.0f) {
                ketQua.setLoiKhuyen(String.format(
                        "Mục tiêu trong tầm tay! Bạn cần đạt tối thiểu %.1f điểm trong kỳ thi kết thúc học phần.",
                        canThiLamTron
                ));
            } else if (canThiLamTron <= 8.0f) {
                ketQua.setLoiKhuyen(String.format(
                        "Mục tiêu vừa sức. Cần ôn tập nghiêm túc để thi đạt tối thiểu %.1f điểm.",
                        canThiLamTron
                ));
            } else {
                ketQua.setLoiKhuyen(String.format(
                        "Thử thách cao! Bạn cần nỗ lực ôn tập xuất sắc để thi đạt từ %.1f điểm trở lên.",
                        canThiLamTron
                ));
            }
        }

        // 5. Tạo bảng dự đoán cho tất cả các thang điểm chữ chuẩn
        ketQua.setDanhSachMucTieu(tinhTatCaMucTieu(diemQT, wThi));

        return ketQua;
    }

    /**
     * Tạo danh sách dự đoán điểm thi cần thiết cho toàn bộ thang điểm chữ.
     */
    public static List<MucTieuDuDoan> tinhTatCaMucTieu(float diemQT, float wThi) {
        List<MucTieuDuDoan> list = new ArrayList<>();

        // Thang điểm chuẩn theo quy chế đào tạo tín chỉ HaUI
        final String[] grades = {"A", "B+", "B", "C+", "C", "D+", "D"};
        final float[] gpa4 = {4.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f, 1.0f};
        final float[] min10 = {8.5f, 8.0f, 7.0f, 6.5f, 5.5f, 5.0f, 4.0f};

        for (int i = 0; i < grades.length; i++) {
            float target10 = min10[i];
            float reqExam = (target10 - diemQT) / wThi;
            float reqExamRounded = (float) (Math.ceil(reqExam * 10.0) / 10.0);

            boolean khaThi = (reqExam <= 10.0f);
            boolean daDat = (reqExam <= 0.0f);
            String moTa;

            if (daDat) {
                moTa = "[ĐÃ ĐẠT] (Thi >= " + DIEM_LIET + ")";
            } else if (!khaThi) {
                moTa = "[BẤT KHẢ THI]";
            } else {
                float displayScore = Math.max(reqExamRounded, (grades[i].equals("D") ? DIEM_LIET : reqExamRounded));
                moTa = "[CẦN THI] >= " + String.format("%.1f", displayScore);
            }

            list.add(new MucTieuDuDoan(grades[i], gpa4[i], target10, reqExamRounded, khaThi, daDat, moTa));
        }

        return list;
    }

    /**
     * Quy đổi điểm hệ 10 sang thang chữ (HaUI).
     */
    public static String quyDoiDiemHe10SangDiemChu(float diem10) {
        if (diem10 >= 8.5f) return "A";
        if (diem10 >= 8.0f) return "B+";
        if (diem10 >= 7.0f) return "B";
        if (diem10 >= 6.5f) return "C+";
        if (diem10 >= 5.5f) return "C";
        if (diem10 >= 5.0f) return "D+";
        if (diem10 >= 4.0f) return "D";
        return "F";
    }

    /**
     * Lấy mốc điểm hệ 10 tối thiểu tương ứng với điểm chữ.
     */
    public static float quyDoiDiemChuSangDiem10ToiThieu(String diemChu) {
        if (diemChu == null) return 4.0f;
        switch (diemChu.trim().toUpperCase()) {
            case "A": return 8.5f;
            case "B+": return 8.0f;
            case "B": return 7.0f;
            case "C+": return 6.5f;
            case "C": return 5.5f;
            case "D+": return 5.0f;
            case "D": return 4.0f;
            case "F": return 0.0f;
            default: return 4.0f;
        }
    }

    /**
     * Hàm làm tròn số thực.
     */
    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
