package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.SanPham;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SanPhamDB {
    private final HeThongBanGiayDBHelper helper;
    final SQLiteDatabase db;

    //Key để sort
    public static final String SORT_SP_THEM_VAO_MOI_NHAT = "new_recent";
    public static final String SORT_SP_BAN_CHAY = "popular";
    public static final String SORT_GIA_CAO_NHAT = "price_high";
    public static final String SORT_GIA_THAP_NHAT = "price_low";
    public static final String SORT_XEP_HANG = "rating";

    public SanPhamDB(Context context) {
        helper = new HeThongBanGiayDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void themSanPham(String id, String danhMucId, String ten, double donGia,
                             String anh, String moTa, String gioiTinh,
                             double diemDanhGia, int luotBan,
                             String ngayTao, String ngayCapNhat, int active) {
        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.SP_ID, id);
        values.put(HeThongBanGiayDBHelper.SP_DANH_MUC_ID, danhMucId);
        values.put(HeThongBanGiayDBHelper.SP_TEN, ten);
        values.put(HeThongBanGiayDBHelper.SP_DON_GIA, donGia);
        values.put(HeThongBanGiayDBHelper.SP_ANH, anh);
        values.put(HeThongBanGiayDBHelper.SP_MO_TA, moTa);
        values.put(HeThongBanGiayDBHelper.SP_DIEM_DANH_GIA, diemDanhGia);
        values.put(HeThongBanGiayDBHelper.SP_LUOT_BAN, luotBan);
        values.put(HeThongBanGiayDBHelper.SP_NGAY_TAO, ngayTao);
        values.put(HeThongBanGiayDBHelper.SP_NGAY_CAP_NHAT, ngayCapNhat);
        values.put(HeThongBanGiayDBHelper.SP_ACTIVE, active);
        db.insert(HeThongBanGiayDBHelper.BANG_SAN_PHAM, null, values);
    }

    public List<SanPham> layTatCaSpDangActive() {
        return timKiemSanPham("", "", 0, 0, 0, SORT_SP_THEM_VAO_MOI_NHAT);
    }

    private SanPham traVe1Sp(Cursor cursor) {
        SanPham sp = new SanPham();
        sp.setSanPhamId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ID)));
        sp.setDanhMucId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DANH_MUC_ID)));
        sp.setTenSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_TEN)));
        sp.setDonGia(cursor.getDouble(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DON_GIA)));
        sp.setAnhSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ANH)));
        sp.setMoTaSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_MO_TA)));
        sp.setDiemDanhGia(cursor.getDouble(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DIEM_DANH_GIA)));
        sp.setLuotBan(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_LUOT_BAN)));
        sp.setNgayTao(null);
        sp.setNgayCapNhat(null);
        sp.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ACTIVE)) == 1);
        return sp;
    }

    private String kieuSapXep(String sortBy) {
        if(SORT_SP_THEM_VAO_MOI_NHAT.equals(sortBy)) {
            return HeThongBanGiayDBHelper.SP_NGAY_TAO + " DESC";
        }
        if(SORT_SP_BAN_CHAY.equals(sortBy)) {
            return HeThongBanGiayDBHelper.SP_LUOT_BAN + " DESC";
        }
        if(SORT_GIA_CAO_NHAT.equals(sortBy)) {
            return HeThongBanGiayDBHelper.SP_DON_GIA + " ASC";
        }
        if(SORT_GIA_THAP_NHAT.equals(sortBy)) {
            return HeThongBanGiayDBHelper.SP_DON_GIA + " DESC";
        }
        if(SORT_XEP_HANG.equals(sortBy)) {
            return HeThongBanGiayDBHelper.SP_DIEM_DANH_GIA +  " DESC";
        }
        return null;
    }

    public double layGiaMax() {
        double maxGia = 0;
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + HeThongBanGiayDBHelper.SP_DON_GIA + ") " +
                "FROM " + HeThongBanGiayDBHelper.BANG_SAN_PHAM + " " +
                "WHERE " + HeThongBanGiayDBHelper.SP_ACTIVE + " = 1",
                null
        );

        if(cursor.moveToFirst()) {
            maxGia = cursor.getDouble(0);
        }

        cursor.close();
        return maxGia;
    }

    public List<SanPham> timKiemSanPham(String tuKhoa,
                                        String danhMucId,
                                        double giaMin,
                                        double giaMax,
                                        double diemDanhGiaMin,
                                        String sortBy) {

        List<SanPham> data = new ArrayList<>();
        StringBuilder selection = new StringBuilder(HeThongBanGiayDBHelper.SP_ACTIVE + " = ?");
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("1");

        if (danhMucId != null && !danhMucId.trim().isEmpty()) {
            selection.append(" AND ").append(HeThongBanGiayDBHelper.SP_DANH_MUC_ID).append(" = ?");
            selectionArgs.add(danhMucId.trim());
        }

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            selection.append(" AND (")
                    .append(HeThongBanGiayDBHelper.SP_TEN).append(" LIKE ? OR ")
                    .append(HeThongBanGiayDBHelper.SP_MO_TA).append(" LIKE ?)");
            String keyword = "%" + tuKhoa.trim() + "%";
            selectionArgs.add(keyword);
            selectionArgs.add(keyword);
        }

        if (giaMin > 0 || giaMax > 0) {
            selection.append(" AND ").append(HeThongBanGiayDBHelper.SP_DON_GIA).append(" BETWEEN ? AND ?");
            selectionArgs.add(String.valueOf(giaMin));
            selectionArgs.add(String.valueOf(giaMax <= 0 ? Double.MAX_VALUE : giaMax));
        }

        if (diemDanhGiaMin > 0) {
            selection.append(" AND ").append(HeThongBanGiayDBHelper.SP_DIEM_DANH_GIA).append(" >= ?");
            selectionArgs.add(String.valueOf(diemDanhGiaMin));
        }

        String orderBy = kieuSapXep(sortBy);

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_SAN_PHAM,
                null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null,
                null,
                orderBy
        );

        if (cursor.moveToFirst()) {
            do {
                data.add(traVe1Sp(cursor));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

    public long insertSanPham(SanPham sp) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sanPhamId", sp.getSanPhamId());
        values.put("danhMucId", sp.getDanhMucId());
        values.put("tenSanPham", sp.getTenSanPham());
        values.put("donGia", sp.getDonGia());
        values.put("anhSanPham", sp.getAnhSanPham());
        values.put("moTaSanPham", sp.getMoTaSanPham());
        values.put("diemDanhGia", sp.getDiemDanhGia());
        values.put("luotBan", sp.getLuotBan());
        values.put("ngayTao", "null");
        values.put("ngayCapNhat", "null");
        values.put("active", sp.isActive() ? 1 : 0);

        return db.insert("SanPham", null, values);
    }
    public void insertSampleSanPham() {
        SQLiteDatabase db = helper.getWritableDatabase();

        // SP 1
        ContentValues sp1 = new ContentValues();
        sp1.put("sanPhamId", "SP1");
        sp1.put("danhMucId", "DM01");
        sp1.put("tenSanPham", "Nike Air Force 1");
        sp1.put("donGia", 2500000);
        sp1.put("anhSanPham", "shoes");
        sp1.put("moTaSanPham", "Giày Nike basic");
        sp1.put("diemDanhGia", 4.8);
        sp1.put("luotBan", 120);
        sp1.put("ngayTao", "2026-04-15 10:00:00");
        sp1.put("ngayCapNhat", "2026-04-15 10:00:00");
        sp1.put("active", 1);
        db.insert("SanPham", null, sp1);

        // SP 2
        ContentValues sp2 = new ContentValues();
        sp2.put("sanPhamId", "SP2");
        sp2.put("danhMucId", "DM01");
        sp2.put("tenSanPham", "Nike Jordan 1");
        sp2.put("donGia", 3200000);
        sp2.put("anhSanPham", "jordan1");
        sp2.put("moTaSanPham", "Giày bóng rổ Jordan");
        sp2.put("diemDanhGia", 4.9);
        sp2.put("luotBan", 200);
        sp2.put("ngayTao", "2026-04-15 10:00:00");
        sp2.put("ngayCapNhat", "2026-04-15 10:00:00");
        sp2.put("active", 1);
        db.insert("SanPham", null, sp2);

        // SP 3
        ContentValues sp3 = new ContentValues();
        sp3.put("sanPhamId", "SP3");
        sp3.put("danhMucId", "DM02");
        sp3.put("tenSanPham", "Adidas Superstar");
        sp3.put("donGia", 1800000);
        sp3.put("anhSanPham", "superstar");
        sp3.put("moTaSanPham", "Giày Adidas cổ điển");
        sp3.put("diemDanhGia", 4.6);
        sp3.put("luotBan", 90);
        sp3.put("ngayTao", "2026-04-15 10:00:00");
        sp3.put("ngayCapNhat", "2026-04-15 10:00:00");
        sp3.put("active", 1);
        db.insert("SanPham", null, sp3);

        // SP 4
        ContentValues sp4 = new ContentValues();
        sp4.put("sanPhamId", "SP4");
        sp4.put("danhMucId", "DM02");
        sp4.put("tenSanPham", "Adidas Ultraboost");
        sp4.put("donGia", 3500000);
        sp4.put("anhSanPham", "ultraboost");
        sp4.put("moTaSanPham", "Giày chạy bộ cao cấp");
        sp4.put("diemDanhGia", 4.7);
        sp4.put("luotBan", 150);
        sp4.put("ngayTao", "2026-04-15 10:00:00");
        sp4.put("ngayCapNhat", "2026-04-15 10:00:00");
        sp4.put("active", 1);
        db.insert("SanPham", null, sp4);

        // SP 5
        ContentValues sp5 = new ContentValues();
        sp5.put("sanPhamId", "SP5");
        sp5.put("danhMucId", "DM03");
        sp5.put("tenSanPham", "Puma RS-X");
        sp5.put("donGia", 2100000);
        sp5.put("anhSanPham", "puma");
        sp5.put("moTaSanPham", "Giày Puma streetwear");
        sp5.put("diemDanhGia", 4.5);
        sp5.put("luotBan", 80);
        sp5.put("ngayTao", "2026-04-15 10:00:00");
        sp5.put("ngayCapNhat", "2026-04-15 10:00:00");
        sp5.put("active", 1);
        db.insert("SanPham", null, sp5);
    }

    public List<SanPham> getSanPhamByDanhMuc(String danhMucId) {

        List<SanPham> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM SanPham WHERE danhMucId=? AND active=1",
                new String[]{danhMucId}
        );

        if (c.moveToFirst()) {
            do {
                SanPham sp = new SanPham();

                sp.setSanPhamId(c.getString(c.getColumnIndexOrThrow("sanPhamId")));
                sp.setDanhMucId(c.getString(c.getColumnIndexOrThrow("danhMucId")));
                sp.setTenSanPham(c.getString(c.getColumnIndexOrThrow("tenSanPham")));
                sp.setDonGia(c.getDouble(c.getColumnIndexOrThrow("donGia")));
                sp.setAnhSanPham(c.getString(c.getColumnIndexOrThrow("anhSanPham")));
                sp.setMoTaSanPham(c.getString(c.getColumnIndexOrThrow("moTaSanPham")));
                sp.setDiemDanhGia(c.getDouble(c.getColumnIndexOrThrow("diemDanhGia")));
                sp.setLuotBan(c.getInt(c.getColumnIndexOrThrow("luotBan")));
                sp.setActive(c.getInt(c.getColumnIndexOrThrow("active")) == 1);

                list.add(sp);

            } while (c.moveToNext());

            c.close();
        }

        return list;
    }
    public SanPham getSanPhamById(String id) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM SanPham WHERE sanPhamId=?",
                new String[]{id});

        if (c.moveToFirst()) {
            SanPham sp = new SanPham();

            sp.setSanPhamId(c.getString(0));
            sp.setDanhMucId(c.getString(1));
            sp.setTenSanPham(c.getString(2));
            sp.setDonGia(c.getDouble(3));
            sp.setAnhSanPham(c.getString(4));
            sp.setMoTaSanPham(c.getString(5));

            c.close();
            return sp;
        }

        return null;
    }
    public void updateSanPham(SanPham sp) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tenSanPham", sp.getTenSanPham());
        values.put("donGia", sp.getDonGia());
        values.put("moTaSanPham", sp.getMoTaSanPham());

        db.update("SanPham", values,
                "sanPhamId=?",
                new String[]{sp.getSanPhamId()});
    }

}
