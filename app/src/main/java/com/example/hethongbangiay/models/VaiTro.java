package com.example.hethongbangiay.models;

import java.io.Serializable;

public class VaiTro implements Serializable {
    private String vaiTroId;
    private String tenVaiTro;

    public VaiTro() {
    }

    public VaiTro(String vaiTroId, String tenVaiTro) {
        this.vaiTroId = vaiTroId;
        this.tenVaiTro = tenVaiTro;
    }

    public String getVaiTroId() {
        return vaiTroId;
    }

    public void setVaiTroId(String vaiTroId) {
        this.vaiTroId = vaiTroId;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }
}