package com.example.hethongbangiay.models;

import java.io.Serializable;

public class DanhMuc implements Serializable {
    private String danhMucId;
    private String tenDanhMuc;
    private String moTaDanhMuc;
    private String anhDanhMuc;
    private boolean active;

    public DanhMuc() {
    }

    public DanhMuc(String danhMucId, String tenDanhMuc, String moTaDanhMuc,
                   String anhDanhMuc, boolean active) {
        this.danhMucId = danhMucId;
        this.tenDanhMuc = tenDanhMuc;
        this.moTaDanhMuc = moTaDanhMuc;
        this.anhDanhMuc = anhDanhMuc;
        this.active = active;
    }

    public String getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(String danhMucId) {
        this.danhMucId = danhMucId;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getMoTaDanhMuc() {
        return moTaDanhMuc;
    }

    public void setMoTaDanhMuc(String moTaDanhMuc) {
        this.moTaDanhMuc = moTaDanhMuc;
    }

    public String getAnhDanhMuc() {
        return anhDanhMuc;
    }

    public void setAnhDanhMuc(String anhDanhMuc) {
        this.anhDanhMuc = anhDanhMuc;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}