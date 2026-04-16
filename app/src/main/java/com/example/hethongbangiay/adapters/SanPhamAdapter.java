package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.utils.FormatUtils;
import com.example.hethongbangiay.utils.ImageResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.MainViewHolder> {

    public interface onSanPhamClickListener {
        void onSanPhamClick(SanPham sp);
    }

    private final Context context;
    private final List<SanPham> danhSachSp;
    private final onSanPhamClickListener listener;
    private final Set<String> danhSachYeuThich = new HashSet<>();


    public SanPhamAdapter(Context context, List<SanPham> danhSachSp, onSanPhamClickListener listener) {
        this.context = context;
        this.danhSachSp = danhSachSp;
        this.listener = listener;
    }

    public void capNhatDuLieu(List<SanPham> dsMoi) {
        danhSachSp.clear();
        if(dsMoi != null) {
            this.danhSachSp.addAll(dsMoi);
        }
        notifyDataSetChanged();
    }

    public void capNhatYeuThich(Set<String> dsYeuThich) {
        danhSachYeuThich.clear();
        if (dsYeuThich != null) {
            danhSachYeuThich.addAll(dsYeuThich);
        }
        notifyDataSetChanged();
    }

    private void bindProductImage(ImageView imgView, String imgReference) {
        ImageResolver.loadImageReference(imgView, imgReference);
    }

    private void bindFavoriteIcon(ImageView imageView, boolean isFavorite) {
        imageView.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline_dark);
        imageView.setColorFilter(ContextCompat.getColor(
                context,
                isFavorite ? R.color.app_primary : R.color.app_text_primary
        ));
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        SanPham sp = danhSachSp.get(position);

        holder.tvProductName.setText(sp.getTenSanPham());

        holder.tvPrice.setText(FormatUtils.formatCurrency(sp.getDonGia()));

        holder.tvRating.setText(sp.getDiemDanhGia() + "");
        holder.tvSold.setText(sp.getLuotBan() + "");
        bindProductImage(holder.imgProduct, sp.getAnhSanPham());
        bindFavoriteIcon(holder.ivFavorite, danhSachYeuThich.contains(sp.getSanPhamId()));

         holder.itemView.setOnClickListener(view -> {
             if(listener != null) {
                 listener.onSanPhamClick(sp);
             }
         });
    }

    @Override
    public int getItemCount() {
        return danhSachSp.size();
    }



    static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        ImageView ivFavorite;
        TextView tvProductName;
        TextView tvRating;
        TextView tvSold;
        TextView tvPrice;

        public MainViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvProductName = itemView.findViewById(R.id.txtName);
            tvRating = itemView.findViewById(R.id.txtRating);
            tvSold = itemView.findViewById(R.id.txtSold);
            tvPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
