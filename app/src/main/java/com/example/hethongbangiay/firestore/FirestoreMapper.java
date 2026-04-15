package com.example.hethongbangiay.firestore;

import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirestoreMapper {

    private FirestoreMapper() {
    }

    public static DanhMuc toDanhMuc(DocumentSnapshot doc) {
        DanhMuc dm = new DanhMuc();
        dm.setDanhMucId(getString(doc, "danhMucId", doc.getId()));
        dm.setTenDanhMuc(getString(doc, "tenDanhMuc", ""));
        dm.setMoTaDanhMuc(getString(doc, "moTaDanhMuc", ""));
        dm.setAnhDanhMuc(getString(doc, "anhDanhMuc", ""));
        dm.setActive(getBoolean(doc, "active", true));
        return dm;
    }

    public static SanPham toSanPham(DocumentSnapshot doc) {
        SanPham sp = new SanPham();
        sp.setSanPhamId(getString(doc, "sanPhamId", doc.getId()));
        sp.setDanhMucId(getString(doc, "danhMucId", ""));
        sp.setTenSanPham(getString(doc, "tenSanPham", ""));
        sp.setDonGia(getDouble(doc, "donGia", 0d));
        sp.setAnhSanPham(getString(doc, "anhSanPham", ""));
        sp.setMoTaSanPham(getString(doc, "moTaSanPham", ""));
        sp.setDiemDanhGia(getDouble(doc, "diemDanhGia", 0d));
        sp.setLuotBan(getInt(doc, "luotBan", 0));
        sp.setActive(getBoolean(doc, "active", true));

        // Model hiện tại dùng com.google.type.DateTime, không map trực tiếp từ Firestore
        sp.setNgayTao(null);
        sp.setNgayCapNhat(null);

        return sp;
    }

    public static SizeGiay toSizeGiay(DocumentSnapshot doc, String sanPhamId) {
        SizeGiay size = new SizeGiay();
        size.setSizeGiayId(getString(doc, "sizeGiayId", doc.getId()));
        size.setSanPhamId(sanPhamId);
        size.setSize(getInt(doc, "size", 0));
        size.setSoLuong(getInt(doc, "soLuong", 0));
        return size;
    }

    public static DanhGia toDanhGia(DocumentSnapshot doc, String sanPhamId) {
        DanhGia dg = new DanhGia();
        dg.setDanhGiaId(getString(doc, "danhGiaId", doc.getId()));
        dg.setNguoiDungId(getString(doc, "nguoiDungId", ""));
        dg.setSanPhamId(sanPhamId);
        dg.setRating(getInt(doc, "rating", 0));
        dg.setComment(getString(doc, "comment", ""));
        dg.setNgayDanhGia(null);
        return dg;
    }

    private static String getString(DocumentSnapshot doc, String key, String defaultValue) {
        String value = doc.getString(key);
        return value == null ? defaultValue : value;
    }

    private static double getDouble(DocumentSnapshot doc, String key, double defaultValue) {
        Number value = doc.getDouble(key);
        return value == null ? defaultValue : value.doubleValue();
    }

    private static int getInt(DocumentSnapshot doc, String key, int defaultValue) {
        Long value = doc.getLong(key);
        return value == null ? defaultValue : value.intValue();
    }

    private static boolean getBoolean(DocumentSnapshot doc, String key, boolean defaultValue) {
        Boolean value = doc.getBoolean(key);
        return value == null ? defaultValue : value;
    }
}