package com.example.hethongbangiay.activities;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.session.SessionManager;

public class ProfileFragment extends Fragment {

    TextView tvEditProfile, txtHeader;
    Switch switchDark;
    SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager(requireContext());
        txtHeader = view.findViewById(R.id.txtHeaderTitle);
        txtHeader.setText("Hồ sơ cá nhân");
        tvEditProfile = view.findViewById(R.id.tvUpdateProfile);
        switchDark = view.findViewById(R.id.switchDark);
        switchDark.setChecked(!sessionManager.getThemeLight());
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setThemeLight(!isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            requireActivity().recreate();
        });
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
