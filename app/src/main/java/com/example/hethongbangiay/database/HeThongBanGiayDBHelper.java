package com.example.hethongbangiay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeThongBanGiayDBHelper extends SQLiteOpenHelper {

    public static final String TEN_DATABASE = "HeThongBanGiay.db";
    private static final int DATABASE_VERSION = 6;

    //Giỏ hàng
    public static final String BANG_GIO_HANG = "GioHang";
    public static final String GH_ID = "gioHangId";
    public static final String GH_TEN_SAN_PHAM = "tenSanPham";
    public static final String GH_DON_GIA = "donGia";
    public static final String GH_GIA_TIEN = "giaTien";
    public static final String GH_SIZE_GIAY = "sizeGiay";
    public static final String GH_MAU_SAC = "mauSac";
    public static final String GH_SO_LUONG = "soLuong";
    public static final String GH_ANH_SAN_PHAM = "anhSanPham";
    public static final String GH_SAN_PHAM_ID = "sanPhamId";

    //Tạo bảng giỏ hàng
    private static final String TAO_BANG_GIO_HANG =
            "CREATE TABLE " + BANG_GIO_HANG + " ("
                    + GH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GH_SAN_PHAM_ID + " TEXT, "
                    + GH_TEN_SAN_PHAM + " TEXT NOT NULL, "
                    + GH_DON_GIA + " INTEGER NOT NULL, "
                    + GH_GIA_TIEN + " INTEGER NOT NULL, "
                    + GH_SIZE_GIAY + " INTEGER NOT NULL, "
                    + GH_MAU_SAC + " TEXT, "
                    + GH_SO_LUONG + " INTEGER NOT NULL, "
                    + GH_ANH_SAN_PHAM + " TEXT)";

    public HeThongBanGiayDBHelper(Context context) {
        super(context, TEN_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TAO_BANG_GIO_HANG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BANG_GIO_HANG);
        onCreate(sqLiteDatabase);
    }
}
