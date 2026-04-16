package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReportActivity extends AppCompatActivity {

    private final UserRepository userRepository = new UserRepository();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView tvReportRole;
    private TextView tvTotalUsers;
    private TextView tvTotalAdmins;
    private TextView tvActiveUsers;
    private TextView tvLockedUsers;
    private TextView tvDeletedUsers;
    private TextView tvTotalOrders;
    private TextView tvPendingOrders;
    private TextView tvCompletedOrders;
    private TextView tvCancelledOrders;
    private TextView tvLastUpdated;
    private ProgressBar progressReport;
    private Button btnRefreshReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        bindViews();
        btnRefreshReport.setOnClickListener(v -> loadReportMetrics());

        validatePermissionAndLoad();
    }

    private void bindViews() {
        tvReportRole = findViewById(R.id.tvReportRole);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalAdmins = findViewById(R.id.tvTotalAdmins);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvLockedUsers = findViewById(R.id.tvLockedUsers);
        tvDeletedUsers = findViewById(R.id.tvDeletedUsers);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvCompletedOrders = findViewById(R.id.tvCompletedOrders);
        tvCancelledOrders = findViewById(R.id.tvCancelledOrders);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        progressReport = findViewById(R.id.progressReport);
        btnRefreshReport = findViewById(R.id.btnRefreshReport);
    }

    private void validatePermissionAndLoad() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Ban chua dang nhap", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Loi doc ho so: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    String role = RoleUtils.normalizeRole(profile != null ? profile.getVaiTro() : null);
                    if (!RoleUtils.canViewReports(role)) {
                        Toast.makeText(this, "Ban khong co quyen xem Report", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    tvReportRole.setText("Role: " + role);
                    loadReportMetrics();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Khong xac thuc duoc quyen: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void loadReportMetrics() {
        setLoading(true);

        Task<QuerySnapshot> usersTask = db.collection("NguoiDung").get();
        Task<QuerySnapshot> ordersTask = db.collection("DonHang").get();

        Tasks.whenAllComplete(usersTask, ordersTask)
                .addOnCompleteListener(task -> {
                    if (!usersTask.isSuccessful()) {
                        handleLoadFailure(usersTask.getException());
                        return;
                    }

                    if (!ordersTask.isSuccessful()) {
                        handleLoadFailure(ordersTask.getException());
                        return;
                    }

                    ReportSummary summary = buildSummary(usersTask.getResult(), ordersTask.getResult());
                    bindSummary(summary);
                    setLoading(false);
                });
    }

    private void handleLoadFailure(Exception error) {
        setLoading(false);
        String message = error != null ? error.getLocalizedMessage() : "unknown error";
        Toast.makeText(this, "Khong tai duoc bao cao: " + message, Toast.LENGTH_LONG).show();
    }

    private ReportSummary buildSummary(QuerySnapshot userSnapshot, QuerySnapshot orderSnapshot) {
        ReportSummary summary = new ReportSummary();

        if (userSnapshot != null) {
            summary.totalUsers = userSnapshot.size();

            for (DocumentSnapshot userDoc : userSnapshot.getDocuments()) {
                String role = RoleUtils.normalizeRole(userDoc.getString("vaiTro"));
                if (RoleUtils.isAdminRole(role)) {
                    summary.totalAdmins++;
                }

                boolean deleted = readBoolean(userDoc, "deleted", "isDeleted", false);
                boolean active = readBoolean(userDoc, "active", "isActive", true);
                boolean locked = readBoolean(userDoc, "locked", "isLocked", false);

                if (deleted) {
                    summary.deletedUsers++;
                }
                if (active && !deleted) {
                    summary.activeUsers++;
                }
                if (locked && !deleted) {
                    summary.lockedUsers++;
                }
            }
        }

        if (orderSnapshot != null) {
            summary.totalOrders = orderSnapshot.size();

            for (DocumentSnapshot orderDoc : orderSnapshot.getDocuments()) {
                String statusGroup = normalizeOrderStatus(orderDoc.getString("tinhTrangDonHang"));
                if ("completed".equals(statusGroup)) {
                    summary.completedOrders++;
                } else if ("cancelled".equals(statusGroup)) {
                    summary.cancelledOrders++;
                } else {
                    summary.pendingOrders++;
                }
            }
        }

        return summary;
    }

    private boolean readBoolean(DocumentSnapshot document, String key, String legacyKey, boolean defaultValue) {
        Boolean value = document.getBoolean(key);
        if (value != null) {
            return value;
        }

        value = document.getBoolean(legacyKey);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    private String normalizeOrderStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) {
            return "pending";
        }

        String status = rawStatus.trim().toLowerCase(Locale.ROOT);
        if (status.contains("huy") || status.contains("cancel")) {
            return "cancelled";
        }

        if (status.contains("giao")
                || status.contains("hoan thanh")
                || status.contains("thanh cong")
                || status.contains("done")
                || status.contains("complete")
                || status.contains("success")) {
            return "completed";
        }

        return "pending";
    }

    private void bindSummary(ReportSummary summary) {
        tvTotalUsers.setText(String.valueOf(summary.totalUsers));
        tvTotalAdmins.setText(String.valueOf(summary.totalAdmins));
        tvActiveUsers.setText(String.valueOf(summary.activeUsers));
        tvLockedUsers.setText(String.valueOf(summary.lockedUsers));
        tvDeletedUsers.setText(String.valueOf(summary.deletedUsers));

        tvTotalOrders.setText(String.valueOf(summary.totalOrders));
        tvPendingOrders.setText(String.valueOf(summary.pendingOrders));
        tvCompletedOrders.setText(String.valueOf(summary.completedOrders));
        tvCancelledOrders.setText(String.valueOf(summary.cancelledOrders));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvLastUpdated.setText("Cap nhat luc: " + dateFormat.format(new Date()));
    }

    private void setLoading(boolean isLoading) {
        progressReport.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRefreshReport.setEnabled(!isLoading);
    }

    private static class ReportSummary {
        int totalUsers;
        int totalAdmins;
        int activeUsers;
        int lockedUsers;
        int deletedUsers;
        int totalOrders;
        int pendingOrders;
        int completedOrders;
        int cancelledOrders;
    }
}
