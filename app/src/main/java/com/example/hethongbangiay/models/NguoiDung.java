package com.example.hethongbangiay.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class NguoiDung {
    private String uid;
    private String email;
    private String hoTen;
    private String soDienThoai;
    private String avatar;
    private String vaiTro;
    private Boolean locked;
    private Boolean active;
    private Boolean deleted;
    private Long createdAt;
    private Long updatedAt;
    private Long lastLoginAt;

    public NguoiDung() {
    }

    public NguoiDung(String uid, String email, String hoTen, String soDienThoai, String avatar, String vaiTro) {
        long now = System.currentTimeMillis();

        this.uid = uid;
        this.email = email;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.avatar = avatar;
        this.vaiTro = vaiTro;
        this.locked = false;
        this.active = true;
        this.deleted = false;
        this.createdAt = now;
        this.updatedAt = now;
        this.lastLoginAt = null;
    }

    public NguoiDung(String uid,
                     String email,
                     String hoTen,
                     String soDienThoai,
                     String avatar,
                     String vaiTro,
                     Boolean locked,
                     Boolean active,
                     Boolean deleted,
                     Long createdAt,
                     Long updatedAt,
                     Long lastLoginAt) {
        this.uid = uid;
        this.email = email;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.avatar = avatar;
        this.vaiTro = vaiTro;
        this.locked = locked;
        this.active = active;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
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

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @PropertyName("isLocked")
    public void setLegacyIsLocked(Boolean legacyLocked) {
        if (legacyLocked != null && this.locked == null) {
            this.locked = legacyLocked;
        }
    }

    @PropertyName("isActive")
    public void setLegacyIsActive(Boolean legacyActive) {
        if (legacyActive != null && this.active == null) {
            this.active = legacyActive;
        }
    }

    @PropertyName("isDeleted")
    public void setLegacyIsDeleted(Boolean legacyDeleted) {
        if (legacyDeleted != null && this.deleted == null) {
            this.deleted = legacyDeleted;
        }
    }

    @Exclude
    public boolean isAccountLocked() {
        return Boolean.TRUE.equals(locked);
    }

    @Exclude
    public boolean isAccountActive() {
        return active == null || active;
    }

    @Exclude
    public boolean isAccountDeleted() {
        return Boolean.TRUE.equals(deleted);
    }
}