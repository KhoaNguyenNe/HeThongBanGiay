package com.example.hethongbangiay.viewmodels;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<List<DonHang>> listDonHang = new MutableLiveData<>();
    private DonHangRepository repository = new DonHangRepository();

    public LiveData<List<DonHang>> getDonHang() {
        return listDonHang;
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
}
