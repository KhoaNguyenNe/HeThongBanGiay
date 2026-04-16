package com.example.hethongbangiay.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.CartAdapter;
import com.example.hethongbangiay.database.GioHangDB;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.utils.FormatUtils;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.session.SessionManager;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvEmptyCart;
    private TextView tvCartTotalQuantity;
    private TextView tvCartTotalPrice;
    private View bottomSummaryCard;
    private AppCompatButton btnCheckout;
    private View removeOverlay;
    private View removeSheetContent;
    private ImageView imgRemoveProduct;
    private TextView tvRemoveName;
    private TextView tvRemoveColor;
    private TextView tvRemoveSize;
    private TextView tvRemovePrice;
    private TextView tvRemoveQty;
    private TextView tvRemoveMetaDivider;
    private View viewRemoveColorDot;
    private AppCompatButton btnCancelRemove;
    private AppCompatButton btnConfirmRemove;
    private ChiTietDonHang sanPhamChoXoa;

    private GioHangDB gioHangDB;
    private CartAdapter cartAdapter;
    private SessionManager sessionManager;
    public CartFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rvCart);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        tvCartTotalQuantity = view.findViewById(R.id.tvCartTotalQuantity);
        tvCartTotalPrice = view.findViewById(R.id.tvCartTotalPrice);
        bottomSummaryCard = view.findViewById(R.id.bottomSummaryCard);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        btnCheckout.setEnabled(false);

        ImageView btnBackCart = view.findViewById(R.id.btnBackCart);
        btnBackCart.setOnClickListener(v -> {
            BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setSelectedItemId(R.id.nav_home);
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        removeOverlay = view.findViewById(R.id.removeOverlay);
        removeSheetContent = view.findViewById(R.id.removeSheetContent);
        imgRemoveProduct = removeSheetContent.findViewById(R.id.imgProduct);
        tvRemoveName = removeSheetContent.findViewById(R.id.tvName);
        tvRemoveColor = removeSheetContent.findViewById(R.id.tvColor);
        tvRemoveSize = removeSheetContent.findViewById(R.id.tvSize);
        tvRemovePrice = removeSheetContent.findViewById(R.id.tvPrice);
        tvRemoveQty = removeSheetContent.findViewById(R.id.tvDialogQty);
        tvRemoveMetaDivider = removeSheetContent.findViewById(R.id.tvMetaDivider);
        viewRemoveColorDot = removeSheetContent.findViewById(R.id.viewColorDot);
        btnCancelRemove = removeSheetContent.findViewById(R.id.btnCancel);
        btnConfirmRemove = removeSheetContent.findViewById(R.id.btnRemove);

        gioHangDB = new GioHangDB(requireContext());
        sessionManager = new SessionManager(requireContext());
        cartAdapter = new CartAdapter(new CartAdapter.OnCartActionListener() {
            @Override
            public void onTangSoLuong(ChiTietDonHang item) {
                gioHangDB.capNhatSoLuong(item, item.getSoLuong() + 1);
                taiGioHang();
            }

            @Override
            public void onGiamSoLuong(ChiTietDonHang item) {
                if (item.getSoLuong() <= 1) {
                    hienThiDialogXoa(item);
                    return;
                }
                gioHangDB.capNhatSoLuong(item, item.getSoLuong() - 1);
                taiGioHang();
            }

            @Override
            public void onXoaSanPham(ChiTietDonHang item) {
                hienThiDialogXoa(item);
            }

            @Override
            public void onSelectionChanged(List<ChiTietDonHang> selectedItems) {
                capNhatTongTheoChon(selectedItems);
            }
        });

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> {
            if (gioHangDB.gioHangTrong()) {
                Toast.makeText(requireContext(), "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            List<ChiTietDonHang> selected = cartAdapter.getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu danh sách được chọn để Checkout/Thanh toán xử lý đúng.
            sessionManager.setGioHangDangChon(selected);
            startActivity(new android.content.Intent(requireContext(), CheckoutActivity.class));
        });

        removeOverlay.setOnClickListener(v -> anSheetXoa());
        removeSheetContent.setOnClickListener(v -> {
            // Giữ sự kiện trong bottom sheet, không đóng khi chạm vào nội dung.
        });
        btnCancelRemove.setOnClickListener(v -> anSheetXoa());
        btnConfirmRemove.setOnClickListener(v -> {
            if (sanPhamChoXoa != null) {
                gioHangDB.xoaSanPhamTrongGio(sanPhamChoXoa);
                anSheetXoa();
                taiGioHang();
            }
        });

        applyInsets(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        taiGioHang();
    }

    private void taiGioHang() {
        List<ChiTietDonHang> dsGioHang = gioHangDB.layTatCaSanPhamTrongGio();
        cartAdapter.capNhatDuLieu(dsGioHang);

        boolean trong = dsGioHang.isEmpty();
        tvEmptyCart.setVisibility(trong ? View.VISIBLE : View.GONE);
        rvCart.setVisibility(trong ? View.GONE : View.VISIBLE);
        bottomSummaryCard.setVisibility(trong ? View.GONE : View.VISIBLE);
    }

    private void capNhatTongTheoChon(List<ChiTietDonHang> selectedItems) {
        int tongSoLuong = 0;
        double tongTien = 0;
        if (selectedItems != null) {
            for (ChiTietDonHang item : selectedItems) {
                if (item == null) continue;
                tongSoLuong += item.getSoLuong();
                tongTien += item.getGiaTien();
            }
        }

        tvCartTotalQuantity.setText(tongSoLuong + " sản phẩm");
        tvCartTotalPrice.setText(FormatUtils.formatCurrency(tongTien));
        btnCheckout.setEnabled(tongSoLuong > 0);
    }

    private void hienThiDialogXoa(ChiTietDonHang item) {
        sanPhamChoXoa = item;
        tvRemoveName.setText(item.getTenSanPham());
        tvRemoveSize.setText("Size = " + item.getSizeGiay());
        tvRemovePrice.setText(FormatUtils.formatCurrency(item.getGiaTien()));
        tvRemoveQty.setText(String.valueOf(item.getSoLuong()));

        if (item.getMauSac() == null || item.getMauSac().trim().isEmpty()) {
            viewRemoveColorDot.setVisibility(View.GONE);
            tvRemoveColor.setVisibility(View.GONE);
            tvRemoveMetaDivider.setVisibility(View.GONE);
        } else {
            viewRemoveColorDot.setVisibility(View.VISIBLE);
            tvRemoveColor.setVisibility(View.VISIBLE);
            tvRemoveMetaDivider.setVisibility(View.VISIBLE);
            tvRemoveColor.setText(item.getMauSac());
        }

        ImageResolver.loadImageReference(imgRemoveProduct, item.getAnhSanPham());

        removeOverlay.setVisibility(View.VISIBLE);
        bottomSummaryCard.setVisibility(View.INVISIBLE);
        removeOverlay.setAlpha(0f);
        removeSheetContent.setTranslationY(80f);
        removeOverlay.animate().alpha(1f).setDuration(160).start();
        removeSheetContent.animate().translationY(0f).setDuration(180).start();
    }

    private void applyInsets(View root) {
        View headerBar = root.findViewById(R.id.headerBar);
        View bottomSummaryCard = root.findViewById(R.id.bottomSummaryCard);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            headerBar.setPadding(
                    headerBar.getPaddingLeft(),
                    dp(4),
                    headerBar.getPaddingRight(),
                    headerBar.getPaddingBottom()
            );

            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) bottomSummaryCard.getLayoutParams();
            params.bottomMargin = dp(8);
            bottomSummaryCard.setLayoutParams(params);

            return insets;
        });
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    private void anSheetXoa() {
        removeSheetContent.animate().translationY(80f).setDuration(160).start();
        removeOverlay.animate()
                .alpha(0f)
                .setDuration(160)
                .withEndAction(() -> {
                    removeOverlay.setVisibility(View.GONE);
                    bottomSummaryCard.setVisibility(gioHangDB.gioHangTrong() ? View.GONE : View.VISIBLE);
                    sanPhamChoXoa = null;
                })
                .start();
    }
}
