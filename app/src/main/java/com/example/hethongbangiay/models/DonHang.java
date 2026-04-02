package com.example.hethongbangiay.models;

import com.google.type.DateTime;

import java.io.Serializable;

public class DonHang implements Serializable {
    private String donHangId;
    private String nguoiDungId;
    private DateTime ngayDatHang;
    private String tinhTrangDonHang;
    private DateTime ngayGiaoHang;
    private DateTime ngayHuy;

    public DonHang() {
    }

    public DonHang(String donHangId, String nguoiDungId, DateTime ngayDatHang,
                   String tinhTrangDonHang, DateTime ngayGiaoHang, DateTime ngayHuy) {
        this.donHangId = donHangId;
        this.nguoiDungId = nguoiDungId;
        this.ngayDatHang = ngayDatHang;
        this.tinhTrangDonHang = tinhTrangDonHang;
        this.ngayGiaoHang = ngayGiaoHang;
        this.ngayHuy = ngayHuy;
    }

    public String getDonHangId() {
        return donHangId;
    }

    public void setDonHangId(String donHangId) {
        this.donHangId = donHangId;
    }

    public String getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(String nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public DateTime getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(DateTime ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public String getTinhTrangDonHang() {
        return tinhTrangDonHang;
    }

    public void setTinhTrangDonHang(String tinhTrangDonHang) {
        this.tinhTrangDonHang = tinhTrangDonHang;
    }

    public DateTime getNgayGiaoHang() {
        return ngayGiaoHang;
    }

    public void setNgayGiaoHang(DateTime ngayGiaoHang) {
        this.ngayGiaoHang = ngayGiaoHang;
    }

    public DateTime getNgayHuy() {
        return ngayHuy;
    }

    public void setNgayHuy(DateTime ngayHuy) {
        this.ngayHuy = ngayHuy;
    }
}