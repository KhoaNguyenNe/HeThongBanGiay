package com.example.hethongbangiay.models;

import com.google.type.DateTime;

import java.io.Serializable;

public class NguoiDung implements Serializable {
    private String uid;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String vaiTro;
    private String anhDaiDien;
    private boolean active;
    private String provider;
    private DateTime ngayTao;
    private DateTime ngayCapNhat;



    public NguoiDung(String uid, String hoTen, String email, String soDienThoai, String vaiTro, String anhDaiDien, boolean active, String provider, DateTime ngayTao, DateTime ngayCapNhat) {
        this.uid = uid;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.anhDaiDien = anhDaiDien;
        this.active = active;
        this.provider = provider;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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
}
