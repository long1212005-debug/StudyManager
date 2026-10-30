package vn.edu.studymanager.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KetQuaTinhDiem implements Serializable {
    private float diemQuaTrinh;
    private float diemMucTieu;
    private String diemChuMucTieu;
    private float diemThiCanDat;
    private float diemThiLamTron;
    private float diemTongKetToiDa;
    private String diemChuToiDa;
    private boolean khaThi;
    private boolean daDuDiem;
    private String loiKhuyen;
    private List<MucTieuDuDoan> danhSachMucTieu = new ArrayList<>();

    public KetQuaTinhDiem() {
    }

    public float getDiemQuaTrinh() {
        return diemQuaTrinh;
    }

    public void setDiemQuaTrinh(float diemQuaTrinh) {
        this.diemQuaTrinh = diemQuaTrinh;
    }

    public float getDiemMucTieu() {
        return diemMucTieu;
    }

    public void setDiemMucTieu(float diemMucTieu) {
        this.diemMucTieu = diemMucTieu;
    }

    public String getDiemChuMucTieu() {
        return diemChuMucTieu;
    }

    public void setDiemChuMucTieu(String diemChuMucTieu) {
        this.diemChuMucTieu = diemChuMucTieu;
    }

    public float getDiemThiCanDat() {
        return diemThiCanDat;
    }

    public void setDiemThiCanDat(float diemThiCanDat) {
        this.diemThiCanDat = diemThiCanDat;
    }

    public float getDiemThiLamTron() {
        return diemThiLamTron;
    }

    public void setDiemThiLamTron(float diemThiLamTron) {
        this.diemThiLamTron = diemThiLamTron;
    }

    public float getDiemTongKetToiDa() {
        return diemTongKetToiDa;
    }

    public void setDiemTongKetToiDa(float diemTongKetToiDa) {
        this.diemTongKetToiDa = diemTongKetToiDa;
    }

    public String getDiemChuToiDa() {
        return diemChuToiDa;
    }

    public void setDiemChuToiDa(String diemChuToiDa) {
        this.diemChuToiDa = diemChuToiDa;
    }

    public boolean isKhaThi() {
        return khaThi;
    }

    public void setKhaThi(boolean khaThi) {
        this.khaThi = khaThi;
    }

    public boolean isDaDuDiem() {
        return daDuDiem;
    }

    public void setDaDuDiem(boolean daDuDiem) {
        this.daDuDiem = daDuDiem;
    }

    public String getLoiKhuyen() {
        return loiKhuyen;
    }

    public void setLoiKhuyen(String loiKhuyen) {
        this.loiKhuyen = loiKhuyen;
    }

    public List<MucTieuDuDoan> getDanhSachMucTieu() {
        return danhSachMucTieu;
    }

    public void setDanhSachMucTieu(List<MucTieuDuDoan> danhSachMucTieu) {
        this.danhSachMucTieu = danhSachMucTieu;
    }
}
