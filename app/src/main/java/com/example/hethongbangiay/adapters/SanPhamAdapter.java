package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.utils.ImageResolver;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.MainViewHolder> {

    private final Context context;
    private final List<SanPham> danhSachSp;

    public SanPhamAdapter(Context context, List<SanPham> danhSachSp) {
        this.context = context;
        this.danhSachSp = danhSachSp;
    }

    private void bindProductImage(ImageView imgView, String imgReference) {
        String imgUrl = ImageResolver.resolveImage(imgReference);
        int fallBack = ImageResolver.resolveFallbackDrawable(context, imgReference);

        if(imgUrl == null) {
            imgView.setImageResource(fallBack);
            return;
        }

        Glide.with(context).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(fallBack).error(fallBack).into(imgView);
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

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(format.format(sp.getDonGia()) + " đ");

        holder.tvRating.setText("0.0");
        holder.tvSold.setText("0 sold");
        bindProductImage(holder.imgProduct, sp.getAnhSanPham());
    }

    @Override
    public int getItemCount() {
        return danhSachSp.size();
    }



    static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvRating;
        TextView tvSold;
        TextView tvPrice;

        public MainViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvSold = itemView.findViewById(R.id.tvSold);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
