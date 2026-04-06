package com.example.hethongbangiay.models;

public class NguoiDung {
    private String uid;
    private String email;
    private String hoTen;
    private String soDienThoai;
    private String avatar;
    private String vaiTro;

    public NguoiDung() {
    }

    public NguoiDung(String uid, String email, String hoTen, String soDienThoai, String avatar, String vaiTro) {
        this.uid = uid;
        this.email = email;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.avatar = avatar;
        this.vaiTro = vaiTro;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }
}