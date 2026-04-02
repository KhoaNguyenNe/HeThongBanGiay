package com.example.hethongbangiay.models;

import java.io.Serializable;

public class DiaChi implements Serializable {
    private String diaChiId;
    private String nguoiDungId;
    private String diaChi;
    private String soDienThoai;
    private String tenNguoiNhan;
    private boolean macDinh;

    public DiaChi() {
    }

    public DiaChi(String diaChiId, String nguoiDungId, String diaChi,
                  String soDienThoai, String tenNguoiNhan, boolean macDinh) {
        this.diaChiId = diaChiId;
        this.nguoiDungId = nguoiDungId;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.tenNguoiNhan = tenNguoiNhan;
        this.macDinh = macDinh;
    }

    public String getDiaChiId() {
        return diaChiId;
    }

    public void setDiaChiId(String diaChiId) {
        this.diaChiId = diaChiId;
    }

    public String getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(String nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public boolean isMacDinh() {
        return macDinh;
    }

    public void setMacDinh(boolean macDinh) {
        this.macDinh = macDinh;
    }
}