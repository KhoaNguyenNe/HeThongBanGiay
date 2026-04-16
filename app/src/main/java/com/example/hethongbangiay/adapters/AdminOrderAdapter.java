package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.DonHang;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminOrderAdapter extends BaseAdapter {
    private Context context;
    private List<DonHang> list;

    public AdminOrderAdapter(Context context, List<DonHang> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtOrderId, txtUser, txtStatus;
        Button btnDone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.admin_order_item, parent, false);

            holder = new ViewHolder();
            holder.txtOrderId = convertView.findViewById(R.id.txtOrderId);
//            holder.txtUser = convertView.findViewById(R.id.txtUser);
            holder.txtStatus = convertView.findViewById(R.id.txtStatus);
            holder.btnDone = convertView.findViewById(R.id.btnDone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DonHang dh = list.get(position);

        holder.txtOrderId.setText("Mã: " + dh.getDonHangId());
//        holder.txtUser.setText("User: " + dh.getNguoiDungId());
        holder.txtStatus.setText("Trạng thái: " + dh.getTinhTrangDonHang());

        holder.btnDone.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("DonHang")
                    .document(dh.getDonHangId())
                    .update("tinhTrangDonHang", "DA_XAC_NHAN")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Đã xác nhận đơn", Toast.LENGTH_SHORT).show();
                        dh.setTinhTrangDonHang("DA_XAC_NHAN");
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        if ("DA_XAC_NHAN".equals(dh.getTinhTrangDonHang())) {

            holder.btnDone.setEnabled(false);
            holder.btnDone.setText("Đã hoàn thành");
            holder.btnDone.setAlpha(0.5f);

        } else {

            holder.btnDone.setEnabled(true);
            holder.btnDone.setText("Xác nhận");
            holder.btnDone.setAlpha(1f);
        }
        return convertView;
    }
}
