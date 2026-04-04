package com.example.hethongbangiay.models;

import java.io.Serializable;

public class ChiTietDonHang implements Serializable {
    private String chiTietDonHangId;
    private String sizeGiayId;
    private String donHangId;
    private int soLuongMua;
    private double giaThoiDiemMua;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(String chiTietDonHangId, String sizeGiayId, String donHangId,
                          int soLuongMua, double giaThoiDiemMua) {
        this.chiTietDonHangId = chiTietDonHangId;
        this.sizeGiayId = sizeGiayId;
        this.donHangId = donHangId;
        this.soLuongMua = soLuongMua;
        this.giaThoiDiemMua = giaThoiDiemMua;
    }

    public String getChiTietDonHangId() {
        return chiTietDonHangId;
    }

    public void setChiTietDonHangId(String chiTietDonHangId) {
        this.chiTietDonHangId = chiTietDonHangId;
    }

    public String getSizeGiayId() {
        return sizeGiayId;
    }

    public void setSizeGiayId(String sizeGiayId) {
        this.sizeGiayId = sizeGiayId;
    }

    public String getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(String donHangId) {
        this.donHangId = donHangId;
    }

    public int getSoLuongMua() {
        return soLuongMua;
    }

    public void setSoLuongMua(int soLuongMua) {
        this.soLuongMua = soLuongMua;
    }

    public double getGiaThoiDiemMua() {
        return giaThoiDiemMua;
    }

    public void setGiaThoiDiemMua(double giaThoiDiemMua) {
        this.giaThoiDiemMua = giaThoiDiemMua;
    }
}