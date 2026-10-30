package vn.edu.studymanager.model.entity;

import java.io.Serializable;

public class MucTieuDuDoan implements Serializable {
    private String diemChu;
    private float diemHe4;
    private float diemHe10ToiThieu;
    private float diemThiCanDat;
    private boolean khaThi;
    private boolean daDat;
    private String trangThaiMoTa;

    public MucTieuDuDoan() {
    }

    public MucTieuDuDoan(String diemChu, float diemHe4, float diemHe10ToiThieu, float diemThiCanDat, boolean khaThi, boolean daDat, String trangThaiMoTa) {
        this.diemChu = diemChu;
        this.diemHe4 = diemHe4;
        this.diemHe10ToiThieu = diemHe10ToiThieu;
        this.diemThiCanDat = diemThiCanDat;
        this.khaThi = khaThi;
        this.daDat = daDat;
        this.trangThaiMoTa = trangThaiMoTa;
    }

    public String getDiemChu() {
        return diemChu;
    }

    public void setDiemChu(String diemChu) {
        this.diemChu = diemChu;
    }

    public float getDiemHe4() {
        return diemHe4;
    }

    public void setDiemHe4(float diemHe4) {
        this.diemHe4 = diemHe4;
    }

    public float getDiemHe10ToiThieu() {
        return diemHe10ToiThieu;
    }

    public void setDiemHe10ToiThieu(float diemHe10ToiThieu) {
        this.diemHe10ToiThieu = diemHe10ToiThieu;
    }

    public float getDiemThiCanDat() {
        return diemThiCanDat;
    }

    public void setDiemThiCanDat(float diemThiCanDat) {
        this.diemThiCanDat = diemThiCanDat;
    }

    public boolean isKhaThi() {
        return khaThi;
    }

    public void setKhaThi(boolean khaThi) {
        this.khaThi = khaThi;
    }

    public boolean isDaDat() {
        return daDat;
    }

    public void setDaDat(boolean daDat) {
        this.daDat = daDat;
    }

    public String getTrangThaiMoTa() {
        return trangThaiMoTa;
    }

    public void setTrangThaiMoTa(String trangThaiMoTa) {
        this.trangThaiMoTa = trangThaiMoTa;
    }
}
