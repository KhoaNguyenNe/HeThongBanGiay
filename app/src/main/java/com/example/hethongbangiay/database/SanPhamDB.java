package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.SanPham;

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

}
