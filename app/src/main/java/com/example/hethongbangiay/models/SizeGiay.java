package com.example.hethongbangiay.models;

import java.io.Serializable;

public class SizeGiay implements Serializable {
    private String sizeGiayId;
    private String sanPhamId;
    private int size;
    private int soLuong;

    public SizeGiay() {
    }

    public SizeGiay(String sizeGiayId, String sanPhamId, int size, int soLuong) {
        this.sizeGiayId = sizeGiayId;
        this.sanPhamId = sanPhamId;
        this.size = size;
        this.soLuong = soLuong;
    }

    public String getSizeGiayId() {
        return sizeGiayId;
    }

    public void setSizeGiayId(String sizeGiayId) {
        this.sizeGiayId = sizeGiayId;
    }

    public String getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(String sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}