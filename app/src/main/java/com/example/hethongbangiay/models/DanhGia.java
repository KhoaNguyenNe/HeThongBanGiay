package com.example.hethongbangiay.models;

import com.google.type.DateTime;

import java.io.Serializable;

public class DanhGia implements Serializable {
    private String danhGiaId;
    private String nguoiDungId;
    private String sanPhamId;
    private int rating;
    private String comment;
    private DateTime ngayDanhGia;

    public DanhGia() {
    }

    public DanhGia(String danhGiaId, String nguoiDungId, String sanPhamId,
                   int rating, String comment, DateTime ngayDanhGia) {
        this.danhGiaId = danhGiaId;
        this.nguoiDungId = nguoiDungId;
        this.sanPhamId = sanPhamId;
        this.rating = rating;
        this.comment = comment;
        this.ngayDanhGia = ngayDanhGia;
    }

    public String getDanhGiaId() {
        return danhGiaId;
    }

    public void setDanhGiaId(String danhGiaId) {
        this.danhGiaId = danhGiaId;
    }

    public String getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(String nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(String sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DateTime getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(DateTime ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }
}