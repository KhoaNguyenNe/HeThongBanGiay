package com.example.hethongbangiay.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.LichSuTimKiemAdapter;
import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.repositories.DanhMucRepository;
import com.example.hethongbangiay.repositories.FavoriteRepository;
import com.example.hethongbangiay.repositories.SanPhamRepository;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.utils.FavoriteUiHelper;
import com.example.hethongbangiay.utils.FormatUtils;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.utils.ProductNavigationHelper;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String PREF_NAME = "search_pref";
    private static final String KEY_RECENT_SEARCHES = "recent_searches";
    private static final int MAX_RECENT_KEYWORDS = 8;

    private TextInputEditText edtSearch;
    private ImageView ivFilter;

    private LinearLayout recentContainer;
    private LinearLayout resultsContainer;
    private LinearLayout emptyContainer;

    private TextView tvClearAll;
    private TextView tvResultTitle;
    private TextView tvResultCount;
    private TextView tvClearResults;

    private RecyclerView rvRecent;
    private RecyclerView rvProducts;

    private LichSuTimKiemAdapter LichSuTimKiemAdapter;
    private SanPhamAdapter sanPhamAdapter;
    private SanPhamRepository SanPhamRepository;
    private DanhMucRepository danhMucDB;
    private FavoriteRepository favoriteRepository;

    private final List<String> recentKeywords = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    private String submittedKeyword = "";
    private boolean isSearchSubmitted = false;

    private String selectedCategoryId = null;
    private String selectedSort = SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT;

    private float selectedMinPrice = 0f;
    private float selectedMaxPrice = 0f;
    private float selectedMinRating = 0f;
    private float absoluteMaxPrice = 0f;
    private int searchRequestVersion = 0;
    private boolean dangXoaKetQua = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_search);

        ThemeUtils.applySystemBars(this);
        setupInsets();
        bindViews();
        initData();
        setupRecyclerViews();
        setupActions();
        showRecentState();
    }

    private void bindViews() {
        edtSearch = findViewById(R.id.edtSearch);
        ivFilter = findViewById(R.id.ivFilter);

        recentContainer = findViewById(R.id.recentContainer);
        resultsContainer = findViewById(R.id.resultsContainer);
        emptyContainer = findViewById(R.id.emptyContainer);

        tvClearAll = findViewById(R.id.tvClearAll);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultCount = findViewById(R.id.tvResultCount);
        tvClearResults = findViewById(R.id.tvClearResults);

        rvRecent = findViewById(R.id.rvRecent);
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void initData() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SanPhamRepository = new SanPhamRepository();
        danhMucDB = new DanhMucRepository();
        favoriteRepository = new FavoriteRepository();

        SanPhamRepository.layGiaMax(new OnFirestoreResult<Double>() {
            @Override
            public void onSuccess(Double data) {
                absoluteMaxPrice = data == null ? 5_000_000f : data.floatValue();
                if (absoluteMaxPrice <= 0f) {
                    absoluteMaxPrice = 5_000_000f;
                }
                selectedMaxPrice = absoluteMaxPrice;
            }

            @Override
            public void onError(Exception e) {
                absoluteMaxPrice = 5_000_000f;
                selectedMaxPrice = absoluteMaxPrice;
            }
        });

        loadRecentKeywords();
    }

    private void setupRecyclerViews() {
        LichSuTimKiemAdapter = new LichSuTimKiemAdapter(this, recentKeywords, new LichSuTimKiemAdapter.OnRecentActionListener() {
            @Override
            public void onRecentClick(String keyword) {
                edtSearch.setText(keyword);
                if (edtSearch.getText() != null) {
                    edtSearch.setSelection(edtSearch.getText().length());
                }
                submittedKeyword = keyword;
                isSearchSubmitted = true;
                performSearch();
            }

            @Override
            public void onDeleteClick(String keyword) {
                recentKeywords.remove(keyword);
                persistRecentKeywords();
                LichSuTimKiemAdapter.capNhatDuLieu(recentKeywords);
                tvClearAll.setVisibility(recentKeywords.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        rvRecent.setLayoutManager(new LinearLayoutManager(this));
        rvRecent.setAdapter(LichSuTimKiemAdapter);

        sanPhamAdapter = new SanPhamAdapter(this, new ArrayList<>(),
                sp -> ProductNavigationHelper.openProductDetail(SearchActivity.this, sp.getSanPhamId()));
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(sanPhamAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachYeuThich();
    }

    private void setupActions() {
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitSearch();
                return true;
            }
            return false;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dangXoaKetQua) {
                    return;
                }

                String currentText = s.toString().trim();

                if (currentText.isEmpty() && !hasActiveFilter()) {
                    searchRequestVersion++;
                    isSearchSubmitted = false;
                    showRecentState();
                    return;
                }

                if (isSearchSubmitted) {
                    submittedKeyword = currentText;
                    performSearch();
                }
            }
        });

        ivFilter.setOnClickListener(v -> showSortFilterBottomSheet());

        tvClearAll.setOnClickListener(v -> {
            recentKeywords.clear();
            persistRecentKeywords();
            LichSuTimKiemAdapter.capNhatDuLieu(recentKeywords);
            tvClearAll.setVisibility(View.GONE);
        });

        tvClearResults.setOnClickListener(v -> clearSearchResults());
    }

    private void submitSearch() {
        String keyword = edtSearch.getText() == null ? "" : edtSearch.getText().toString().trim();

        if (keyword.isEmpty() && !hasActiveFilter()) {
            showRecentState();
            return;
        }

        submittedKeyword = keyword;
        isSearchSubmitted = true;

        if (!keyword.isEmpty()) {
            saveRecentKeyword(keyword);
        }

        performSearch();
        hideKeyboard();
    }

    private void performSearch() {
        final int requestVersion = ++searchRequestVersion;
        final String keywordSnapshot = submittedKeyword;

        SanPhamRepository.timKiemSanPham(
                keywordSnapshot,
                selectedCategoryId,
                selectedMinPrice,
                selectedMaxPrice,
                selectedMinRating,
                selectedSort,
                new OnFirestoreResult<List<SanPham>>() {
                    @Override
                    public void onSuccess(List<SanPham> ketQua) {
                        if (requestVersion != searchRequestVersion) {
                            return;
                        }

                        sanPhamAdapter.capNhatDuLieu(ketQua);

                        String title;
                        if (keywordSnapshot.isEmpty()) {
                            title = "Tất cả sản phẩm";
                        } else {
                            title = "Kết quả cho \"" + keywordSnapshot + "\"";
                        }

                        tvResultTitle.setText(title);
                        tvResultCount.setText(formatCount(ketQua.size()) + " kết quả");

                        if (ketQua.isEmpty()) {
                            showEmptyState();
                        } else {
                            showResultsState();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (requestVersion != searchRequestVersion) {
                            return;
                        }

                        sanPhamAdapter.capNhatDuLieu(new ArrayList<>());
                        tvResultTitle.setText("Lỗi tìm kiếm");
                        tvResultCount.setText("0 kết quả");
                        showEmptyState();
                    }
                }
        );
    }

    private void showRecentState() {
        recentContainer.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.GONE);
        tvClearAll.setVisibility(recentKeywords.isEmpty() ? View.GONE : View.VISIBLE);
        LichSuTimKiemAdapter.capNhatDuLieu(recentKeywords);
    }

    private void showResultsState() {
        recentContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.VISIBLE);
        emptyContainer.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recentContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.VISIBLE);
    }

    private void showSortFilterBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_sort_filter, null);
        dialog.setContentView(view);

        ChipGroup chipGroupCategory = view.findViewById(R.id.chipGroupCategory);
        ChipGroup chipGroupSort = view.findViewById(R.id.chipGroupSort);
        ChipGroup chipGroupRating = view.findViewById(R.id.chipGroupRating);

        TextView tvMinPriceValue = view.findViewById(R.id.tvMinPriceValue);
        TextView tvMaxPriceValue = view.findViewById(R.id.tvMaxPriceValue);

        RangeSlider rangePrice = view.findViewById(R.id.rangePrice);
        AppCompatButton btnReset = view.findViewById(R.id.btnReset);
        AppCompatButton btnApply = view.findViewById(R.id.btnApply);

        buildCategoryChips(chipGroupCategory);
        enableCheckableChips(chipGroupSort);
        enableCheckableChips(chipGroupRating);
        syncFilterChips(chipGroupSort, chipGroupRating);

        rangePrice.setValueFrom(0f);
        rangePrice.setValueTo(absoluteMaxPrice);

        float endValue = selectedMaxPrice <= 0f ? absoluteMaxPrice : Math.min(selectedMaxPrice, absoluteMaxPrice);
        float startValue = Math.min(selectedMinPrice, endValue);

        rangePrice.setValues(startValue, endValue);
        updatePriceText(tvMinPriceValue, tvMaxPriceValue, startValue, endValue);

        rangePrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            updatePriceText(tvMinPriceValue, tvMaxPriceValue, values.get(0), values.get(1));
        });

        btnReset.setOnClickListener(v -> {
            selectedCategoryId = null;
            selectedSort = SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT;
            selectedMinPrice = 0f;
            selectedMaxPrice = absoluteMaxPrice;
            selectedMinRating = 0f;

            buildCategoryChips(chipGroupCategory);
            syncFilterChips(chipGroupSort, chipGroupRating);
            rangePrice.setValues(0f, absoluteMaxPrice);
            updatePriceText(tvMinPriceValue, tvMaxPriceValue, 0f, absoluteMaxPrice);
        });

        btnApply.setOnClickListener(v -> {
            selectedCategoryId = readSelectedCategory(chipGroupCategory);
            selectedSort = readSelectedSort(chipGroupSort.getCheckedChipId());
            selectedMinRating = readSelectedRating(chipGroupRating.getCheckedChipId());

            List<Float> values = rangePrice.getValues();
            selectedMinPrice = values.get(0);
            selectedMaxPrice = values.get(1);

            submittedKeyword = edtSearch.getText() == null ? "" : edtSearch.getText().toString().trim();
            isSearchSubmitted = true;
            performSearch();
            dialog.dismiss();
        });

        dialog.show();

        FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void taiDanhSachYeuThich() {
        FavoriteUiHelper.syncFavoriteIds(favoriteRepository, sanPhamAdapter);
    }

    private void buildCategoryChips(ChipGroup chipGroupCategory) {
        chipGroupCategory.removeAllViews();

        Chip allChip = createCategoryChip("Tất cả", null);
        chipGroupCategory.addView(allChip);

        danhMucDB.layTatCaDMActive(new OnFirestoreResult<List<DanhMuc>>() {
            @Override
            public void onSuccess(List<DanhMuc> categories) {
                for (DanhMuc danhMuc : categories) {
                    chipGroupCategory.addView(createCategoryChip(
                            danhMuc.getTenDanhMuc(),
                            danhMuc.getDanhMucId()
                    ));
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
    private Chip createCategoryChip(String label, String tagValue) {
        Chip chip = new Chip(this);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setTag(tagValue);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setCloseIconVisible(false);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setChipBackgroundColor(AppCompatResources.getColorStateList(this, R.color.chip_bg_selector));
        chip.setTextColor(AppCompatResources.getColorStateList(this, R.color.chip_text_selector));
        chip.setChipStrokeWidth(0f);
        chip.setChecked(tagValue == null ? selectedCategoryId == null : tagValue.equals(selectedCategoryId));
        return chip;
    }

    private void checkChipByTag(ChipGroup chipGroup, String tagValue) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                Object tag = chip.getTag();

                if (tagValue == null && tag == null) {
                    chipGroup.check(chip.getId());
                    return;
                }

                if (tag != null && tag.toString().equals(tagValue)) {
                    chipGroup.check(chip.getId());
                    return;
                }
            }
        }
    }

    private String readSelectedCategory(ChipGroup chipGroup) {
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == View.NO_ID) {
            return null;
        }

        Chip chip = chipGroup.findViewById(checkedId);
        if (chip == null || chip.getTag() == null) {
            return null;
        }
        return chip.getTag().toString();
    }

    private String readSelectedSort(int checkedId) {
        if (checkedId == R.id.chipSortPopular) {
            return SanPhamRepository.SORT_SP_BAN_CHAY;
        }
        if (checkedId == R.id.chipSortPriceHigh) {
            return SanPhamRepository.SORT_GIA_CAO_NHAT;
        }
        if (checkedId == R.id.chipSortPriceLow) {
            return SanPhamRepository.SORT_GIA_THAP_NHAT;
        }
        if (checkedId == R.id.chipSortRating) {
            return SanPhamRepository.SORT_XEP_HANG;
        }
        return SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT;
    }

    private float readSelectedRating(int checkedId) {
        if (checkedId == R.id.chipRating5) {
            return 5f;
        }
        if (checkedId == R.id.chipRating4) {
            return 4f;
        }
        if (checkedId == R.id.chipRating3) {
            return 3f;
        }
        if (checkedId == R.id.chipRating2) {
            return 2f;
        }
        return 0f;
    }

    private boolean hasActiveFilter() {
        return selectedCategoryId != null
                || !SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT.equals(selectedSort)
                || selectedMinPrice > 0f
                || selectedMaxPrice < absoluteMaxPrice
                || selectedMinRating > 0f;
    }

    private void syncFilterChips(ChipGroup chipGroupSort, ChipGroup chipGroupRating) {
        chipGroupSort.check(getSortChipId(selectedSort));
        chipGroupRating.check(getRatingChipId(selectedMinRating));
    }

    private void enableCheckableChips(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setCheckable(true);
            }
        }
    }

    private int getSortChipId(String sort) {
        if (SanPhamRepository.SORT_SP_BAN_CHAY.equals(sort)) {
            return R.id.chipSortPopular;
        }
        if (SanPhamRepository.SORT_GIA_CAO_NHAT.equals(sort)) {
            return R.id.chipSortPriceHigh;
        }
        if (SanPhamRepository.SORT_GIA_THAP_NHAT.equals(sort)) {
            return R.id.chipSortPriceLow;
        }
        if (SanPhamRepository.SORT_XEP_HANG.equals(sort)) {
            return R.id.chipSortRating;
        }
        return R.id.chipSortRecent;
    }

    private int getRatingChipId(float rating) {
        if (rating >= 5f) {
            return R.id.chipRating5;
        }
        if (rating >= 4f) {
            return R.id.chipRating4;
        }
        if (rating >= 3f) {
            return R.id.chipRating3;
        }
        if (rating >= 2f) {
            return R.id.chipRating2;
        }
        return R.id.chipRatingAll;
    }

    private void clearSearchResults() {
        dangXoaKetQua = true;
        searchRequestVersion++;
        submittedKeyword = "";
        isSearchSubmitted = false;
        selectedCategoryId = null;
        selectedSort = SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT;
        selectedMinPrice = 0f;
        selectedMaxPrice = absoluteMaxPrice <= 0f ? 5_000_000f : absoluteMaxPrice;
        selectedMinRating = 0f;
        edtSearch.setText("");
        sanPhamAdapter.capNhatDuLieu(new ArrayList<>());
        showRecentState();
        dangXoaKetQua = false;
    }

    private void saveRecentKeyword(String keyword) {
        recentKeywords.remove(keyword);
        recentKeywords.add(0, keyword);

        while (recentKeywords.size() > MAX_RECENT_KEYWORDS) {
            recentKeywords.remove(recentKeywords.size() - 1);
        }

        persistRecentKeywords();
        LichSuTimKiemAdapter.capNhatDuLieu(recentKeywords);
    }

    private void loadRecentKeywords() {
        recentKeywords.clear();
        String json = sharedPreferences.getString(KEY_RECENT_SEARCHES, "[]");

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                recentKeywords.add(jsonArray.getString(i));
            }
        } catch (JSONException ignored) {
        }
    }

    private void persistRecentKeywords() {
        JSONArray jsonArray = new JSONArray();
        for (String keyword : recentKeywords) {
            jsonArray.put(keyword);
        }

        sharedPreferences.edit()
                .putString(KEY_RECENT_SEARCHES, jsonArray.toString())
                .apply();
    }

    private void updatePriceText(TextView tvMinPriceValue, TextView tvMaxPriceValue, float min, float max) {
        tvMinPriceValue.setText(FormatUtils.formatCurrency(min));
        tvMaxPriceValue.setText(FormatUtils.formatCurrency(max));
    }

    private String formatCurrency(double price) {
        return FormatUtils.formatCurrency(price);
    }

    private String formatCount(int count) {
        return FormatUtils.formatCount(count);
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void setupInsets() {
        View root = findViewById(R.id.searchRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
