package com.example.hethongbangiay.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * GridLayoutManager đo chiều cao đầy đủ để dùng an toàn trong ScrollView/NestedScrollView.
 */
public class WrapContentGridLayoutManager extends GridLayoutManager {

    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public WrapContentGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public WrapContentGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        if (heightMode == View.MeasureSpec.EXACTLY) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }

        int expandedHeightSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(recycler, state, widthSpec, expandedHeightSpec);
    }
}

