package com.example.hethongbangiay.activities.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminUserAdapter;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.models.VaiTro;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminUserManagementActivity extends AppCompatActivity {

    private static final String FILTER_ROLE_ALL = "Tất cả vai trò";
    private static final String FILTER_STATUS_ALL = "Tất cả trạng thái";
    private static final String FILTER_STATUS_ACTIVE = "Đang hoạt động";
    private static final String FILTER_STATUS_LOCKED = "Đã khóa";
    private static final String FILTER_STATUS_DELETED = "Đã xóa mềm";

    private TextInputEditText edtSearchUser;
    private Spinner spRoleFilter;
    private Spinner spStatusFilter;
    private AppCompatButton btnAddUser;
    private RecyclerView rvUsers;
    private View progressUsers;
    private TextView tvEmptyUsers;

    private UserRepository userRepository;
    private AdminUserAdapter adminUserAdapter;
    private final List<NguoiDung> allUsers = new ArrayList<>();
    private String currentAdminRole = VaiTro.USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_management);

        userRepository = new UserRepository();

        initViews();
        initRecyclerView();
        initFilters();
        initActions();
        validatePermissionAndLoad();
    }

    private void initViews() {
        edtSearchUser = findViewById(R.id.edtSearchUser);
        spRoleFilter = findViewById(R.id.spRoleFilter);
        spStatusFilter = findViewById(R.id.spStatusFilter);
        btnAddUser = findViewById(R.id.btnAddUser);
        rvUsers = findViewById(R.id.rvUsers);
        progressUsers = findViewById(R.id.progressUsers);
        tvEmptyUsers = findViewById(R.id.tvEmptyUsers);
    }

    private void initRecyclerView() {
        adminUserAdapter = new AdminUserAdapter(new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(NguoiDung user) {
                showEditUserDialog(user);
            }

            @Override
            public void onChangeRole(NguoiDung user) {
                showRoleDialog(user);
            }

            @Override
            public void onToggleLock(NguoiDung user) {
                toggleLockUser(user);
            }

            @Override
            public void onDelete(NguoiDung user) {
                confirmDeleteUser(user);
            }
        });

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adminUserAdapter);
    }

    private void initFilters() {
        List<String> roleFilters = new ArrayList<>();
        roleFilters.add(FILTER_ROLE_ALL);
        roleFilters.addAll(RoleUtils.getAssignableRoles());

        ArrayAdapter<String> roleFilterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roleFilters
        );
        roleFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoleFilter.setAdapter(roleFilterAdapter);

        List<String> statusFilters = Arrays.asList(
                FILTER_STATUS_ALL,
                FILTER_STATUS_ACTIVE,
                FILTER_STATUS_LOCKED,
                FILTER_STATUS_DELETED
        );
        ArrayAdapter<String> statusFilterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusFilters
        );
        statusFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusFilter.setAdapter(statusFilterAdapter);
    }

    private void initActions() {
        edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spRoleFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        spStatusFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnAddUser.setOnClickListener(v -> showCreateUserDialog());
    }

    private void validatePermissionAndLoad() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setLoading(true);

        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        setLoading(false);
                        Toast.makeText(this, "Không tìm thấy hồ sơ quản trị", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    NguoiDung currentUser;
                    try {
                        currentUser = documentSnapshot.toObject(NguoiDung.class);
                    } catch (RuntimeException ex) {
                        setLoading(false);
                        Toast.makeText(this, "Lỗi dữ liệu hồ sơ quản trị: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    currentAdminRole = RoleUtils.normalizeRole(currentUser != null ? currentUser.getVaiTro() : null);

                    if (!RoleUtils.canManageUsers(currentAdminRole)) {
                        setLoading(false);
                        Toast.makeText(this, "Bạn không có quyền quản lý người dùng", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    applyActionPermissions();
                    loadUsers();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi xác thực quyền: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void loadUsers() {
        setLoading(true);

        userRepository.getAllUsers().addOnSuccessListener(querySnapshot -> {
            allUsers.clear();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                NguoiDung user;
                try {
                    user = doc.toObject(NguoiDung.class);
                } catch (RuntimeException ignored) {
                    // Skip malformed records instead of crashing the management screen.
                    continue;
                }
                if (user != null) {
                    if (user.getUid() == null || user.getUid().trim().isEmpty()) {
                        user.setUid(doc.getId());
                    }
                    user.setVaiTro(RoleUtils.normalizeRole(user.getVaiTro()));
                    allUsers.add(user);
                }
            }

            Collections.sort(allUsers, Comparator.comparingLong(this::safeUpdatedAt).reversed());
            applyFilters();
            setLoading(false);
        }).addOnFailureListener(e -> {
            setLoading(false);
            Toast.makeText(this, "Lỗi tải danh sách user", Toast.LENGTH_SHORT).show();
        });
    }

    private void applyFilters() {
        String keyword = edtSearchUser.getText() != null
                ? edtSearchUser.getText().toString().trim().toLowerCase()
                : "";
        String selectedRole = spRoleFilter.getSelectedItem() != null
                ? spRoleFilter.getSelectedItem().toString()
                : FILTER_ROLE_ALL;
        String selectedStatus = spStatusFilter.getSelectedItem() != null
                ? spStatusFilter.getSelectedItem().toString()
                : FILTER_STATUS_ALL;

        List<NguoiDung> filtered = new ArrayList<>();

        for (NguoiDung user : allUsers) {
            if (!matchesKeyword(user, keyword)) {
                continue;
            }
            if (!matchesRole(user, selectedRole)) {
                continue;
            }
            if (!matchesStatus(user, selectedStatus)) {
                continue;
            }
            filtered.add(user);
        }

        adminUserAdapter.submitList(filtered);
        tvEmptyUsers.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matchesKeyword(NguoiDung user, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }

        String name = user.getHoTen() != null ? user.getHoTen().toLowerCase() : "";
        String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
        String phone = user.getSoDienThoai() != null ? user.getSoDienThoai().toLowerCase() : "";

        return name.contains(keyword) || email.contains(keyword) || phone.contains(keyword);
    }

    private boolean matchesRole(NguoiDung user, String selectedRole) {
        if (FILTER_ROLE_ALL.equals(selectedRole)) {
            return true;
        }
        return RoleUtils.normalizeRole(user.getVaiTro()).equals(selectedRole);
    }

    private boolean matchesStatus(NguoiDung user, String selectedStatus) {
        if (FILTER_STATUS_ALL.equals(selectedStatus)) {
            return true;
        }

        switch (selectedStatus) {
            case FILTER_STATUS_ACTIVE:
                return !user.isAccountDeleted() && !user.isAccountLocked() && user.isAccountActive();
            case FILTER_STATUS_LOCKED:
                return !user.isAccountDeleted() && user.isAccountLocked();
            case FILTER_STATUS_DELETED:
                return user.isAccountDeleted();
            default:
                return true;
        }
    }

    private void showCreateUserDialog() {
        if (!canCreateUser()) {
            Toast.makeText(this, "Bạn không có quyền thêm người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_user_form, null);

        TextInputEditText edtName = dialogView.findViewById(R.id.edtDialogUserName);
        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtDialogUserEmail);
        TextInputEditText edtPhone = dialogView.findViewById(R.id.edtDialogUserPhone);
        TextInputEditText edtAvatar = dialogView.findViewById(R.id.edtDialogUserAvatar);
        Spinner spRole = dialogView.findViewById(R.id.spDialogRole);
        SwitchMaterial swActive = dialogView.findViewById(R.id.swDialogActive);

        List<String> assignableRoles = getAssignableRolesByCurrentAdmin();
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                assignableRoles
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
        swActive.setChecked(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm người dùng")
                .setView(dialogView)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String hoTen = getTextValue(edtName);
            String email = getTextValue(edtEmail);
            String soDienThoai = getTextValue(edtPhone);
                String avatar = getTextValue(edtAvatar);
            String role = spRole.getSelectedItem() != null
                    ? spRole.getSelectedItem().toString()
                    : VaiTro.USER;

            if (hoTen.isEmpty()) {
                edtName.setError("Nhập họ tên");
                edtName.requestFocus();
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                edtEmail.requestFocus();
                return;
            }

            long now = System.currentTimeMillis();
            boolean active = swActive.isChecked();

            NguoiDung newUser = new NguoiDung(
                    "",
                    email,
                    hoTen,
                    soDienThoai,
                    avatar,
                    role,
                    !active,
                    active,
                    false,
                    now,
                    now,
                    null
            );

            setLoading(true);
            userRepository.createUserByAdmin(newUser)
                    .addOnSuccessListener(unused -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Đã thêm người dùng", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(this, "Thêm user thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    });
        }));

        dialog.show();
    }

    private void showEditUserDialog(NguoiDung user) {
        if (!canEditUserInfo()) {
            Toast.makeText(this, "Bạn không có quyền sửa thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_user_form, null);

        TextInputEditText edtName = dialogView.findViewById(R.id.edtDialogUserName);
        TextInputEditText edtEmail = dialogView.findViewById(R.id.edtDialogUserEmail);
        TextInputEditText edtPhone = dialogView.findViewById(R.id.edtDialogUserPhone);
        TextInputEditText edtAvatar = dialogView.findViewById(R.id.edtDialogUserAvatar);
        Spinner spRole = dialogView.findViewById(R.id.spDialogRole);
        SwitchMaterial swActive = dialogView.findViewById(R.id.swDialogActive);

        edtName.setText(user.getHoTen());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getSoDienThoai());
        edtAvatar.setText(user.getAvatar());
        swActive.setChecked(user.isAccountActive() && !user.isAccountLocked() && !user.isAccountDeleted());

        List<String> assignableRoles = getAssignableRolesByCurrentAdmin();
        if (!assignableRoles.contains(RoleUtils.normalizeRole(user.getVaiTro()))) {
            assignableRoles.add(RoleUtils.normalizeRole(user.getVaiTro()));
        }
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                assignableRoles
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
        spRole.setSelection(assignableRoles.indexOf(RoleUtils.normalizeRole(user.getVaiTro())));
        spRole.setEnabled(false);

        if (user.isAccountDeleted()) {
            edtName.setEnabled(false);
            edtEmail.setEnabled(false);
            edtPhone.setEnabled(false);
            edtAvatar.setEnabled(false);
            swActive.setEnabled(false);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cập nhật người dùng")
                .setView(dialogView)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (user.isAccountDeleted()) {
                dialog.dismiss();
                return;
            }

            String hoTen = getTextValue(edtName);
            String email = getTextValue(edtEmail);
            String soDienThoai = getTextValue(edtPhone);
            String avatar = getTextValue(edtAvatar);
            boolean active = swActive.isChecked();

            if (hoTen.isEmpty()) {
                edtName.setError("Nhập họ tên");
                edtName.requestFocus();
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                edtEmail.requestFocus();
                return;
            }

            boolean currentActive = user.isAccountActive() && !user.isAccountLocked();
            setLoading(true);

            userRepository.updateUserBasicInfo(user.getUid(), hoTen, email, soDienThoai, avatar)
                    .addOnSuccessListener(unused -> {
                        if (currentActive != active) {
                            userRepository.setUserActiveState(user.getUid(), active)
                                    .addOnSuccessListener(unused2 -> {
                                        dialog.dismiss();
                                        Toast.makeText(this, "Đã cập nhật người dùng", Toast.LENGTH_SHORT).show();
                                        loadUsers();
                                    })
                                    .addOnFailureListener(e -> {
                                        setLoading(false);
                                        Toast.makeText(this, "Cập nhật trạng thái thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(this, "Đã cập nhật người dùng", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(this, "Cập nhật thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    });
        }));

        dialog.show();
    }

    private void showRoleDialog(NguoiDung user) {
        if (!canChangeUserRole()) {
            Toast.makeText(this, "Bạn không có quyền cập nhật vai trò", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null || user.isAccountDeleted()) {
            Toast.makeText(this, "Không thể cập nhật vai trò cho tài khoản đã xóa", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCurrentUser(user)) {
            Toast.makeText(this, "Không thể tự đổi vai trò của chính bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> assignableRoles = getAssignableRolesByCurrentAdmin();
        String currentRole = RoleUtils.normalizeRole(user.getVaiTro());

        if (!assignableRoles.contains(currentRole)) {
            assignableRoles.add(currentRole);
        }

        String[] roleArray = assignableRoles.toArray(new String[0]);
        int checkedIndex = Math.max(0, assignableRoles.indexOf(currentRole));
        final int[] selectedIndex = {checkedIndex};

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật vai trò")
                .setSingleChoiceItems(roleArray, checkedIndex, (dialog, which) -> selectedIndex[0] = which)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String selectedRole = roleArray[selectedIndex[0]];
                    if (selectedRole.equals(currentRole)) {
                        return;
                    }

                    setLoading(true);
                    userRepository.updateUserRole(user.getUid(), selectedRole)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đã cập nhật vai trò", Toast.LENGTH_SHORT).show();
                                loadUsers();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this, "Cập nhật role thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .show();
    }

    private void toggleLockUser(NguoiDung user) {
        if (!canLockOrUnlockUser()) {
            Toast.makeText(this, "Bạn không có quyền khóa/mở khóa tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null || user.isAccountDeleted()) {
            Toast.makeText(this, "Không thể khóa tài khoản đã xóa", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCurrentUser(user)) {
            Toast.makeText(this, "Không thể tự khóa tài khoản của chính bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean newLocked = !user.isAccountLocked();
        setLoading(true);

        userRepository.setUserLockState(user.getUid(), newLocked)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, newLocked ? "Đã khóa tài khoản" : "Đã mở khóa tài khoản", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Cập nhật trạng thái khóa thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void confirmDeleteUser(NguoiDung user) {
        if (!canDeleteUser()) {
            Toast.makeText(this, "Bạn không có quyền xóa tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            return;
        }
        if (user.isAccountDeleted()) {
            Toast.makeText(this, "Tài khoản đã được xóa mềm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCurrentUser(user)) {
            Toast.makeText(this, "Không thể xóa tài khoản của chính bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa người dùng")
                .setMessage("Xóa mềm tài khoản này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    setLoading(true);
                    userRepository.softDeleteUser(user.getUid())
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đã xóa mềm tài khoản", Toast.LENGTH_SHORT).show();
                                loadUsers();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this, "Xóa thất bại: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .show();
    }

    private List<String> getAssignableRolesByCurrentAdmin() {
        if (VaiTro.SUPER_ADMIN.equals(RoleUtils.normalizeRole(currentAdminRole))) {
            return new ArrayList<>(RoleUtils.getAssignableRoles());
        }
        if (VaiTro.USER_ADMIN.equals(RoleUtils.normalizeRole(currentAdminRole))) {
            return new ArrayList<>(Arrays.asList(
                    VaiTro.USER,
                    VaiTro.USER_ADMIN,
                    VaiTro.ORDER_ADMIN,
                    VaiTro.PRODUCT_ADMIN
            ));
        }
        return new ArrayList<>(Collections.singletonList(VaiTro.USER));
    }

    private boolean isCurrentUser(NguoiDung user) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return false;
        }
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentUid.equals(user.getUid());
    }

    private void setLoading(boolean loading) {
        progressUsers.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAddUser.setEnabled(!loading && canCreateUser());
        btnAddUser.setAlpha((!loading && canCreateUser()) ? 1f : 0.4f);
    }

    private String getTextValue(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private long safeUpdatedAt(NguoiDung user) {
        return user.getUpdatedAt() == null ? 0L : user.getUpdatedAt();
    }

    private void applyActionPermissions() {
        boolean canEditInfo = canEditUserInfo();
        boolean canChangeRole = canChangeUserRole();
        boolean canLock = canLockOrUnlockUser();
        boolean canDelete = canDeleteUser();

        adminUserAdapter.setPermissions(canEditInfo, canChangeRole, canLock, canDelete);
        btnAddUser.setEnabled(canCreateUser());
        btnAddUser.setAlpha(canCreateUser() ? 1f : 0.4f);
    }

    private String normalizedCurrentRole() {
        return RoleUtils.normalizeRole(currentAdminRole);
    }

    private boolean canCreateUser() {
        return isUserAdminOrSuper();
    }

    private boolean canEditUserInfo() {
        return isUserAdminOrSuper();
    }

    private boolean canChangeUserRole() {
        return isUserAdminOrSuper();
    }

    private boolean canLockOrUnlockUser() {
        return isUserAdminOrSuper();
    }

    private boolean canDeleteUser() {
        return isUserAdminOrSuper();
    }

    private boolean isUserAdminOrSuper() {
        String role = normalizedCurrentRole();
        return VaiTro.USER_ADMIN.equals(role) || VaiTro.SUPER_ADMIN.equals(role);
    }
}