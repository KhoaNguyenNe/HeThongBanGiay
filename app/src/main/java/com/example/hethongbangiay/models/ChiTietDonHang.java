package com.example.hethongbangiay.models;

import java.io.Serializable;

public class ChiTietDonHang implements Serializable {
    private String chiTietDonHangId;
    private String tenSanPham;
    private double giaTien;

    private int sizeGiay;
    private String mauSac;
    private int soLuong;
    private String anhSanPham;
    private String sanPhamId;
    private String donHangId;
    private boolean daDanhGia = false;

    public String getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(String donHangId) {
        this.donHangId = donHangId;
    }

    public boolean isDaDanhGia() {
        return daDanhGia;
    }

    public void setDaDanhGia(boolean daDanhGia) {
        this.daDanhGia = daDanhGia;
    }

    public String getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(String sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public ChiTietDonHang() {
    }


    public ChiTietDonHang(String chiTietDonHangId, String tenSanPham, double giaTien, int sizeGiay, String mauSac, int soLuong, String anhSanPham, String sanPhamId, String donHangId) {
        this.chiTietDonHangId = chiTietDonHangId;
        this.tenSanPham = tenSanPham;
        this.giaTien = giaTien;
        this.sizeGiay = sizeGiay;
        this.mauSac = mauSac;
        this.soLuong = soLuong;
        this.anhSanPham = anhSanPham;
        this.sanPhamId = sanPhamId;
        this.donHangId = donHangId;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public double getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(double giaTien) {
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

    public String getChiTietDonHangId() {
        return chiTietDonHangId;
    }

    public void setChiTietDonHangId(String chiTietDonHangId) {
        this.chiTietDonHangId = chiTietDonHangId;
    }

    @Override
    public String toString() {
        return "ChiTietDonHang{" +
                "tenSanPham='" + tenSanPham + '\'' +
                ", giaTien=" + giaTien +
                ", mauSac='" + mauSac + '\'' +
                ", soLuong='" + soLuong + '\'' +
                ", sizeGiay=" + sizeGiay +
                '}';
    }
}