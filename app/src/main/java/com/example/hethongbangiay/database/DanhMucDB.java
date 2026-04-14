package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.DanhMuc;

import java.util.ArrayList;
import java.util.List;

public class DanhMucDB {
    private final HeThongBanGiayDBHelper helper;
    final SQLiteDatabase db;

    public DanhMucDB(Context context) {
        helper = new HeThongBanGiayDBHelper(context);
        db = helper.getWritableDatabase();
    }

    private void themDanhMuc(String id, String ten, String moTa, String anh, int active) {
        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DM_ID, id);
        values.put(HeThongBanGiayDBHelper.DM_TEN, ten);
        values.put(HeThongBanGiayDBHelper.DM_MO_TA, moTa);
        values.put(HeThongBanGiayDBHelper.DM_ANH, anh);
        values.put(HeThongBanGiayDBHelper.DM_ACTIVE, active);
        db.insert(HeThongBanGiayDBHelper.BANG_DANH_MUC, null, values);
    }

    public List<DanhMuc> layTatCaDMActive() {
        List<DanhMuc> data = new ArrayList<>();

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_DANH_MUC,
                null,
                HeThongBanGiayDBHelper.SP_ACTIVE + "=?",
                new String[]{"1"},
                null,
                null,
                HeThongBanGiayDBHelper.DM_TEN + " ASC"
        );

        if(cursor.moveToFirst()) {
            do {
                DanhMuc dm = new DanhMuc();
                dm.setDanhMucId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DM_ID)));
                dm.setTenDanhMuc(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DM_TEN)));
                dm.setMoTaDanhMuc(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DM_MO_TA)));
                dm.setAnhDanhMuc(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DM_ANH)));
                dm.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.DM_ACTIVE)) == 1);
                data.add(dm);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }
}
