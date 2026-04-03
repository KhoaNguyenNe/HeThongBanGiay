package com.example.hethongbangiay.models;

import com.google.type.DateTime;

import java.io.Serializable;

public class HoaDon implements Serializable {
    private String hoaDonId;
    private String diaChiId;
    private String donHangId;
    private double tongTienThanhToan;
    private DateTime ngayLapHoaDon;
    private double tienShip;
    private String phuongThucThanhToan;
    private String trangThaiThanhToan;
    private String maGiaoDich;

    public HoaDon() {
    }

    public HoaDon(String hoaDonId, String diaChiId, String donHangId,
                  double tongTienThanhToan, DateTime ngayLapHoaDon,
                  double tienShip, String phuongThucThanhToan,
                  String trangThaiThanhToan, String maGiaoDich) {
        this.hoaDonId = hoaDonId;
        this.diaChiId = diaChiId;
        this.donHangId = donHangId;
        this.tongTienThanhToan = tongTienThanhToan;
        this.ngayLapHoaDon = ngayLapHoaDon;
        this.tienShip = tienShip;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.maGiaoDich = maGiaoDich;
    }

    public String getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(String hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public String getDiaChiId() {
        return diaChiId;
    }

    public void setDiaChiId(String diaChiId) {
        this.diaChiId = diaChiId;
    }

    public String getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(String donHangId) {
        this.donHangId = donHangId;
    }

    public double getTongTienThanhToan() {
        return tongTienThanhToan;
    }

    public void setTongTienThanhToan(double tongTienThanhToan) {
        this.tongTienThanhToan = tongTienThanhToan;
    }

    public DateTime getNgayLapHoaDon() {
        return ngayLapHoaDon;
    }

    public void setNgayLapHoaDon(DateTime ngayLapHoaDon) {
        this.ngayLapHoaDon = ngayLapHoaDon;
    }

    public double getTienShip() {
        return tienShip;
    }

    public void setTienShip(double tienShip) {
        this.tienShip = tienShip;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }
}