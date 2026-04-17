package com.example.hethongbangiay.viewmodels;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<List<DonHang>> listDonHang = new MutableLiveData<>();
    private DonHangRepository repository = new DonHangRepository();

    public LiveData<List<DonHang>> getDonHang() {
        return listDonHang;
    }
    private MutableLiveData<String> createOrderResult = new MutableLiveData<>();
    public LiveData<String> getCreateOrderResult() {
        return createOrderResult;
    }

    private MutableLiveData<DonHang> donHangDetail = new MutableLiveData<>();

    public LiveData<DonHang> getDonHangDetail() {
        return donHangDetail;
    }
    public void loadDonHang() {
        repository.getAllDonHang(new DonHangRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<DonHang> data) {
                listDonHang.setValue(data);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
    public void taoDonHang(List<ChiTietDonHang> cart) {

        repository.createOrder(cart, new DonHangRepository.OnCreateOrderListener() {
            @Override
            public void onSuccess(String orderId) {
                createOrderResult.setValue(orderId);

                // reload lại danh sách đơn hàng sau khi tạo
                loadDonHang();
            }

            @Override
            public void onError(Exception e) {
                createOrderResult.setValue(null);
            }
        });
    }
    public void loadDonHangById(String id) {
        repository.getDonHangById(id, new DonHangRepository.OnDataLoadedSingle() {
            @Override
            public void onSuccess(DonHang donHang) {
                donHangDetail.setValue(donHang);
            }

            @Override
            public void onError(Exception e) {
                donHangDetail.setValue(null);
            }
        });
    }

    private MutableLiveData<List<ChiTietDonHang>> chiTietDonHang = new MutableLiveData<>();

    public LiveData<List<ChiTietDonHang>> getChiTietDonHang() {
        return chiTietDonHang;
    }

    public void loadChiTietDonHang(String donHangId) {
        repository.getChiTietDonHang(donHangId,
                new DonHangRepository.OnChiTietLoaded() {
                    @Override
                    public void onSuccess(List<ChiTietDonHang> list) {
                        chiTietDonHang.setValue(list);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private MutableLiveData<DiaChi> diaChiGiaoHang = new MutableLiveData<>();

    public LiveData<DiaChi> getDiaChiGiaoHang() {
        return diaChiGiaoHang;
    }
    public void loadDiaChiGiaoHang(String nguoiDungId){
        repository.getDiaChiTheoNguoiDungId(    nguoiDungId, new DonHangRepository.OnDiaChiMacDinhLoaded() {
            @Override
            public void onSuccess(DiaChi data) {
                diaChiGiaoHang.setValue(data);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
