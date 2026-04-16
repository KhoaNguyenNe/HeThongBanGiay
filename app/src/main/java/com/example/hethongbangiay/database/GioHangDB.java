package com.example.hethongbangiay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;

import java.util.ArrayList;
import java.util.List;

public class GioHangDB {

    private final SQLiteDatabase db;

    public GioHangDB(Context context) {
        db = new HeThongBanGiayDBHelper(context).getWritableDatabase();
    }

    public void themSanPhamVaoGio(SanPham sanPham, SizeGiay sizeGiay, int soLuong) {
        if (sanPham == null || sizeGiay == null || soLuong <= 0) {
            return;
        }

        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_GIO_HANG,
                null,
                HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM + "=? AND "
                        + HeThongBanGiayDBHelper.GH_SIZE_GIAY + "=?",
                new String[]{sanPham.getTenSanPham(), String.valueOf(sizeGiay.getSize())},
                null,
                null,
                null
        );

        int donGia = (int) sanPham.getDonGia();

        if (cursor.moveToFirst()) {
            int soLuongCu = cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_SO_LUONG));
            int soLuongMoi = soLuongCu + soLuong;

            ContentValues values = new ContentValues();
            values.put(HeThongBanGiayDBHelper.GH_SO_LUONG, soLuongMoi);
            values.put(HeThongBanGiayDBHelper.GH_GIA_TIEN, donGia * soLuongMoi);

            db.update(
                    HeThongBanGiayDBHelper.BANG_GIO_HANG,
                    values,
                    HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM + "=? AND "
                            + HeThongBanGiayDBHelper.GH_SIZE_GIAY + "=?",
                    new String[]{sanPham.getTenSanPham(), String.valueOf(sizeGiay.getSize())}
            );
        } else {
            ContentValues values = new ContentValues();
            values.put(HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM, sanPham.getTenSanPham());
            values.put(HeThongBanGiayDBHelper.GH_DON_GIA, donGia);
            values.put(HeThongBanGiayDBHelper.GH_GIA_TIEN, donGia * soLuong);
            values.put(HeThongBanGiayDBHelper.GH_SIZE_GIAY, sizeGiay.getSize());
            values.put(HeThongBanGiayDBHelper.GH_MAU_SAC, "");
            values.put(HeThongBanGiayDBHelper.GH_SO_LUONG, soLuong);
            values.put(HeThongBanGiayDBHelper.GH_ANH_SAN_PHAM, sanPham.getAnhSanPham());
            values.put(HeThongBanGiayDBHelper.GH_SAN_PHAM_ID,sanPham.getSanPhamId());
            db.insert(HeThongBanGiayDBHelper.BANG_GIO_HANG, null, values);
        }

        cursor.close();
    }

    public List<ChiTietDonHang> layTatCaSanPhamTrongGio() {
        List<ChiTietDonHang> data = new ArrayList<>();
        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_GIO_HANG,
                null,
                null,
                null,
                null,
                null,
                HeThongBanGiayDBHelper.GH_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                data.add(taoChiTietDonHang(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

    public void capNhatSoLuong(ChiTietDonHang item, int soLuongMoi) {
        if (item == null) {
            return;
        }

        if (soLuongMoi <= 0) {
            xoaSanPhamTrongGio(item);
            return;
        }

        int donGia = layDonGiaTheoSanPham(item.getTenSanPham(), item.getSizeGiay());
        ContentValues values = new ContentValues();
        values.put(HeThongBanGiayDBHelper.GH_SO_LUONG, soLuongMoi);
        values.put(HeThongBanGiayDBHelper.GH_GIA_TIEN, donGia * soLuongMoi);

        db.update(
                HeThongBanGiayDBHelper.BANG_GIO_HANG,
                values,
                HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM + "=? AND "
                        + HeThongBanGiayDBHelper.GH_SIZE_GIAY + "=?",
                new String[]{item.getTenSanPham(), String.valueOf(item.getSizeGiay())}
        );
    }

    public void xoaSanPhamTrongGio(ChiTietDonHang item) {
        if (item == null) {
            return;
        }

        db.delete(
                HeThongBanGiayDBHelper.BANG_GIO_HANG,
                HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM + "=? AND "
                        + HeThongBanGiayDBHelper.GH_SIZE_GIAY + "=?",
                new String[]{item.getTenSanPham(), String.valueOf(item.getSizeGiay())}
        );
    }

    public int tongSoLuongSanPham() {
        int tong = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(SUM(" + HeThongBanGiayDBHelper.GH_SO_LUONG + "), 0) FROM "
                        + HeThongBanGiayDBHelper.BANG_GIO_HANG,
                null
        );

        if (cursor.moveToFirst()) {
            tong = cursor.getInt(0);
        }

        cursor.close();
        return tong;
    }

    public int tongTienGioHang() {
        int tong = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(SUM(" + HeThongBanGiayDBHelper.GH_GIA_TIEN + "), 0) FROM "
                        + HeThongBanGiayDBHelper.BANG_GIO_HANG,
                null
        );

        if (cursor.moveToFirst()) {
            tong = cursor.getInt(0);
        }

        cursor.close();
        return tong;
    }

    public boolean gioHangTrong() {
        return tongSoLuongSanPham() == 0;
    }

    /**
     * Xóa toàn bộ sản phẩm trong bảng giỏ hàng.
     * Sử dụng lệnh DELETE không có WHERE để xóa sạch table.
     */
    public void xoaTatCaGioHang() {
        db.beginTransaction();
        try {
            db.delete(HeThongBanGiayDBHelper.BANG_GIO_HANG, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private int layDonGiaTheoSanPham(String tenSanPham, int sizeGiay) {
        Cursor cursor = db.query(
                HeThongBanGiayDBHelper.BANG_GIO_HANG,
                new String[]{HeThongBanGiayDBHelper.GH_DON_GIA},
                HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM + "=? AND "
                        + HeThongBanGiayDBHelper.GH_SIZE_GIAY + "=?",
                new String[]{tenSanPham, String.valueOf(sizeGiay)},
                null,
                null,
                null
        );

        int donGia = 0;
        if (cursor.moveToFirst()) {
            donGia = cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_DON_GIA));
        }
        cursor.close();
        return donGia;
    }

    private ChiTietDonHang taoChiTietDonHang(Cursor cursor) {
        ChiTietDonHang item = new ChiTietDonHang();
        item.setTenSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_TEN_SAN_PHAM)));
        item.setGiaTien(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_GIA_TIEN)));
        item.setSizeGiay(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_SIZE_GIAY)));
        item.setMauSac(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_MAU_SAC)));
        item.setSoLuong(cursor.getInt(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_SO_LUONG)));
        item.setAnhSanPham(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_ANH_SAN_PHAM)));
        item.setSanPhamId(cursor.getString(cursor.getColumnIndexOrThrow(HeThongBanGiayDBHelper.GH_SAN_PHAM_ID)));
        return item;
    }
}
