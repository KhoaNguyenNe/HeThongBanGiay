package com.example.hethongbangiay.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.io.Serializable;
import java.util.List;

public class DonHang implements Serializable {

    private String donHangId;
    private String nguoiDungId;
    private Timestamp ngayDatHang;
    private String tinhTrangDonHang;
    private Timestamp ngayGiaoHang;
    private Timestamp ngayHuy;
    private String phuongThucThanhToan;

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    private double tongTien;


    public DonHang() {
    }

    public DonHang(String donHangId, String nguoiDungId, Timestamp ngayDatHang,
                   String tinhTrangDonHang, Timestamp ngayGiaoHang,
                   Timestamp ngayHuy, String phuongThucThanhToan, double tongTien) {
        this.donHangId = donHangId;
        this.nguoiDungId = nguoiDungId;
        this.ngayDatHang = ngayDatHang;
        this.tinhTrangDonHang = tinhTrangDonHang;
        this.ngayGiaoHang = ngayGiaoHang;
        this.ngayHuy = ngayHuy;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.tongTien = tongTien;
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

    public Timestamp getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(Timestamp ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public String getTinhTrangDonHang() {
        return tinhTrangDonHang;
    }

    public void setTinhTrangDonHang(String tinhTrangDonHang) {
        this.tinhTrangDonHang = tinhTrangDonHang;
    }

    public Timestamp getNgayGiaoHang() {
        return ngayGiaoHang;
    }

    public void setNgayGiaoHang(Timestamp ngayGiaoHang) {
        this.ngayGiaoHang = ngayGiaoHang;
    }

    public Timestamp getNgayHuy() {
        return ngayHuy;
    }

    public void setNgayHuy(Timestamp ngayHuy) {
        this.ngayHuy = ngayHuy;
    }


    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}