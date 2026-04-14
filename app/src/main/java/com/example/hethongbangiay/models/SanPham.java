package com.example.hethongbangiay.models;

import com.google.type.DateTime;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String sanPhamId;
    private String danhMucId;
    private String tenSanPham;
    private double donGia;
    private String anhSanPham;
    private String moTaSanPham;
    private double diemDanhGia;
    private int luotBan;
    private DateTime ngayTao;
    private DateTime ngayCapNhat;
    private boolean active;

    public SanPham() {
    }

    public SanPham(String sanPhamId, String danhMucId, String tenSanPham,
                   double donGia, String anhSanPham, String moTaSanPham,
                   String gioiTinh, double diemDanhGia, int luotBan,
                   DateTime ngayTao, DateTime ngayCapNhat, boolean active) {
        this.sanPhamId = sanPhamId;
        this.danhMucId = danhMucId;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.anhSanPham = anhSanPham;
        this.moTaSanPham = moTaSanPham;
        this.diemDanhGia = diemDanhGia;
        this.luotBan = luotBan;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.active = active;
    }

    public String getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(String sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public String getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(String danhMucId) {
        this.danhMucId = danhMucId;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }

    public String getMoTaSanPham() {
        return moTaSanPham;
    }

    public void setMoTaSanPham(String moTaSanPham) {
        this.moTaSanPham = moTaSanPham;
    }

    public double getDiemDanhGia() {
        return diemDanhGia;
    }

    public void setDiemDanhGia(double diemDanhGia) {
        this.diemDanhGia = diemDanhGia;
    }

    public int getLuotBan() {
        return luotBan;
    }

    public void setLuotBan(int luotBan) {
        this.luotBan = luotBan;
    }

    public DateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(DateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public DateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(DateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
