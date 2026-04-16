package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.session.SessionManager;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.viewmodels.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFragment extends Fragment {

    private TextView txtHeader;
    private TextView tvHoTen;
    private TextView tvSoDienThoai;
    private TextView btnEditProfile;
    private TextView btnLogout;
    private ImageView imgAvatar;
    private View rowEditProfile;
    private View rowAddress;
    private View rowHistoryOrder;
    private Switch switchDarkMode;

    private ProfileViewModel profileViewModel;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = new SessionManager(requireContext());

        txtHeader = view.findViewById(R.id.txtHeaderTitle);
        tvHoTen = view.findViewById(R.id.txtUsenameProfile);
        tvSoDienThoai = view.findViewById(R.id.txtPhoneProfile);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        rowEditProfile = view.findViewById(R.id.rowEditProfile);
        btnEditProfile = view.findViewById(R.id.tvUpdateProfile);
        rowAddress = view.findViewById(R.id.txtAddress);
        rowHistoryOrder = view.findViewById(R.id.txtHistoryOrder);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchDarkMode = view.findViewById(R.id.switchDark);

        txtHeader.setText("Hồ sơ cá nhân");

        switchDarkMode.setChecked(!sessionManager.getThemeLight());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setThemeLight(!isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        View.OnClickListener moChinhSuaHoSo = v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class));
        rowEditProfile.setOnClickListener(moChinhSuaHoSo);
        btnEditProfile.setOnClickListener(moChinhSuaHoSo);

        rowAddress.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ShippingAddressActivity.class)));

        rowHistoryOrder.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_orders);
            }
        });

        btnLogout.setOnClickListener(v -> {
            profileViewModel.logout();

            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }

            requireActivity().recreate();
        });

        observeViewModel();
        applyInsets(view);
        profileViewModel.loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewModel.loadProfile();
    }

    private void observeViewModel() {
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), this::bindProfile);
        profileViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        profileViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading ->
                btnLogout.setEnabled(!Boolean.TRUE.equals(isLoading)));
    }

    private void bindProfile(NguoiDung nguoiDung) {
        if (nguoiDung == null) {
            return;
        }

        tvHoTen.setText(nguoiDung.getHoTen() != null && !nguoiDung.getHoTen().isEmpty()
                ? nguoiDung.getHoTen()
                : "Chưa cập nhật");

        tvSoDienThoai.setText(nguoiDung.getSoDienThoai() != null && !nguoiDung.getSoDienThoai().isEmpty()
                ? nguoiDung.getSoDienThoai()
                : "Chưa cập nhật");

        ImageResolver.loadAvatar(imgAvatar, nguoiDung.getAvatar());
    }

    private void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
