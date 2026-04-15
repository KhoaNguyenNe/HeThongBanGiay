package com.example.hethongbangiay.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.DanhGia;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaDB {
    private final SQLiteDatabase db;

    public DanhGiaDB(Context context) {
        db = new HeThongBanGiayDBHelper(context).getReadableDatabase();
    }

    public float layDiemTrungBinh(String sanPhamId) {
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(AVG(" + HeThongBanGiayDBHelper.DG_RATING + "), 0) " +
                        "FROM " + HeThongBanGiayDBHelper.BANG_DANH_GIA +
                        " WHERE " + HeThongBanGiayDBHelper.DG_SAN_PHAM_ID + "=?",
                new String[]{sanPhamId}
        );

        float diem = 0f;
        if (cursor.moveToFirst()) {
            diem = cursor.getFloat(0);
        }
        cursor.close();
        return diem;
    }

    public int demSoDanhGia(String sanPhamId) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + HeThongBanGiayDBHelper.BANG_DANH_GIA +
                        " WHERE " + HeThongBanGiayDBHelper.DG_SAN_PHAM_ID + "=?",
                new String[]{sanPhamId}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<DanhGia> layDanhGiaTheoSanPhamId(String sanPhamId) {
        List<DanhGia> list = new ArrayList<>();

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_DANH_GIA,
                null,
                HeThongBanGiayDBHelper.DG_SAN_PHAM_ID + "=?",
                new String[]{sanPhamId},
                null,
                null,
                HeThongBanGiayDBHelper.DG_NGAY_DANH_GIA + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                DanhGia danhGia = new DanhGia();
                danhGia.setDanhGiaId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DG_ID)));
                danhGia.setNguoiDungId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DG_NGUOI_DUNG_ID)));
                danhGia.setSanPhamId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DG_SAN_PHAM_ID)));
                danhGia.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DG_RATING)));
                danhGia.setComment(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DG_COMMENT)));
                list.add(danhGia);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
