package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.BannerItem;
import com.google.android.gms.common.internal.ResourceUtils;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.ViewPager2_Items> {
    private final List<BannerItem> items;

    public OnBoardingAdapter(List<BannerItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewPager2_Items onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater view = LayoutInflater.from(parent.getContext());
        View viewItem = view.inflate(R.layout.item_onboarding, parent, false);

        return new ViewPager2_Items(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2_Items holder, int position) {
        BannerItem item = items.get(position);
        holder.tvSubtitle.setText(item.subtitle);
        holder.tvTitle.setText(item.title);
        holder.layoutHero.setBackgroundResource(
                holder.itemView.getContext().getResources().getIdentifier(item.backgroundName, "drawable",
                holder.itemView.getContext().getPackageName()
                )
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewPager2_Items extends RecyclerView.ViewHolder {
        private final FrameLayout layoutHero;
        private final TextView tvTitle;
        private final TextView tvSubtitle;

        ViewPager2_Items(View itemView) {
            super(itemView);
            this.layoutHero = itemView.findViewById(R.id.layoutHero);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}
