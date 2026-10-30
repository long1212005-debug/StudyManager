package vn.edu.studymanager;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

import vn.edu.studymanager.model.entity.KetQuaTinhDiem;
import vn.edu.studymanager.model.entity.MucTieuDuDoan;
import vn.edu.studymanager.service.DiemThiCalculator;
import vn.edu.studymanager.service.ValidationResult;

public class DiemThiCalculatorTest {

    // ==========================================
    // 1. KIỂM THỬ XỬ LÝ TRỌNG SỐ
    // ==========================================

    @Test
    public void testNormalizeTrongSo_DangThapPhanHopLe() {
        // 0.2, 0.2, 0.2, 0.4 (tổng = 1.0)
        float[] w = DiemThiCalculator.validateAndNormalizeTrongSo(0.2f, 0.2f, 0.2f, 0.4f);
        assertEquals(0.2f, w[0], 0.001f);
        assertEquals(0.2f, w[1], 0.001f);
        assertEquals(0.2f, w[2], 0.001f);
        assertEquals(0.4f, w[3], 0.001f);
    }

    @Test
    public void testNormalizeTrongSo_DangPhanTramHopLe() {
        // 20%, 20%, 20%, 40% (tổng = 100%) -> Tự động chuyển thành 0.2, 0.2, 0.2, 0.4
        float[] w = DiemThiCalculator.validateAndNormalizeTrongSo(20f, 20f, 20f, 40f);
        assertEquals(0.2f, w[0], 0.001f);
        assertEquals(0.2f, w[1], 0.001f);
        assertEquals(0.2f, w[2], 0.001f);
        assertEquals(0.4f, w[3], 0.001f);
    }

    @Test
    public void testNormalizeTrongSo_KhongCoTX3() {
        // Môn học chỉ có 2 điểm TX: 30%, 20%, Thi 50%
        float[] w = DiemThiCalculator.validateAndNormalizeTrongSo(0.3f, 0.2f, null, 0.5f);
        assertEquals(0.3f, w[0], 0.001f);
        assertEquals(0.2f, w[1], 0.001f);
        assertEquals(0.0f, w[2], 0.001f);
        assertEquals(0.5f, w[3], 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeTrongSo_TrongSoAm() {
        DiemThiCalculator.validateAndNormalizeTrongSo(-0.1f, 0.3f, 0.2f, 0.6f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeTrongSo_TrongSoThiBangKhong() {
        DiemThiCalculator.validateAndNormalizeTrongSo(0.5f, 0.5f, 0.0f, 0.0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeTrongSo_TongKhongBang100Hoac1() {
        // Tổng = 0.8 (thiếu 20%)
        DiemThiCalculator.validateAndNormalizeTrongSo(0.2f, 0.2f, 0.1f, 0.3f);
    }

    // ==========================================
    // 2. KIỂM THỬ XỬ LÝ DỮ LIỆU ĐẦU VÀO
    // ==========================================

    @Test
    public void testValidateDiem_HopLe() {
        ValidationResult res1 = DiemThiCalculator.validateDiem("8.5", "TX1", true);
        assertTrue(res1.isValid());
        assertEquals(8.5f, res1.getParsedValue(), 0.001f);

        // Hỗ trợ dấu phẩy "8,5"
        ValidationResult res2 = DiemThiCalculator.validateDiem("8,5", "TX2", true);
        assertTrue(res2.isValid());
        assertEquals(8.5f, res2.getParsedValue(), 0.001f);
    }

    @Test
    public void testValidateDiem_BoTrongBatBuoc() {
        ValidationResult res = DiemThiCalculator.validateDiem("", "TX1", true);
        assertFalse(res.isValid());
        assertNotNull(res.getMessage());
    }

    @Test
    public void testValidateDiem_BoTrongKhongBatBuoc() {
        ValidationResult res = DiemThiCalculator.validateDiem("", "TX3", false);
        assertTrue(res.isValid());
    }

    @Test
    public void testValidateDiem_SaiDinhDangChu() {
        ValidationResult res = DiemThiCalculator.validateDiem("abc", "TX1", true);
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("không đúng định dạng"));
    }

    @Test
    public void testValidateDiem_DiemAmHoacQua10() {
        ValidationResult resAm = DiemThiCalculator.validateDiem("-1", "TX1", true);
        assertFalse(resAm.isValid());

        ValidationResult resQua10 = DiemThiCalculator.validateDiem("10.5", "TX1", true);
        assertFalse(resQua10.isValid());
    }

    @Test
    public void testValidateDiemMucTieu() {
        assertTrue(DiemThiCalculator.validateDiemMucTieu(8.5f).isValid());
        assertFalse(DiemThiCalculator.validateDiemMucTieu(3.5f).isValid()); // < 4.0 là F
        assertFalse(DiemThiCalculator.validateDiemMucTieu(10.5f).isValid());
    }

    // ==========================================
    // 3. KIỂM THỬ LẬP TRÌNH TÍNH ĐIỂM CẦN THI
    // ==========================================

    @Test
    public void testTinhDiemCanThi_KhaThi() {
        // TX1 = 7, TX2 = 8, TX3 = 9. Trọng số: 20%, 20%, 20%, Thi 40%
        // Điểm quá trình = 7*0.2 + 8*0.2 + 9*0.2 = 1.4 + 1.6 + 1.8 = 4.8
        // Mục tiêu A (8.5)
        // Điểm thi cần đạt = (8.5 - 4.8) / 0.4 = 3.7 / 0.4 = 9.25
        KetQuaTinhDiem kq = DiemThiCalculator.tinhDiemCanThi(7.0f, 8.0f, 9.0f, 0.2f, 0.2f, 0.2f, 0.4f, 8.5f);

        assertEquals(4.8f, kq.getDiemQuaTrinh(), 0.01f);
        assertEquals(9.25f, kq.getDiemThiCanDat(), 0.01f);
        assertEquals(9.3f, kq.getDiemThiLamTron(), 0.01f);
        assertTrue(kq.isKhaThi());
        assertFalse(kq.isDaDuDiem());
    }

    @Test
    public void testTinhDiemCanThi_BatKhaThi() {
        // TX1 = 4, TX2 = 4, TX3 = 4. Trọng số: 20%, 20%, 20%, Thi 40%
        // Điểm quá trình = 2.4
        // Điểm tối đa nếu thi 10 = 2.4 + 4.0 = 6.4 (Chỉ đạt C)
        // Mục tiêu A (8.5) -> (8.5 - 2.4) / 0.4 = 15.25 > 10.0 => Bất khả thi
        KetQuaTinhDiem kq = DiemThiCalculator.tinhDiemCanThi(4.0f, 4.0f, 4.0f, 0.2f, 0.2f, 0.2f, 0.4f, 8.5f);

        assertFalse(kq.isKhaThi());
        assertEquals(6.4f, kq.getDiemTongKetToiDa(), 0.01f);
        assertEquals("C", kq.getDiemChuToiDa());
    }

    @Test
    public void testTinhDiemCanThi_DaDuDiem() {
        // TX1 = 10, TX2 = 10, TX3 = 10. Điểm QT = 6.0
        // Mục tiêu D (4.0 - Qua môn)
        // Điểm thi cần đạt = (4.0 - 6.0) / 0.4 = -5.0 <= 0 => Đã đủ điểm
        KetQuaTinhDiem kq = DiemThiCalculator.tinhDiemCanThi(10.0f, 10.0f, 10.0f, 0.2f, 0.2f, 0.2f, 0.4f, 4.0f);

        assertTrue(kq.isKhaThi());
        assertTrue(kq.isDaDuDiem());
        assertTrue(kq.getLoiKhuyen().contains("điểm liệt"));
    }

    @Test
    public void testTinhTatCaMucTieu() {
        // Điểm quá trình = 4.8, Trọng số thi = 0.4
        List<MucTieuDuDoan> list = DiemThiCalculator.tinhTatCaMucTieu(4.8f, 0.4f);
        assertEquals(7, list.size()); // A, B+, B, C+, C, D+, D

        // A (8.5) -> can thi 9.25 làm tròn 9.3 -> khả thi
        MucTieuDuDoan a = list.get(0);
        assertEquals("A", a.getDiemChu());
        assertTrue(a.isKhaThi());

        // D (4.0) -> can thi (4.0 - 4.8)/0.4 = -2.0 -> da dat
        MucTieuDuDoan d = list.get(6);
        assertEquals("D", d.getDiemChu());
        assertTrue(d.isDaDat());
    }

    // ==========================================
    // 4. KIỂM THỬ QUY ĐỔI HỆ 10, HỆ 4 VÀ TRẠNG THÁI
    // ==========================================

    @Test
    public void testQuyDoiHe10SangHe4VaTrangThai() {
        assertEquals(4.0f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(8.7f), 0.01f);
        assertEquals(3.5f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(8.2f), 0.01f);
        assertEquals(3.0f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(7.5f), 0.01f);
        assertEquals(2.0f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(5.8f), 0.01f);
        assertEquals(1.0f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(4.2f), 0.01f);
        assertEquals(0.0f, DiemThiCalculator.quyDoiDiemHe10SangDiemHe4(3.8f), 0.01f);

        // Kiểm tra trạng thái trượt nếu điểm thi < 4.0 dù tổng kết >= 4.0
        assertEquals("Trượt", DiemThiCalculator.xacDinhTrangThaiMonHoc(5.0f, 3.5f));
        assertEquals("Qua môn", DiemThiCalculator.xacDinhTrangThaiMonHoc(5.0f, 4.0f));
    }
}
