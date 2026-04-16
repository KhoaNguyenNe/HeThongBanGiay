package com.example.hethongbangiay.activities.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import com.example.hethongbangiay.utils.TrangThaiDonHang;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminReportActivity extends AppCompatActivity {

    private static final int IDX_CHO_XAC_NHAN = 0;
    private static final int IDX_DA_XAC_NHAN = 1;
    private static final int IDX_DANG_GIAO = 2;
    private static final int IDX_DA_GIAO = 3;
    private static final int IDX_DA_HUY = 4;

    private static final String[] ORDER_STATUS_LABELS = new String[]{
            "Chờ xác nhận",
            "Đã xác nhận",
            "Đang giao",
            "Đã giao",
            "Đã hủy"
    };

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserRepository userRepository = new UserRepository();

    private TextView tvReportRole;
    private TextView tvTopProductsInsight;
    private TextView tvRevenueInsight;
    private TextView tvLastUpdated;

    private Spinner spinnerTopMonth;
    private Spinner spinnerRevenueMonth;

    private TableLayout tableTopProducts;
    private TableLayout tableRevenue;

    private BarChart chartTopProducts;
    private BarChart chartRevenue;

    private ProgressBar progressReport;
    private Button btnRefreshReport;

    private NumberFormat moneyFormatter;
    private ReportSummary cachedSummary;

    private final ValueFormatter intValueFormatter = new ValueFormatter() {
        @Override
        public String getBarLabel(BarEntry barEntry) {
            return String.valueOf((int) barEntry.getY());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        moneyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        moneyFormatter.setMaximumFractionDigits(0);

        bindViews();
        setupCharts();
        setupMonthSpinners();

        btnRefreshReport.setOnClickListener(v -> loadReportMetrics());

        validatePermissionAndLoad();
    }

    private void bindViews() {
        tvReportRole = findViewById(R.id.tvReportRole);
        tvTopProductsInsight = findViewById(R.id.tvTopProductsInsight);
        tvRevenueInsight = findViewById(R.id.tvRevenueInsight);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);

        spinnerTopMonth = findViewById(R.id.spinnerTopMonth);
        spinnerRevenueMonth = findViewById(R.id.spinnerRevenueMonth);

        tableTopProducts = findViewById(R.id.tableTopProducts);
        tableRevenue = findViewById(R.id.tableRevenue);

        chartTopProducts = findViewById(R.id.chartTopProducts);
        chartRevenue = findViewById(R.id.chartRevenue);

        progressReport = findViewById(R.id.progressReport);
        btnRefreshReport = findViewById(R.id.btnRefreshReport);
    }

    private void setupCharts() {
        setupBarChart(chartTopProducts);
        setupBarChart(chartRevenue);
    }

    private void setupMonthSpinners() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add("Tháng " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTopMonth.setAdapter(adapter);
        spinnerRevenueMonth.setAdapter(adapter);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerTopMonth.setSelection(currentMonth, false);
        spinnerRevenueMonth.setSelection(currentMonth, false);

        spinnerTopMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cachedSummary != null) {
                    renderTopProductsSection(position, cachedSummary);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });

        spinnerRevenueMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cachedSummary != null) {
                    renderRevenueSection(position, cachedSummary);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });
    }

    private void validatePermissionAndLoad() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    NguoiDung profile;
                    try {
                        profile = documentSnapshot.toObject(NguoiDung.class);
                    } catch (RuntimeException ex) {
                        Toast.makeText(this, "Lỗi đọc hồ sơ: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    String role = RoleUtils.normalizeRole(profile != null ? profile.getVaiTro() : null);
                    if (!RoleUtils.canViewReports(role)) {
                        Toast.makeText(this, "Bạn không có quyền xem báo cáo", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    tvReportRole.setText("Vai trò: " + role);
                    loadReportMetrics();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không xác thực được quyền: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void loadReportMetrics() {
        setLoading(true);

        Task<QuerySnapshot> ordersTask = db.collection("DonHang").get();
        Task<QuerySnapshot> orderDetailsTask = db.collection("ChiTietDonHang").get();

        Tasks.whenAllComplete(ordersTask, orderDetailsTask)
                .addOnSuccessListener(unused -> {
                    if (!ordersTask.isSuccessful()) {
                        handleLoadFailure(ordersTask.getException());
                        return;
                    }

                    if (!orderDetailsTask.isSuccessful()) {
                        handleLoadFailure(orderDetailsTask.getException());
                        return;
                    }

                    QuerySnapshot orderSnapshot = ordersTask.getResult();
                    QuerySnapshot orderDetailSnapshot = orderDetailsTask.getResult();
                    if (orderSnapshot == null || orderDetailSnapshot == null) {
                        handleLoadFailure(new IllegalStateException("Không đọc được dữ liệu đơn hàng"));
                        return;
                    }

                    cachedSummary = buildSummary(orderSnapshot.getDocuments(), orderDetailSnapshot.getDocuments());
                    bindSummary(cachedSummary);
                    setLoading(false);
                })
                .addOnFailureListener(this::handleLoadFailure);
    }

    private void handleLoadFailure(Exception error) {
        setLoading(false);
        String message = error != null ? error.getLocalizedMessage() : "unknown error";
        Toast.makeText(this, "Không tải được báo cáo: " + message, Toast.LENGTH_LONG).show();
    }

    private ReportSummary buildSummary(List<DocumentSnapshot> orders, List<DocumentSnapshot> orderDetails) {
        ReportSummary summary = new ReportSummary();
        Map<String, List<OrderItem>> itemsByOrderId = groupOrderItemsByOrderId(orderDetails);

        List<Map<String, ProductSale>> monthlySaleMaps = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthlySaleMaps.add(new HashMap<>());
        }

        for (DocumentSnapshot orderDoc : orders) {
            String orderId = resolveOrderId(orderDoc);
            String status = normalizeOrderStatus(orderDoc.getString("tinhTrangDonHang"));
            int statusIndex = statusToIndex(status);
            int monthIndex = resolveMonthIndex(orderDoc);

            List<OrderItem> itemsFromDetailCollection = itemsByOrderId.get(orderId);
            List<OrderItem> itemsFromOrderDoc = extractOrderItems(orderDoc);
            List<OrderItem> items = hasItems(itemsFromDetailCollection)
                    ? itemsFromDetailCollection
                    : itemsFromOrderDoc;

            double orderTotal = resolveOrderTotal(orderDoc, items);

            if (monthIndex >= 0) {
                MonthRevenue monthRevenue = summary.revenueByMonth.get(monthIndex);
                monthRevenue.totalOrders++;
                monthRevenue.totalRevenue += orderTotal;
                monthRevenue.revenueByStatus[statusIndex] += orderTotal;
                monthRevenue.orderCountByStatus[statusIndex]++;
            }

            if (!TrangThaiDonHang.DA_HUY.equals(status) && monthIndex >= 0) {
                for (OrderItem item : items) {
                    updateProductSale(monthlySaleMaps.get(monthIndex), item);
                }
            }
        }

        for (int month = 0; month < 12; month++) {
            summary.topProductsByMonth.set(month, toTopN(monthlySaleMaps.get(month), 5));
        }

        return summary;
    }

    private Map<String, List<OrderItem>> groupOrderItemsByOrderId(List<DocumentSnapshot> orderDetails) {
        Map<String, List<OrderItem>> grouped = new HashMap<>();
        if (orderDetails == null) {
            return grouped;
        }

        for (DocumentSnapshot detailDoc : orderDetails) {
            String orderId = readStringObject(detailDoc.get("donHangId"), "");
            if (orderId.isEmpty()) {
                continue;
            }

            int quantity = readIntObject(detailDoc.get("soLuong"), 0);
            if (quantity <= 0) {
                continue;
            }

            String productId = readStringObject(detailDoc.get("sanPhamId"), "");
            String productName = readStringObject(detailDoc.get("tenSanPham"), "Sản phẩm");
            double unitPrice = readDoubleObject(detailDoc.get("giaTien"), 0d);

            grouped
                    .computeIfAbsent(orderId, key -> new ArrayList<>())
                    .add(new OrderItem(productId, productName, quantity, unitPrice));
        }

        return grouped;
    }

    private String resolveOrderId(DocumentSnapshot orderDoc) {
        String orderId = readStringObject(orderDoc.get("donHangId"), "");
        if (!orderId.isEmpty()) {
            return orderId;
        }

        String docId = orderDoc.getId();
        return docId == null ? "" : docId.trim();
    }

    private boolean hasItems(List<OrderItem> items) {
        return items != null && !items.isEmpty();
    }

    private List<OrderItem> extractOrderItems(DocumentSnapshot orderDoc) {
        List<OrderItem> items = new ArrayList<>();
        Object raw = orderDoc.get("chiTietSanPham");
        if (!(raw instanceof List<?>)) {
            return items;
        }

        for (Object element : (List<?>) raw) {
            if (!(element instanceof Map<?, ?>)) {
                continue;
            }

            Map<?, ?> itemMap = (Map<?, ?>) element;
            String productId = readStringObject(itemMap.get("sanPhamId"), "");
            String productName = readStringObject(itemMap.get("tenSanPham"), "Sản phẩm");
            int quantity = readIntObject(itemMap.get("soLuong"), 0);
            double unitPrice = readDoubleObject(itemMap.get("giaTien"), 0d);

            if (quantity <= 0) {
                continue;
            }

            items.add(new OrderItem(productId, productName, quantity, unitPrice));
        }

        return items;
    }

    private double resolveOrderTotal(DocumentSnapshot orderDoc, List<OrderItem> orderItems) {
        double persistedTotal = readDoubleObject(orderDoc.get("tongTien"), -1d);
        if (persistedTotal > 0d) {
            return persistedTotal;
        }

        double fallbackTotal = 0d;
        for (OrderItem item : orderItems) {
            fallbackTotal += item.unitPrice * item.quantity;
        }
        return Math.max(fallbackTotal, 0d);
    }

    private int resolveMonthIndex(DocumentSnapshot orderDoc) {
        Timestamp timestamp = orderDoc.getTimestamp("ngayDatHang");
        Calendar calendar = Calendar.getInstance();

        if (timestamp != null) {
            calendar.setTime(timestamp.toDate());
            return calendar.get(Calendar.MONTH);
        }

        Object rawDate = orderDoc.get("ngayDatHang");
        if (rawDate instanceof Date) {
            calendar.setTime((Date) rawDate);
            return calendar.get(Calendar.MONTH);
        }

        if (rawDate instanceof Number) {
            calendar.setTimeInMillis(((Number) rawDate).longValue());
            return calendar.get(Calendar.MONTH);
        }

        return -1;
    }

    private void updateProductSale(Map<String, ProductSale> saleMap, OrderItem item) {
        String key = item.productId != null && !item.productId.isEmpty() ? item.productId : item.productName;
        ProductSale existing = saleMap.get(key);
        if (existing == null) {
            saleMap.put(key, new ProductSale(item.productName, item.quantity));
            return;
        }
        existing.soldQuantity += item.quantity;
    }

    private List<ProductSale> toTopN(Map<String, ProductSale> saleMap, int n) {
        List<ProductSale> ranked = new ArrayList<>(saleMap.values());
        ranked.sort((a, b) -> Integer.compare(b.soldQuantity, a.soldQuantity));

        int limit = Math.min(n, ranked.size());
        List<ProductSale> top = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            top.add(ranked.get(i));
        }
        return top;
    }

    private String normalizeOrderStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) {
            return TrangThaiDonHang.CHO_XAC_NHAN;
        }

        String status = rawStatus.trim().toUpperCase(Locale.ROOT);

        if (TrangThaiDonHang.CHO_XAC_NHAN.equals(status)
                || TrangThaiDonHang.DA_XAC_NHAN.equals(status)
                || TrangThaiDonHang.DANG_GIAO.equals(status)
                || TrangThaiDonHang.DA_GIAO.equals(status)
                || TrangThaiDonHang.DA_HUY.equals(status)) {
            return status;
        }

        if (status.contains("HUY") || status.contains("CANCEL")) {
            return TrangThaiDonHang.DA_HUY;
        }
        if (status.contains("DANG_GIAO") || status.contains("DANG GIAO")) {
            return TrangThaiDonHang.DANG_GIAO;
        }
        if (status.contains("DA_XAC_NHAN") || status.contains("DA XAC NHAN")) {
            return TrangThaiDonHang.DA_XAC_NHAN;
        }
        if (status.contains("DA_GIAO")
                || status.contains("DA GIAO")
                || status.contains("HOAN_THANH")
                || status.contains("HOAN THANH")
                || status.contains("THANH_CONG")
                || status.contains("THANH CONG")
                || status.contains("DONE")
                || status.contains("COMPLETE")
                || status.contains("SUCCESS")) {
            return TrangThaiDonHang.DA_GIAO;
        }

        return TrangThaiDonHang.CHO_XAC_NHAN;
    }

    private int statusToIndex(String status) {
        if (TrangThaiDonHang.DA_XAC_NHAN.equals(status)) {
            return IDX_DA_XAC_NHAN;
        }
        if (TrangThaiDonHang.DANG_GIAO.equals(status)) {
            return IDX_DANG_GIAO;
        }
        if (TrangThaiDonHang.DA_GIAO.equals(status)) {
            return IDX_DA_GIAO;
        }
        if (TrangThaiDonHang.DA_HUY.equals(status)) {
            return IDX_DA_HUY;
        }
        return IDX_CHO_XAC_NHAN;
    }

    private void bindSummary(ReportSummary summary) {
        int topMonth = spinnerTopMonth.getSelectedItemPosition();
        int revenueMonth = spinnerRevenueMonth.getSelectedItemPosition();

        if (topMonth < 0 || topMonth > 11) {
            topMonth = Calendar.getInstance().get(Calendar.MONTH);
            spinnerTopMonth.setSelection(topMonth, false);
        }
        if (revenueMonth < 0 || revenueMonth > 11) {
            revenueMonth = Calendar.getInstance().get(Calendar.MONTH);
            spinnerRevenueMonth.setSelection(revenueMonth, false);
        }

        renderTopProductsSection(topMonth, summary);
        renderRevenueSection(revenueMonth, summary);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvLastUpdated.setText("Cập nhật lúc: " + dateFormat.format(new Date()));
    }

    private void renderTopProductsSection(int monthIndex, ReportSummary summary) {
        List<ProductSale> topProducts = summary.topProductsByMonth.get(monthIndex);
        renderTopProductsTable(topProducts);
        renderTopProductsChart(topProducts);
        tvTopProductsInsight.setText(buildTopProductsInsight(monthIndex, topProducts));
    }

    private void renderRevenueSection(int monthIndex, ReportSummary summary) {
        MonthRevenue monthRevenue = summary.revenueByMonth.get(monthIndex);
        renderRevenueTable(monthRevenue);
        renderRevenueChart(monthRevenue);
        tvRevenueInsight.setText(buildRevenueInsight(monthIndex, monthRevenue));
    }

    private void renderTopProductsTable(List<ProductSale> topProducts) {
        tableTopProducts.removeAllViews();
        tableTopProducts.addView(createHeaderRow("Hạng", "Sản phẩm", "Số lượng"));

        if (topProducts.isEmpty()) {
            tableTopProducts.addView(createBodyRow("-", "Không có dữ liệu", "0", false));
            return;
        }

        for (int i = 0; i < topProducts.size(); i++) {
            ProductSale item = topProducts.get(i);
            tableTopProducts.addView(createBodyRow(
                    String.valueOf(i + 1),
                    item.productName,
                    String.valueOf(item.soldQuantity),
                    false
            ));
        }
    }

    private void renderRevenueTable(MonthRevenue revenue) {
        tableRevenue.removeAllViews();
        tableRevenue.addView(createHeaderRow("Trạng thái", "Số đơn", "Doanh thu"));

        for (int i = 0; i < ORDER_STATUS_LABELS.length; i++) {
            tableRevenue.addView(createBodyRow(
                    ORDER_STATUS_LABELS[i],
                    String.valueOf(revenue.orderCountByStatus[i]),
                    formatMoney(revenue.revenueByStatus[i]),
                    false
            ));
        }

        tableRevenue.addView(createBodyRow(
                "Tổng tháng",
                String.valueOf(revenue.totalOrders),
                formatMoney(revenue.totalRevenue),
                true
        ));
    }

    private TableRow createHeaderRow(String c1, String c2, String c3) {
        return createTableRow(c1, c2, c3, true, false);
    }

    private TableRow createBodyRow(String c1, String c2, String c3, boolean highlight) {
        return createTableRow(c1, c2, c3, false, highlight);
    }

    private TableRow createTableRow(String c1, String c2, String c3, boolean isHeader, boolean highlight) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 6, 0, 6);
        if (highlight) {
            row.setBackgroundColor(Color.parseColor("#EDF7F4"));
        }

        row.addView(createCell(c1, isHeader, false));
        row.addView(createCell(c2, isHeader, false));
        row.addView(createCell(c3, isHeader, true));
        return row;
    }

    private TextView createCell(String text, boolean isHeader, boolean alignEnd) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setTextSize(isHeader ? 13f : 12f);
        cell.setTextColor(isHeader ? Color.parseColor("#0F172A") : Color.parseColor("#334155"));
        cell.setTypeface(cell.getTypeface(), isHeader ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        cell.setPadding(8, 6, 8, 6);
        cell.setGravity(alignEnd ? Gravity.END : Gravity.START);

        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, alignEnd ? 0.9f : 1.6f);
        cell.setLayoutParams(lp);
        return cell;
    }

    private void renderTopProductsChart(List<ProductSale> topProducts) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < topProducts.size(); i++) {
            ProductSale item = topProducts.get(i);
            entries.add(new BarEntry(i, item.soldQuantity));
            labels.add(shortenLabel(item.productName));
        }

        if (entries.isEmpty()) {
            entries.add(new BarEntry(0f, 0f));
            labels.add("No data");
        }

        BarDataSet dataSet = new BarDataSet(entries, "Top sản phẩm");
        dataSet.setColor(Color.parseColor("#1D4ED8"));
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueFormatter(intValueFormatter);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.55f);

        configureIndexedXAxis(chartTopProducts, labels, -18f);
        chartTopProducts.setData(data);
        chartTopProducts.animateY(500);
        chartTopProducts.invalidate();
    }

    private void renderRevenueChart(MonthRevenue revenue) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < ORDER_STATUS_LABELS.length; i++) {
            float valueInMillion = (float) (revenue.revenueByStatus[i] / 1_000_000d);
            entries.add(new BarEntry(i, valueInMillion));
            labels.add(ORDER_STATUS_LABELS[i]);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (triệu VND)");
        dataSet.setColors(
                Color.parseColor("#F59E0B"),
                Color.parseColor("#0EA5E9"),
                Color.parseColor("#F97316"),
                Color.parseColor("#16A34A"),
                Color.parseColor("#EF4444")
        );
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return barEntry.getY() > 0f ? String.format(Locale.US, "%.1f", barEntry.getY()) : "";
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        configureIndexedXAxis(chartRevenue, labels, -25f);
        chartRevenue.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                return String.format(Locale.US, "%.0f", value);
            }
        });
        chartRevenue.setData(data);
        chartRevenue.animateY(500);
        chartRevenue.invalidate();
    }

    private String buildTopProductsInsight(int monthIndex, List<ProductSale> topProducts) {
        if (topProducts.isEmpty()) {
            return String.format(Locale.US, "Tháng %d chưa có dữ liệu bán hàng.", monthIndex + 1);
        }

        ProductSale top1 = topProducts.get(0);
        return String.format(Locale.US,
                "Tháng %d: đứng đầu là %s (%d sản phẩm).",
                monthIndex + 1,
                top1.productName,
                top1.soldQuantity
        );
    }

    private String buildRevenueInsight(int monthIndex, MonthRevenue revenue) {
        if (revenue.totalRevenue <= 0d) {
            return String.format(Locale.US,
                    "Tháng %d chưa có doanh thu.",
                    monthIndex + 1
            );
        }

        int maxStatus = IDX_CHO_XAC_NHAN;
        double maxRevenue = revenue.revenueByStatus[IDX_CHO_XAC_NHAN];
        for (int i = 1; i < revenue.revenueByStatus.length; i++) {
            if (revenue.revenueByStatus[i] > maxRevenue) {
                maxRevenue = revenue.revenueByStatus[i];
                maxStatus = i;
            }
        }

        return String.format(Locale.US,
                "Tháng %d: tổng %s, cao nhất ở trạng thái %s (%s).",
                monthIndex + 1,
                formatMoney(revenue.totalRevenue),
                ORDER_STATUS_LABELS[maxStatus],
                formatMoney(maxRevenue)
        );
    }

    private void setupBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setNoDataText("Không có dữ liệu");
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setDrawGridLines(true);
    }

    private void configureIndexedXAxis(BarChart chart, List<String> labels, float rotationAngle) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size(), false);
        xAxis.setLabelRotationAngle(rotationAngle);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(labels.size() - 0.5f);
        xAxis.setCenterAxisLabels(false);

        chart.setExtraBottomOffset(rotationAngle == 0f ? 6f : 18f);
    }

    private int readIntObject(Object raw, int defaultValue) {
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }

        if (raw instanceof String) {
            try {
                return Integer.parseInt(((String) raw).trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    private double readDoubleObject(Object raw, double defaultValue) {
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }

        if (raw instanceof String) {
            try {
                String normalized = ((String) raw).trim().replace(",", "");
                return Double.parseDouble(normalized);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    private String readStringObject(Object raw, String defaultValue) {
        if (raw == null) {
            return defaultValue;
        }

        String value = String.valueOf(raw).trim();
        return value.isEmpty() ? defaultValue : value;
    }

    private String shortenLabel(String name) {
        if (name == null) {
            return "SP";
        }

        String normalized = name.trim();
        if (normalized.length() <= 11) {
            return normalized;
        }
        return normalized.substring(0, 10) + "...";
    }

    private String formatMoney(double value) {
        return moneyFormatter.format(Math.max(value, 0d)) + " VND";
    }

    private void setLoading(boolean isLoading) {
        progressReport.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRefreshReport.setEnabled(!isLoading);
    }

    private static class ReportSummary {
        List<List<ProductSale>> topProductsByMonth = new ArrayList<>();
        List<MonthRevenue> revenueByMonth = new ArrayList<>();

        ReportSummary() {
            for (int i = 0; i < 12; i++) {
                topProductsByMonth.add(new ArrayList<>());
                revenueByMonth.add(new MonthRevenue());
            }
        }
    }

    private static class MonthRevenue {
        double totalRevenue;
        int totalOrders;
        double[] revenueByStatus = new double[5];
        int[] orderCountByStatus = new int[5];
    }

    private static class OrderItem {
        String productId;
        String productName;
        int quantity;
        double unitPrice;

        OrderItem(String productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }

    private static class ProductSale {
        String productName;
        int soldQuantity;

        ProductSale(String productName, int soldQuantity) {
            this.productName = productName;
            this.soldQuantity = soldQuantity;
        }
    }
}
