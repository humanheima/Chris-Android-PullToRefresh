package com.handmark.pulltorefresh.samples.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.samples.R;
import com.handmark.pulltorefresh.samples.interfaces.ItemTypeCallBack;

import java.util.List;

/**
 * Created by dumingwei on 2017/4/19.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> implements ItemTypeCallBack<T> {

    protected Context context;
    private List<T> data;
    private View footerView;

    public CommonAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemCount() {
        if (footerView == null) {
            return data.size();
        } else {
            return data.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= data.size()) {
            return R.layout.item_load_all;
        }
        return getHolderType(position, data.get(position));
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CommonViewHolder.get(context, parent, viewType);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        if (position < data.size()) {
            bindViewHolder(holder, data.get(position), position);
        }
    }

    public abstract void bindViewHolder(CommonViewHolder holder, T t, int position);

    /**
     * 添加底部
     *
     * @param view
     */
    public void addFooterView(View view) {
        if (view == null) {
            throw new NullPointerException("FooterView is null!");
        }
        if (footerView != null) {
            return;
        }
        footerView = view;
        footerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        notifyItemInserted(getItemCount());
    }

    /**
     * 移除底部View
     */
    public void removeFooterView() {
        if (footerView != null) {
            footerView = null;
            notifyItemRemoved(getItemCount());
        }
    }

    /**
     * 设置网格布局footView占据一整行
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == R.layout.item_load_all ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 设置瀑布流布局footView占据一整行
     */
    @Override
    public void onViewAttachedToWindow(CommonViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int lastItemPosition = holder.getAdapterPosition();
        if (getItemViewType(lastItemPosition) == R.layout.item_load_all) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = ((StaggeredGridLayoutManager.LayoutParams) lp);
                p.setFullSpan(true);
            }
        }
    }
}
