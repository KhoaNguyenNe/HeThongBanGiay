package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DemoDataSeeder {

    private final HeThongBanGiayDBHelper dbHelper;

    public DemoDataSeeder(Context context) {
        dbHelper = new HeThongBanGiayDBHelper(context);
    }

    public void seedIfNeeded() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            if (!hasAnyData(db, HeThongBanGiayDBHelper.BANG_DANH_MUC)) {
                seedCategories(db);
            }
            if (!hasAnyData(db, HeThongBanGiayDBHelper.BANG_SAN_PHAM)) {
                seedProducts(db);
            }
            if (!hasAnyData(db, HeThongBanGiayDBHelper.BANG_SIZE_GIAY)) {
                seedSizes(db);
            }
            if (!hasAnyData(db, HeThongBanGiayDBHelper.BANG_DANH_GIA)) {
                seedReviews(db);
            }
            if (!hasAnyData(db, HeThongBanGiayDBHelper.BANG_GIO_HANG)) {
                seedCart(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        seedFirebaseAddressForCurrentUser();
    }

    public void reseedAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(HeThongBanGiayDBHelper.BANG_GIO_HANG, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_DANH_GIA, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_SIZE_GIAY, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_SAN_PHAM, null, null);
            db.delete(HeThongBanGiayDBHelper.BANG_DANH_MUC, null, null);

            seedCategories(db);
            seedProducts(db);
            seedSizes(db);
            seedReviews(db);
            seedCart(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        seedFirebaseAddressForCurrentUser();
    }

    private boolean hasAnyData(SQLiteDatabase db, String tenBang) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tenBang, null);

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

    private void seedSizes(SQLiteDatabase db) {
        String[] dsSp = {
                "SP01", "SP02", "SP03", "SP04", "SP05", "SP06",
                "SP07", "SP08", "SP09", "SP10", "SP11", "SP12",
                "SP13", "SP14", "SP15", "SP16", "SP17", "SP18"
        };

        int stt = 1;
        for (String sanPhamId : dsSp) {
            insertSize(db, "SZ" + stt++, sanPhamId, 40, 6);
            insertSize(db, "SZ" + stt++, sanPhamId, 41, 8);
            insertSize(db, "SZ" + stt++, sanPhamId, 42, 10);
            insertSize(db, "SZ" + stt++, sanPhamId, 43, 4);
        }
    }

    private void seedReviews(SQLiteDatabase db) {
        insertReview(db, "DG01", "user_demo_01", "SP01", 5, "Giày đi rất êm, chạy bộ ổn định và form đẹp.", "2026-04-13 10:00:00");
        insertReview(db, "DG02", "user_demo_02", "SP01", 4, "Đệm khá tốt, mang hằng ngày thoải mái.", "2026-04-13 11:30:00");
        insertReview(db, "DG03", "user_demo_03", "SP03", 5, "Air Force 1 phối đồ dễ, mang rất thích.", "2026-04-13 12:00:00");
        insertReview(db, "DG04", "user_demo_04", "SP06", 4, "Mẫu retro đẹp, chất liệu ổn trong tầm giá.", "2026-04-13 12:45:00");
        insertReview(db, "DG05", "user_demo_05", "SP10", 5, "Giày hỗ trợ bàn chân tốt, chạy lâu vẫn êm.", "2026-04-13 14:20:00");
        insertReview(db, "DG06", "user_demo_06", "SP18", 4, "Phong cách streetwear đẹp, lên chân nổi bật.", "2026-04-13 15:10:00");
        insertReview(db, "DG07", "user_demo_07", "SP18", 5, "Form đẹp, đi chơi rất hợp outfit.", "2026-04-13 16:30:00");
    }

    private void seedCart(SQLiteDatabase db) {
        insertCartFromProduct(db, "SP01", 41, 1);
        insertCartFromProduct(db, "SP03", 42, 2);
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

    private void insertSize(SQLiteDatabase db,
                            String sizeId,
                            String sanPhamId,
                            int size,
                            int soLuong) {

        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.SG_ID, sizeId);
        values.put(HeThongBanGiayDBHelper.SG_SAN_PHAM_ID, sanPhamId);
        values.put(HeThongBanGiayDBHelper.SG_SIZE, size);
        values.put(HeThongBanGiayDBHelper.SG_SO_LUONG, soLuong);

        db.insertWithOnConflict(
                HeThongBanGiayDBHelper.BANG_SIZE_GIAY,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    private void insertReview(SQLiteDatabase db,
                              String danhGiaId,
                              String nguoiDungId,
                              String sanPhamId,
                              int rating,
                              String comment,
                              String ngayDanhGia) {

        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.DG_ID, danhGiaId);
        values.put(HeThongBanGiayDBHelper.DG_NGUOI_DUNG_ID, nguoiDungId);
        values.put(HeThongBanGiayDBHelper.DG_SAN_PHAM_ID, sanPhamId);
        values.put(HeThongBanGiayDBHelper.DG_RATING, rating);
        values.put(HeThongBanGiayDBHelper.DG_COMMENT, comment);
        values.put(HeThongBanGiayDBHelper.DG_NGAY_DANH_GIA, ngayDanhGia);

        db.insertWithOnConflict(
                HeThongBanGiayDBHelper.BANG_DANH_GIA,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    private void insertCartFromProduct(SQLiteDatabase db,
                                       String sanPhamId,
                                       int sizeGiay,
                                       int soLuong) {

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_SAN_PHAM,
                null,
                HeThongBanGiayDBHelper.SP_ID + "=?",
                new String[]{sanPhamId},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String tenSanPham = cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_TEN));
            int donGia = (int) cursor.getDouble(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_DON_GIA));
            String anhSanPham = cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.SP_ANH));

            ContentValues values = new ContentValues();
            values.put(HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM, tenSanPham);
            values.put(HeThongBanGiayDBHelper.GH_DON_GIA, donGia);
            values.put(HeThongBanGiayDBHelper.GH_GIA_TIEN, donGia * soLuong);
            values.put(HeThongBanGiayDBHelper.GH_SIZE_GIAY, sizeGiay);
            values.put(HeThongBanGiayDBHelper.GH_MAU_SAC, "");
            values.put(HeThongBanGiayDBHelper.GH_SO_LUONG, soLuong);
            values.put(HeThongBanGiayDBHelper.GH_ANH_SAN_PHAM, anhSanPham);

            db.insertWithOnConflict(
                    HeThongBanGiayDBHelper.BANG_GIO_HANG,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
        }

        cursor.close();
    }

    private void seedFirebaseAddressForCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> diaChiNha = new HashMap<>();
        diaChiNha.put("diaChiId", "DC_HOME_" + user.getUid());
        diaChiNha.put("nguoiDungId", user.getUid());
        diaChiNha.put("diaChi", "25 Nguyen Hue, Ben Nghe, Quan 1, TP.HCM");
        diaChiNha.put("soDienThoai", "0901234567");
        diaChiNha.put("tenNguoiNhan", "Nguyen Van A");
        diaChiNha.put("macDinh", true);

        Map<String, Object> diaChiCongTy = new HashMap<>();
        diaChiCongTy.put("diaChiId", "DC_OFFICE_" + user.getUid());
        diaChiCongTy.put("nguoiDungId", user.getUid());
        diaChiCongTy.put("diaChi", "12 Vo Van Tan, Phuong 6, Quan 3, TP.HCM");
        diaChiCongTy.put("soDienThoai", "0912345678");
        diaChiCongTy.put("tenNguoiNhan", "Nguyen Van A");
        diaChiCongTy.put("macDinh", false);

        firestore.collection("DiaChi").document("DC_HOME_" + user.getUid()).set(diaChiNha);
        firestore.collection("DiaChi").document("DC_OFFICE_" + user.getUid()).set(diaChiCongTy);
    }
}
