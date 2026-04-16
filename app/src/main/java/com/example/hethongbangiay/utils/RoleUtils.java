package com.example.hethongbangiay.utils;

import com.example.hethongbangiay.models.VaiTro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RoleUtils {

    private RoleUtils() {
    }

    public static String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return VaiTro.USER;
        }

        String normalized = role.trim().toUpperCase();
        if (Constants.VAI_TRO_ADMIN.equals(normalized)) {
            return VaiTro.SUPER_ADMIN;
        }

        if ("USERADMIN".equals(normalized) || "ADMIN_USER".equals(normalized) || "USER-ADMIN".equals(normalized)) {
            return VaiTro.USER_ADMIN;
        }

        if ("ORDERADMIN".equals(normalized) || "ADMIN_ORDER".equals(normalized) || "ORDER-ADMIN".equals(normalized)) {
            return VaiTro.ORDER_ADMIN;
        }

        if ("PRODUCTADMIN".equals(normalized) || "ADMIN_PRODUCT".equals(normalized) || "PRODUCT-ADMIN".equals(normalized)) {
            return VaiTro.PRODUCT_ADMIN;
        }

        if ("SUPERADMIN".equals(normalized) || "SUPEERADMIN".equals(normalized) || "SUPER-ADMIN".equals(normalized)) {
            return VaiTro.SUPER_ADMIN;
        }

        switch (normalized) {
            case VaiTro.USER:
            case VaiTro.USER_ADMIN:
            case VaiTro.ORDER_ADMIN:
            case VaiTro.PRODUCT_ADMIN:
            case VaiTro.SUPER_ADMIN:
                return normalized;
            default:
                return VaiTro.USER;
        }
    }

    public static boolean isAdminRole(String role) {
        String normalized = normalizeRole(role);
        return VaiTro.USER_ADMIN.equals(normalized)
                || VaiTro.ORDER_ADMIN.equals(normalized)
                || VaiTro.PRODUCT_ADMIN.equals(normalized)
                || VaiTro.SUPER_ADMIN.equals(normalized);
    }

    public static boolean canManageUsers(String role) {
        String normalized = normalizeRole(role);
        return VaiTro.USER_ADMIN.equals(normalized) || VaiTro.SUPER_ADMIN.equals(normalized);
    }

    public static boolean canManageOrders(String role) {
        String normalized = normalizeRole(role);
        return VaiTro.ORDER_ADMIN.equals(normalized) || VaiTro.SUPER_ADMIN.equals(normalized);
    }

    public static boolean canManageProducts(String role) {
        String normalized = normalizeRole(role);
        return VaiTro.PRODUCT_ADMIN.equals(normalized) || VaiTro.SUPER_ADMIN.equals(normalized);
    }

    public static boolean canViewReports(String role) {
        return isAdminRole(role);
    }

    public static boolean canAccessAdminDashboard(String role) {
        return isAdminRole(role);
    }

    public static List<String> getAssignableRoles() {
        return new ArrayList<>(Arrays.asList(
                VaiTro.USER,
                VaiTro.USER_ADMIN,
                VaiTro.ORDER_ADMIN,
                VaiTro.PRODUCT_ADMIN,
                VaiTro.SUPER_ADMIN
        ));
    }
}
