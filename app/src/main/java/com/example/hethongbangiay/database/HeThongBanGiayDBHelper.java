package com.example.hethongbangiay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeThongBanGiayDBHelper extends SQLiteOpenHelper {

    public static final String TEN_DATABASE = "HeThongBanGiay.db";
    private static final int DATABASE_VERSION = 2;

    //Danh mục
    public static final String BANG_DANH_MUC = "DanhMuc";
    public static final String DM_ID = "danhMucId";
    public static final String DM_TEN = "tenDanhMuc";
    public static final String DM_MO_TA = "moTaDanhMuc";
    public static final String DM_ANH = "anhDanhMuc";
    public static final String DM_ACTIVE = "active";

    //Sản phẩm
    public static final String BANG_SAN_PHAM = "SanPham";
    public static final String SP_ID = "sanPhamId";
    public static final String SP_DANH_MUC_ID = "danhMucId";
    public static final String SP_TEN = "tenSanPham";
    public static final String SP_DON_GIA = "donGia";
    public static final String SP_ANH = "anhSanPham";
    public static final String SP_MO_TA = "moTaSanPham";
    public static final String SP_DIEM_DANH_GIA = "diemDanhGia";
    public static final String SP_LUOT_BAN = "luotBan";
    public static final String SP_NGAY_TAO = "ngayTao";
    public static final String SP_NGAY_CAP_NHAT = "ngayCapNhat";
    public static final String SP_ACTIVE = "active";

    //Size giày
    public static final String BANG_SIZE_GIAY = "SizeGiay";
    public static final String SG_ID = "sizeGiayId";
    public static final String SG_SAN_PHAM_ID = "sanPhamId";
    public static final String SG_SIZE = "size";
    public static final String SG_SO_LUONG = "soLuong";

    //Tạo bảng Danh Mục
    private static final String TAO_BANG_DANH_MUC =
            "CREATE TABLE " + BANG_DANH_MUC + " ("
                    + DM_ID + " TEXT PRIMARY KEY, "
                    + DM_TEN + " TEXT NOT NULL, "
                    + DM_MO_TA + " TEXT, "
                    + DM_ANH + " TEXT, "
                    + DM_ACTIVE + " INTEGER NOT NULL)";

    //Tạo bảng Sản phẩm
    private static final String TAO_BANG_SAN_PHAM =
            "CREATE TABLE " + BANG_SAN_PHAM + " ("
                    + SP_ID + " TEXT PRIMARY KEY, "
                    + SP_DANH_MUC_ID + " TEXT NOT NULL, "
                    + SP_TEN + " TEXT NOT NULL, "
                    + SP_DON_GIA + " FLOAT NOT NULL, "
                    + SP_ANH + " TEXT, "
                    + SP_MO_TA + " TEXT, "
                    + SP_DIEM_DANH_GIA + " FLOAT DEFAULT 0, "
                    + SP_LUOT_BAN + " INTEGER DEFAULT 0, "
                    + SP_NGAY_TAO + " TIMESTAMP, "
                    + SP_NGAY_CAP_NHAT + " TIMESTAMP, "
                    + SP_ACTIVE + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + SP_DANH_MUC_ID + ") REFERENCES "
                    + BANG_DANH_MUC + "(" + DM_ID + "))";

    //Tạo bảng Size giày
    private static final String TAO_BANG_SIZE_GIAY =
            "CREATE TABLE " + BANG_SIZE_GIAY + " ("
                    + SG_ID + " TEXT PRIMARY KEY, "
                    + SG_SAN_PHAM_ID + " TEXT NOT NULL, "
                    + SG_SIZE + " INTEGER NOT NULL, "
                    + SG_SO_LUONG + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + SG_SAN_PHAM_ID + ") REFERENCES "
                    + BANG_SAN_PHAM + "(" + SP_ID + "))";

    public HeThongBanGiayDBHelper(Context context) {
        super(context, TEN_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TAO_BANG_DANH_MUC);
        sqLiteDatabase.execSQL(TAO_BANG_SAN_PHAM);
        sqLiteDatabase.execSQL(TAO_BANG_SIZE_GIAY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BANG_DANH_MUC);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BANG_SAN_PHAM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BANG_SIZE_GIAY);
        onCreate(sqLiteDatabase);
    }
}
