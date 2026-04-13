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

    public SanPhamDB(Context context) {
        helper = new HeThongBanGiayDBHelper(context);
        db = helper.getWritableDatabase();
    }

    private void themSanPham(String id, String danhMucId, String ten, double donGia,
                             String anh, String moTa, String ngayTao,
                             String ngayCapNhat, int active) {
        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.SP_ID, id);
        values.put(HeThongBanGiayDBHelper.SP_DANH_MUC_ID, danhMucId);
        values.put(HeThongBanGiayDBHelper.SP_TEN, ten);
        values.put(HeThongBanGiayDBHelper.SP_DON_GIA, donGia);
        values.put(HeThongBanGiayDBHelper.SP_ANH, anh);
        values.put(HeThongBanGiayDBHelper.SP_MO_TA, moTa);
        values.put(HeThongBanGiayDBHelper.SP_NGAY_TAO, ngayTao);
        values.put(HeThongBanGiayDBHelper.SP_NGAY_CAP_NHAT, ngayCapNhat);
        values.put(HeThongBanGiayDBHelper.SP_ACTIVE, active);
        db.insert(HeThongBanGiayDBHelper.BANG_SAN_PHAM, null, values);
    }

    public List<SanPham> layTatCaSpDangActive() {
        List<SanPham> data = new ArrayList<>();

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_SAN_PHAM,
                null,
                HeThongBanGiayDBHelper.SP_ACTIVE + "=?",
                new String[]{"1"},
                null,
                null,
                HeThongBanGiayDBHelper.SP_NGAY_TAO + " DESC"
        );

        if(cursor.moveToFirst()) {
            do {
                SanPham sp = new SanPham();
                sp.setSanPhamId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ID)));
                sp.setDanhMucId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DANH_MUC_ID)));
                sp.setTenSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_TEN)));
                sp.setDonGia(cursor.getDouble(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DON_GIA)));
                sp.setAnhSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ANH)));
                sp.setMoTaSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_MO_TA)));
                sp.setNgayTao(null);
                sp.setNgayCapNhat(null);
                sp.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ACTIVE)) == 1);
                data.add(sp);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

//    public void taoDuLieuMau() {
//        Cursor cursor = db.rawQuery(
//                "SELECT COUNT(*) FROM " + HeThongBanGiayDBHelper.BANG_SAN_PHAM, null);
//
//        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
//            cursor.close();
//            return;
//        }
//        cursor.close();
//
//        themDanhMuc("DM01", "Nike", "Giay Nike", "shoes", 1);
//        themDanhMuc("DM02", "Adidas", "Giay Adidas", "shoes", 1);
//        themDanhMuc("DM03", "Puma", "Giay Puma", "shoes", 1);
//
//        themSanPham("SP01", "DM01", "Nike Air Zoom Pegasus", 2490000,
//                "shoes", "Giay chay bo em nhe",
//                "2026-04-13 08:00:00", "2026-04-13 08:00:00", 1);
//
//        themSanPham("SP02", "DM01", "Nike Revolution 7", 1890000,
//                "shoes", "Giay tap hang ngay",
//                "2026-04-13 08:10:00", "2026-04-13 08:10:00", 1);
//
//        themSanPham("SP03", "DM02", "Adidas Ultraboost Light", 3290000,
//                "shoes", "Giay chay bo cao cap",
//                "2026-04-13 08:20:00", "2026-04-13 08:20:00", 1);
//
//        themSanPham("SP04", "DM02", "Adidas Runfalcon", 1590000,
//                "shoes", "Giay the thao pho thong",
//                "2026-04-13 08:30:00", "2026-04-13 08:30:00", 1);
//
//        themSanPham("SP05", "DM03", "Puma Velocity Nitro", 2790000,
//                "shoes", "Giay chay bo dam chan",
//                "2026-04-13 08:40:00", "2026-04-13 08:40:00", 1);
//
//        themSanPham("SP06", "DM03", "Puma Smash 3.0", 1490000,
//                "shoes", "Giay sneaker co dien",
//                "2026-04-13 08:50:00", "2026-04-13 08:50:00", 1);
//
//        themSizeGiay("SZ01", "SP01", 39, 5);
//        themSizeGiay("SZ02", "SP01", 40, 8);
//        themSizeGiay("SZ03", "SP02", 40, 6);
//        themSizeGiay("SZ04", "SP03", 41, 4);
//        themSizeGiay("SZ05", "SP04", 42, 7);
//        themSizeGiay("SZ06", "SP05", 40, 3);
//        themSizeGiay("SZ07", "SP06", 39, 9);
//    }

}
