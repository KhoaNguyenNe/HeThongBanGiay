package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DemoDataSeeder {

    private final HeThongBanGiayDBHelper dbHelper;

    public DemoDataSeeder(Context context) {
        dbHelper = new HeThongBanGiayDBHelper(context);
    }

    public void seedIfNeeded() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (hasAnyProduct(db)) {
            return;
        }

        db.beginTransaction();
        try {
            seedCategories(db);
            seedProducts(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void reseedAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(HeThongBanGiayDBHelper.BANG_SIZE_GIAY, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_SAN_PHAM, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_DANH_MUC, null, null);

            seedCategories(db);
            seedProducts(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private boolean hasAnyProduct(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + HeThongBanGiayDBHelper.BANG_SAN_PHAM,
                null
        );

        boolean hasData = false;
        if (cursor.moveToFirst()) {
            hasData = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasData;
    }

    private void seedCategories(SQLiteDatabase db) {
        insertCategory(db, "DM01", "Nike", "Giày thể thao Nike", "shoes", 1);
        insertCategory(db, "DM02", "Adidas", "Giày thể thao Adidas", "shoes", 1);
        insertCategory(db, "DM03", "Puma", "Giày thể thao Puma", "shoes", 1);
        insertCategory(db, "DM04", "Asics", "Giày thể thao Asics", "shoes", 1);
        insertCategory(db, "DM05", "New Balance", "Giày thể thao New Balance", "shoes", 1);
        insertCategory(db, "DM06", "Converse", "Giày sneaker Converse", "shoes", 1);
    }

    private void seedProducts(SQLiteDatabase db) {
        insertProduct(db, "SP01", "DM01", "Nike Running Pegasus 40", 3290000, "shoes",
                "Giày chạy bộ êm nhẹ cho chạy hàng ngày",  4.8, 12450,
                "2026-04-13 08:00:00", "2026-04-13 08:00:00", 1);

        insertProduct(db, "SP02", "DM01", "Nike Running Revolution 7", 1890000, "shoes",
                "Mẫu running cơ bản, bền và dễ phối đồ",  4.5, 8240,
                "2026-04-13 08:05:00", "2026-04-13 08:05:00", 1);

        insertProduct(db, "SP03", "DM01", "Nike Air Force 1 '07", 2790000, "shoes",
                "Sneaker lifestyle cổ điển, phù hợp đi học và đi chơi", 4.9, 15780,
                "2026-04-13 08:10:00", "2026-04-13 08:10:00", 1);

        insertProduct(db, "SP04", "DM02", "Adidas Running Ultraboost Light", 4190000, "shoes",
                "Giày running cao cấp, đệm êm và hoàn trả lực tốt", 4.8, 6350,
                "2026-04-13 08:15:00", "2026-04-13 08:15:00", 1);

        insertProduct(db, "SP05", "DM02", "Adidas Running Adizero SL", 2990000, "shoes",
                "Giày chạy tốc độ dành cho luyện tập tempo",  4.6, 5920,
                "2026-04-13 08:20:00", "2026-04-13 08:20:00", 1);

        insertProduct(db, "SP06", "DM02", "Adidas Forum Low", 2490000, "shoes",
                "Sneaker cổ thấp phong cách retro basketball",  4.4, 4680,
                "2026-04-13 08:25:00", "2026-04-13 08:25:00", 1);

        insertProduct(db, "SP07", "DM03", "Puma Running Velocity Nitro 3", 2890000, "shoes",
                "Running shoe đệm êm, phù hợp cự ly ngắn và trung bình", 4.7, 5130,
                "2026-04-13 08:30:00", "2026-04-13 08:30:00", 1);

        insertProduct(db, "SP08", "DM03", "Puma Running Deviate Nitro 2", 3790000, "shoes",
                "Giày chạy hiệu năng cao với tấm đẩy lực",  4.8, 3310,
                "2026-04-13 08:35:00", "2026-04-13 08:35:00", 1);

        insertProduct(db, "SP09", "DM03", "Puma Suede Classic XXI", 1990000, "shoes",
                "Mẫu sneaker cổ điển cho phong cách casual",  4.3, 7210,
                "2026-04-13 08:40:00", "2026-04-13 08:40:00", 1);

        insertProduct(db, "SP10", "DM04", "Asics Running Gel-Kayano 30", 4390000, "shoes",
                "Giày chạy ổn định, phù hợp runner cần hỗ trợ bàn chân",  4.9, 2860,
                "2026-04-13 08:45:00", "2026-04-13 08:45:00", 1);

        insertProduct(db, "SP11", "DM04", "Asics Running Novablast 4", 3690000, "shoes",
                "Giày chạy phản hồi tốt, cảm giác bật nảy", 4.7, 3540,
                "2026-04-13 08:50:00", "2026-04-13 08:50:00", 1);

        insertProduct(db, "SP12", "DM04", "Asics Gel-1130", 2590000, "shoes",
                "Sneaker lifestyle mang phong cách Y2K", 4.4, 6740,
                "2026-04-13 08:55:00", "2026-04-13 08:55:00", 1);

        insertProduct(db, "SP13", "DM05", "New Balance Running 1080v13", 4090000, "shoes",
                "Running shoe premium với đệm êm cho daily run", 4.8, 2980,
                "2026-04-13 09:00:00", "2026-04-13 09:00:00", 1);

        insertProduct(db, "SP14", "DM05", "New Balance FuelCell Rebel v4", 3390000, "shoes",
                "Giày chạy nhẹ, linh hoạt và giàu độ nảy",  4.6, 2450,
                "2026-04-13 09:05:00", "2026-04-13 09:05:00", 1);

        insertProduct(db, "SP15", "DM05", "New Balance 530", 2590000, "shoes",
                "Sneaker retro phù hợp outfit thường ngày",  4.5, 9120,
                "2026-04-13 09:10:00", "2026-04-13 09:10:00", 1);

        insertProduct(db, "SP16", "DM06", "Converse Chuck Taylor All Star", 1650000, "shoes",
                "Classic high top sneaker dễ phối với mọi outfit",  4.7, 18640,
                "2026-04-13 09:15:00", "2026-04-13 09:15:00", 1);

        insertProduct(db, "SP17", "DM06", "Converse Run Star Motion", 2890000, "shoes",
                "Chunky sneaker đế cao tạo điểm nhấn thời trang",  4.4, 4380,
                "2026-04-13 09:20:00", "2026-04-13 09:20:00", 1);

        insertProduct(db, "SP18", "DM06", "Converse Weapon Vintage", 3090000, "shoes",
                "Mẫu basketball retro phù hợp phong cách streetwear",  4.3, 2140,
                "2026-04-13 09:25:00", "2026-04-13 09:25:00", 1);
    }

    private void insertCategory(SQLiteDatabase db,
                                String id,
                                String ten,
                                String moTa,
                                String anh,
                                int active) {

        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DM_ID, id);
        values.put(HeThongBanGiayDBHelper.DM_TEN, ten);
        values.put(HeThongBanGiayDBHelper.DM_MO_TA, moTa);
        values.put(HeThongBanGiayDBHelper.DM_ANH, anh);
        values.put(HeThongBanGiayDBHelper.DM_ACTIVE, active);

        db.insertWithOnConflict(
                HeThongBanGiayDBHelper.BANG_DANH_MUC,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    private void insertProduct(SQLiteDatabase db,
                               String id,
                               String danhMucId,
                               String ten,
                               double donGia,
                               String anh,
                               String moTa,
                               double diemDanhGia,
                               int luotBan,
                               String ngayTao,
                               String ngayCapNhat,
                               int active) {

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

        db.insertWithOnConflict(
                HeThongBanGiayDBHelper.BANG_SAN_PHAM,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }
}
