package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.SizeGiay;

import java.util.List;

public class SizeAdapter extends BaseAdapter {

    private Context context;
    private List<SizeGiay> list;

    public SizeAdapter(Context context, List<SizeGiay> list) {
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
        TextView txtSize;
        EditText edtQty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_size, parent, false);

            holder = new ViewHolder();
            holder.txtSize = convertView.findViewById(R.id.txtSize);
            holder.edtQty = convertView.findViewById(R.id.edtQty);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SizeGiay size = list.get(position);

        holder.txtSize.setText("Size " + size.getSize());
        holder.edtQty.setText(String.valueOf(size.getSoLuong()));

        // tránh lỗi nhập lại bị loop
        holder.edtQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int qty = Integer.parseInt(s.toString());
                    size.setSoLuong(qty);
                } catch (Exception e) {
                    size.setSoLuong(0);
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
        });

        return convertView;
    }
}