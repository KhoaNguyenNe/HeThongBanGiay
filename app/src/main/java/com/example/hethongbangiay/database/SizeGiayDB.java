package com.example.hethongbangiay.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.SizeGiay;

import java.util.ArrayList;
import java.util.List;

public class SizeGiayDB {
    private final HeThongBanGiayDBHelper helper;
    final SQLiteDatabase db;

    public SizeGiayDB(Context context) {
        helper = new HeThongBanGiayDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public List<SizeGiay> laySizeTheoSanPhamId(String spId) {
        List<SizeGiay> list = new ArrayList<>();

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_SIZE_GIAY,
                null,
                HeThongBanGiayDBHelper.SG_SAN_PHAM_ID + "=?",
                new String[]{spId},
                null,
                null,
                HeThongBanGiayDBHelper.SG_SIZE + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                SizeGiay sizeGiay = new SizeGiay();
                sizeGiay.setSizeGiayId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SG_ID)));
                sizeGiay.setSanPhamId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SG_SAN_PHAM_ID)));
                sizeGiay.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SG_SIZE)));
                sizeGiay.setSoLuong(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SG_SO_LUONG)));
                list.add(sizeGiay);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
