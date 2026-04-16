package com.example.hethongbangiay.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.utils.RoleUtils;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEdit(NguoiDung user);
        void onChangeRole(NguoiDung user);
        void onToggleLock(NguoiDung user);
        void onDelete(NguoiDung user);
    }

    private final List<NguoiDung> users = new ArrayList<>();
    private final OnUserActionListener listener;
    private boolean canEditInfo;
    private boolean canChangeRole;
    private boolean canToggleLock;
    private boolean canDelete;

    public AdminUserAdapter(OnUserActionListener listener) {
        this.listener = listener;
        this.canEditInfo = false;
        this.canChangeRole = false;
        this.canToggleLock = false;
        this.canDelete = false;
    }

    public void setPermissions(boolean canEditInfo, boolean canChangeRole, boolean canToggleLock, boolean canDelete) {
        this.canEditInfo = canEditInfo;
        this.canChangeRole = canChangeRole;
        this.canToggleLock = canToggleLock;
        this.canDelete = canDelete;
        notifyDataSetChanged();
    }

    public void submitList(List<NguoiDung> data) {
        users.clear();
        if (data != null) {
            users.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        NguoiDung user = users.get(position);

        ImageResolver.loadAvatar(holder.imgAvatar, user.getAvatar());

        holder.tvName.setText(user.getHoTen() == null || user.getHoTen().trim().isEmpty()
                ? "Chưa cập nhật"
                : user.getHoTen());
        holder.tvEmail.setText(user.getEmail() == null ? "" : user.getEmail());
        holder.tvPhone.setText("SĐT: " + (user.getSoDienThoai() == null || user.getSoDienThoai().trim().isEmpty()
                ? "Chưa cập nhật"
                : user.getSoDienThoai()));
        holder.tvRole.setText("Vai trò: " + RoleUtils.normalizeRole(user.getVaiTro()));
        holder.tvStatus.setText("Trạng thái: " + getStatusText(user));
        holder.tvStatus.setTextColor(getStatusColor(holder.itemView, user));
        holder.tvLastLogin.setText("Đăng nhập gần nhất: " + formatTime(user.getLastLoginAt()));

        boolean deleted = user.isAccountDeleted();
        holder.btnEdit.setVisibility(canEditInfo ? View.VISIBLE : View.GONE);
        holder.btnLock.setVisibility(canToggleLock ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
        holder.btnChangeRole.setVisibility(canChangeRole ? View.VISIBLE : View.GONE);

        holder.btnEdit.setEnabled(canEditInfo && !deleted);
        holder.btnChangeRole.setEnabled(canChangeRole && !deleted);
        holder.btnLock.setEnabled(canToggleLock && !deleted);
        holder.btnDelete.setEnabled(canDelete && !deleted);

        holder.btnLock.setText(user.isAccountLocked() ? "Mở khóa" : "Khóa");

        holder.btnEdit.setOnClickListener(canEditInfo ? v -> {
            if (listener != null) listener.onEdit(user);
        } : null);

        holder.btnChangeRole.setOnClickListener(canChangeRole ? v -> {
            if (listener != null) listener.onChangeRole(user);
        } : null);

        holder.btnLock.setOnClickListener(canToggleLock ? v -> {
            if (listener != null) listener.onToggleLock(user);
        } : null);

        holder.btnDelete.setOnClickListener(canDelete ? v -> {
            if (listener != null) listener.onDelete(user);
        } : null);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private String getStatusText(NguoiDung user) {
        if (user.isAccountDeleted()) {
            return "Đã xóa mềm";
        }
        if (user.isAccountLocked()) {
            return "Đã khóa";
        }
        if (user.isAccountActive()) {
            return "Đang hoạt động";
        }
        return "Tạm ngưng";
    }

    private int getStatusColor(View view, NguoiDung user) {
        if (user.isAccountDeleted()) {
            return Color.parseColor("#A1A1AA");
        }
        if (user.isAccountLocked()) {
            return Color.parseColor("#FCA5A5");
        }
        if (user.isAccountActive()) {
            return Color.parseColor("#86EFAC");
        }
        return Color.parseColor("#FCD34D");
    }

    private String formatTime(Long timestamp) {
        if (timestamp == null || timestamp <= 0L) {
            return "--";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return formatter.format(new Date(timestamp));
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName;
        TextView tvEmail;
        TextView tvPhone;
        TextView tvRole;
        TextView tvStatus;
        TextView tvLastLogin;
        AppCompatButton btnEdit;
        AppCompatButton btnChangeRole;
        AppCompatButton btnLock;
        AppCompatButton btnDelete;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
            tvLastLogin = itemView.findViewById(R.id.tvLastLogin);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnChangeRole = itemView.findViewById(R.id.btnChangeRole);
            btnLock = itemView.findViewById(R.id.btnLockUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
