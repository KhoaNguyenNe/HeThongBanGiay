package com.example.hethongbangiay.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static final String Session_name = "Shoea_Session";
    private static final String KEY_ONBOARDING = "onboarding_done";

    //THEME
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final Boolean THEME_LIGHT = true;
    private static final Boolean THEME_DARK = false;

    //Giỏ hàng
    private static final String KEY_GIO_HANG = "cart";
    private static final String KEY_CHO_XU_LY_THANH_TOAN = "cho_xu_ly_thanh_toan";
    private static final String KEY_PHUONG_THUC_THANH_TOAN = "phuong_thuc_thanh_toan";
    private static final String KEY_DIA_CHI_CHECKOUT = "dia_chi_checkout";
    private static final String KEY_PHI_SHIP = "phi_ship";
    private static final String KEY_GIAM_GIA = "giam_gia";

    private final SharedPreferences preferences;
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(Session_name, Context.MODE_PRIVATE);
    }

    public void setOnBoardingDone(Boolean done) {
        preferences.edit().putBoolean(KEY_ONBOARDING, done).apply();
    }

    public boolean isOnBoardingDone() {
        return preferences.getBoolean(KEY_ONBOARDING, false);
    }

    public void setThemeLight(Boolean isLight) {
        preferences.edit().putBoolean(KEY_THEME_MODE, isLight).apply();
    }

    public boolean getThemeLight() {
        return preferences.getBoolean(KEY_THEME_MODE, THEME_LIGHT);
    }

    public void themSpVaoGioHang(SanPham sanPham, SizeGiay sizeGiay, int soLuong) {
        try {
            JSONArray cartArray = new JSONArray(preferences.getString(KEY_GIO_HANG, "[]"));
            boolean updated = false;

            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject obj = cartArray.getJSONObject(i);

                boolean sameItem =
                        sanPham.getTenSanPham().equals(obj.optString("tenSanPham")) &&
                                sizeGiay.getSize() == obj.optInt("sizeGiay");

                if (sameItem) {
                    int newQuantity = obj.optInt("soLuong") + soLuong;
                    obj.put("soLuong", newQuantity);
                    obj.put("giaTien", (int) (sanPham.getDonGia() * newQuantity));
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                ChiTietDonHang item = new ChiTietDonHang();
                item.setTenSanPham(sanPham.getTenSanPham());
                item.setGiaTien((int) (sanPham.getDonGia() * soLuong));
                item.setSizeGiay(sizeGiay.getSize());
                item.setSoLuong(soLuong);
                item.setAnhSanPham(sanPham.getAnhSanPham());
                item.setMauSac("");

                cartArray.put(toJson(item));
            }

            preferences.edit().putString(KEY_GIO_HANG, cartArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ChiTietDonHang> getThongTinGioHang() {
        List<ChiTietDonHang> list = new ArrayList<>();

        try {
            JSONArray cartArray = new JSONArray(preferences.getString(KEY_GIO_HANG, "[]"));
            for (int i = 0; i < cartArray.length(); i++) {
                list.add(fromJson(cartArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int soLuongSpTrongGioHang() {
        int total = 0;
        for (ChiTietDonHang item : getThongTinGioHang()) {
            total += item.getSoLuong();
        }
        return total;
    }

    public void xoaGioHang() {
        preferences.edit().remove(KEY_GIO_HANG).apply();
    }

    public void batDauChoXuLyThanhToan() {
        preferences.edit().putBoolean(KEY_CHO_XU_LY_THANH_TOAN, true).apply();
    }

    public boolean dangChoXuLyThanhToan() {
        return preferences.getBoolean(KEY_CHO_XU_LY_THANH_TOAN, false);
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        preferences.edit().putString(KEY_PHUONG_THUC_THANH_TOAN, phuongThucThanhToan).apply();
    }

    public String getPhuongThucThanhToan() {
        return preferences.getString(KEY_PHUONG_THUC_THANH_TOAN, "COD");
    }

    public void setDiaChiCheckout(String diaChiId) {
        preferences.edit().putString(KEY_DIA_CHI_CHECKOUT, diaChiId).apply();
    }

    public String getDiaChiCheckout() {
        return preferences.getString(KEY_DIA_CHI_CHECKOUT, "");
    }

    public void setPhiShip(int phiShip) {
        preferences.edit().putInt(KEY_PHI_SHIP, phiShip).apply();
    }

    public int getPhiShip() {
        return preferences.getInt(KEY_PHI_SHIP, 15000);
    }

    public void setGiamGia(int giamGia) {
        preferences.edit().putInt(KEY_GIAM_GIA, giamGia).apply();
    }

    public int getGiamGia() {
        return preferences.getInt(KEY_GIAM_GIA, 0);
    }

    public void xoaThongTinTamCheckout() {
        preferences.edit()
                .remove(KEY_CHO_XU_LY_THANH_TOAN)
                .remove(KEY_PHUONG_THUC_THANH_TOAN)
                .remove(KEY_DIA_CHI_CHECKOUT)
                .remove(KEY_PHI_SHIP)
                .remove(KEY_GIAM_GIA)
                .apply();
    }

    private JSONObject toJson(ChiTietDonHang item) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("tenSanPham", item.getTenSanPham());
        json.put("giaTien", item.getGiaTien());
        json.put("sizeGiay", item.getSizeGiay());
        json.put("soLuong", item.getSoLuong());
        json.put("anhSanPham", item.getAnhSanPham());
        json.put("mauSac", item.getMauSac());
        return json;
    }

    private ChiTietDonHang fromJson(JSONObject json) {
        ChiTietDonHang item = new ChiTietDonHang();
        item.setTenSanPham(json.optString("tenSanPham"));
        item.setGiaTien(json.optInt("giaTien"));
        item.setSizeGiay(json.optInt("sizeGiay"));
        item.setSoLuong(json.optInt("soLuong"));
        item.setAnhSanPham(json.optString("anhSanPham"));
        item.setMauSac(json.optString("mauSac"));
        return item;
    }

}
