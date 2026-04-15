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

    public ArrayList<DanhMuc> getAllDM() {
        ArrayList<DanhMuc> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_DANH_MUC,
                null,
                null,
                null,
                null,
                null,
                HeThongBanGiayDBHelper.DM_TEN + " ASC");

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
    public void themDMtest() {
        SQLiteDatabase db = helper.getWritableDatabase();

        themDanhMuc("DM01", "Giày thể thao", "Chạy bộ năng động", "shoes", 1);
        themDanhMuc("DM02", "Giày da", "Phong cách lịch lãm", "shoes", 1);
        themDanhMuc("DM03", "Sandal", "Thoáng mát mùa hè", "shoes", 1);
        themDanhMuc("DM04", "Boot", "Cá tính mạnh mẽ", "shoes", 1);
        themDanhMuc("DM05", "Giày lười", "Tiện lợi hàng ngày", "shoes", 1);

        db.close();
    }

    public void themDM(SQLiteDatabase db, String id, String ten, String mota, String anh) {
        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DM_ID, id);
        values.put(HeThongBanGiayDBHelper.DM_TEN, ten);
        values.put(HeThongBanGiayDBHelper.DM_MO_TA, mota);
        values.put(HeThongBanGiayDBHelper.DM_ANH, anh);
        values.put(HeThongBanGiayDBHelper.DM_ACTIVE, 1);

        db.insert(HeThongBanGiayDBHelper.BANG_DANH_MUC, null, values);
    }

    public int updateDanhMuc(DanhMuc dm) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DM_TEN, dm.getTenDanhMuc());
        values.put(HeThongBanGiayDBHelper.DM_MO_TA, dm.getMoTaDanhMuc());
        values.put(HeThongBanGiayDBHelper.DM_ANH, dm.getAnhDanhMuc());

        int result = db.update(
                HeThongBanGiayDBHelper.BANG_DANH_MUC,
                values,
                HeThongBanGiayDBHelper.DM_ID + "=?",
                new String[]{dm.getDanhMucId()}
        );

        db.close();
        return result;
    }
    public String generateNewId() {
        String prefix = "DM";
        String newId = "DM01";

        Cursor cursor = db.rawQuery(
                "SELECT " + HeThongBanGiayDBHelper.DM_ID +
                        " FROM " + HeThongBanGiayDBHelper.BANG_DANH_MUC +
                        " ORDER BY " + HeThongBanGiayDBHelper.DM_ID + " DESC LIMIT 1",
                null
        );

        if (cursor.moveToFirst()) {
            String lastId = cursor.getString(0); // ví dụ: DM05
            int number = Integer.parseInt(lastId.replace(prefix, ""));
            number++;

            newId = String.format("DM%02d", number); // DM06
        }

        cursor.close();
        return newId;
    }
    public long insertDanhMuc(DanhMuc dm) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DM_ID, dm.getDanhMucId());
        values.put(HeThongBanGiayDBHelper.DM_TEN, dm.getTenDanhMuc());
        values.put(HeThongBanGiayDBHelper.DM_MO_TA, dm.getMoTaDanhMuc());
        values.put(HeThongBanGiayDBHelper.DM_ANH, dm.getAnhDanhMuc());
        values.put(HeThongBanGiayDBHelper.DM_ACTIVE, dm.isActive() ? 1 : 0);

        long result = db.insert(HeThongBanGiayDBHelper.BANG_DANH_MUC, null, values);

        db.close();
        return result;
    }
}
