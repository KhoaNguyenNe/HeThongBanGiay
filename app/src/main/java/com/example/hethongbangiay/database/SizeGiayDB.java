package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.SizeGiay;

import java.util.ArrayList;
import java.util.List;

public class SizeGiayDB {

    private HeThongBanGiayDBHelper helper;

    public SizeGiayDB(Context context) {
        helper = new HeThongBanGiayDBHelper(context);
    }

    public String generateSizeGiayId() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;

        String newId = "SZ01";

        try {
            cursor = db.rawQuery(
                    "SELECT " + helper.SG_ID +
                            " FROM " + helper.BANG_SIZE_GIAY +
                            " ORDER BY " + helper.SG_ID + " DESC LIMIT 1",
                    null
            );

            if (cursor.moveToFirst()) {
                String lastId = cursor.getString(0); // ví dụ SZ09
                int num = Integer.parseInt(lastId.replace("SZ", ""));
                num++;
                newId = String.format("SZ%02d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return newId;
    }

    public long insertSizeGiay(SizeGiay sizeGiay) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(helper.SG_ID, sizeGiay.getSizeGiayId());
        values.put(helper.SG_SAN_PHAM_ID, sizeGiay.getSanPhamId());
        values.put(helper.SG_SIZE, sizeGiay.getSize());
        values.put(helper.SG_SO_LUONG, sizeGiay.getSoLuong());

        return db.insert(helper.BANG_SIZE_GIAY, null, values);
    }

    public int updateSizeGiay(SizeGiay sizeGiay) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(helper.SG_SAN_PHAM_ID, sizeGiay.getSanPhamId());
        values.put(helper.SG_SIZE, sizeGiay.getSize());
        values.put(helper.SG_SO_LUONG, sizeGiay.getSoLuong());

        return db.update(
                helper.BANG_SIZE_GIAY,
                values,
                helper.SG_ID + "=?",
                new String[]{sizeGiay.getSizeGiayId()}
        );
    }

    public int deleteSizeGiay(String sizeGiayId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        return db.delete(
                helper.BANG_SIZE_GIAY,
                helper.SG_ID + "=?",
                new String[]{sizeGiayId}
        );
    }

    public int deleteBySanPhamId(String sanPhamId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        return db.delete(
                helper.BANG_SIZE_GIAY,
                helper.SG_SAN_PHAM_ID + "=?",
                new String[]{sanPhamId}
        );
    }

    public List<SizeGiay> getBySanPhamId(String sanPhamId) {
        List<SizeGiay> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + helper.BANG_SIZE_GIAY +
                            " WHERE " + helper.SG_SAN_PHAM_ID + "=?",
                    new String[]{sanPhamId}
            );

            if (cursor.moveToFirst()) {
                do {
                    SizeGiay sg = new SizeGiay();
                    sg.setSizeGiayId(cursor.getString(0));
                    sg.setSanPhamId(cursor.getString(1));
                    sg.setSize(cursor.getInt(2));
                    sg.setSoLuong(cursor.getInt(3));
                    list.add(sg);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    public List<SizeGiay> laySizeTheoSanPhamId(String sanPhamId) {
        return getBySanPhamId(sanPhamId);
    }
}