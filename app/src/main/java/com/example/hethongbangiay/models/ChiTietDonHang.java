package com.example.hethongbangiay.models;

import java.io.Serializable;

public class ChiTietDonHang implements Serializable {
    private String tenSanPham;
    private int giaTien;
    private int sizeGiay;
    private String mauSac;
    private int soLuong;
    private String anhSanPham;


    public ChiTietDonHang() {
    }
//
//    public ChiTietDonHang(String chiTietDonHangId, String sizeGiayId, String donHangId,
//                          int soLuongMua, double giaThoiDiemMua) {
//        this.chiTietDonHangId = chiTietDonHangId;
//        this.sizeGiayId = sizeGiayId;
//        this.donHangId = donHangId;
//        this.soLuongMua = soLuongMua;
//        this.giaThoiDiemMua = giaThoiDiemMua;
//    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(int giaTien) {
        this.giaTien = giaTien;
    }

    public int getSizeGiay() {
        return sizeGiay;
    }

    public void setSizeGiay(int sizeGiay) {
        this.sizeGiay = sizeGiay;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }
}