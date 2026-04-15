package com.example.hethongbangiay.firestore;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.database.DanhGiaDB;
import com.example.hethongbangiay.database.DanhMucDB;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.database.SizeGiayDB;
import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseMigrationSeeder {

    private final Context context;
    private final FirebaseFirestore firestore;

    public FirebaseMigrationSeeder(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void migrateAll(@NonNull Runnable onSuccess,
                           @NonNull java.util.function.Consumer<Exception> onError) {

        try {
            DanhMucDB danhMucDB = new DanhMucDB(context);
            SanPhamDB sanPhamDB = new SanPhamDB(context);
            SizeGiayDB sizeGiayDB = new SizeGiayDB(context);
            DanhGiaDB danhGiaDB = new DanhGiaDB(context);

            List<DanhMuc> dsDanhMuc = danhMucDB.layTatCaDMActive();
            List<SanPham> dsSanPham = sanPhamDB.layTatCaSpDangActive();

            WriteBatch batch = firestore.batch();

            for (DanhMuc dm : dsDanhMuc) {
                Map<String, Object> map = new HashMap<>();
                map.put("danhMucId", dm.getDanhMucId());
                map.put("tenDanhMuc", dm.getTenDanhMuc());
                map.put("moTaDanhMuc", dm.getMoTaDanhMuc());
                map.put("anhDanhMuc", dm.getAnhDanhMuc());
                map.put("active", dm.isActive());

                batch.set(
                        firestore.collection("DanhMuc").document(dm.getDanhMucId()),
                        map
                );
            }

            for (SanPham sp : dsSanPham) {
                Map<String, Object> mapSp = new HashMap<>();
                mapSp.put("sanPhamId", sp.getSanPhamId());
                mapSp.put("danhMucId", sp.getDanhMucId());
                mapSp.put("tenSanPham", sp.getTenSanPham());
                mapSp.put("donGia", sp.getDonGia());
                mapSp.put("anhSanPham", sp.getAnhSanPham());
                mapSp.put("moTaSanPham", sp.getMoTaSanPham());
                mapSp.put("diemDanhGia", sp.getDiemDanhGia());
                mapSp.put("luotBan", sp.getLuotBan());
                mapSp.put("active", sp.isActive());

                batch.set(
                        firestore.collection("SanPham").document(sp.getSanPhamId()),
                        mapSp
                );

                List<SizeGiay> dsSize = sizeGiayDB.laySizeTheoSanPhamId(sp.getSanPhamId());
                for (SizeGiay size : dsSize) {
                    Map<String, Object> mapSize = new HashMap<>();
                    mapSize.put("sizeGiayId", size.getSizeGiayId());
                    mapSize.put("sanPhamId", size.getSanPhamId());
                    mapSize.put("size", size.getSize());
                    mapSize.put("soLuong", size.getSoLuong());

                    batch.set(
                            firestore.collection("SanPham")
                                    .document(sp.getSanPhamId())
                                    .collection("Sizes")
                                    .document(size.getSizeGiayId()),
                            mapSize
                    );
                }

                List<DanhGia> dsDanhGia = danhGiaDB.layDanhGiaTheoSanPhamId(sp.getSanPhamId());
                for (DanhGia dg : dsDanhGia) {
                    Map<String, Object> mapDg = new HashMap<>();
                    mapDg.put("danhGiaId", dg.getDanhGiaId());
                    mapDg.put("nguoiDungId", dg.getNguoiDungId());
                    mapDg.put("sanPhamId", dg.getSanPhamId());
                    mapDg.put("rating", dg.getRating());
                    mapDg.put("comment", dg.getComment());

                    batch.set(
                            firestore.collection("SanPham")
                                    .document(sp.getSanPhamId())
                                    .collection("DanhGia")
                                    .document(dg.getDanhGiaId()),
                            mapDg
                    );
                }
            }

            batch.commit()
                    .addOnSuccessListener(unused -> onSuccess.run())
                    .addOnFailureListener(onError::accept);

        } catch (Exception e) {
            onError.accept(e);
        }
    }
}