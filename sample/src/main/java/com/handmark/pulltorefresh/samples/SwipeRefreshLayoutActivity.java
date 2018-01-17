package com.handmark.pulltorefresh.samples;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.OnCheckCanDoRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.samples.adapter.CommonAdapter;
import com.handmark.pulltorefresh.samples.adapter.CommonViewHolder;
import com.handmark.pulltorefresh.samples.databinding.ActivitySwipeRefreshLayoutBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwipeRefreshLayoutActivity extends AppCompatActivity {

    private ActivitySwipeRefreshLayoutBinding binding;

    private List<String> dataList;
    private int page = 1;
    private View loadAllView;
    private CommonAdapter<String> adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe_refresh_layout);
        dataList = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        loadAllView = getLayoutInflater().inflate(R.layout.item_load_all, null);
        recyclerView = binding.pullRefreshRecyclerView.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new CommonAdapter<String>(this, dataList) {
            @Override
            public void bindViewHolder(CommonViewHolder holder, String s, int position) {
                holder.setTextViewText(R.id.list_textview, s);
            }

            @Override
            public int getHolderType(int position, String s) {
                return R.layout.activity_ptr_recycler_item;
            }
        };
        recyclerView.setAdapter(adapter);

        binding.switchRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light, android.R.color.holo_blue_dark);
        binding.switchRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.switchRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.switchRefreshLayout.setRefreshing(false);
                        if (adapter != null)
                            adapter.removeFooterView();
                        binding.pullRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                        page = 1;
                        dataList.clear();
                        dataList.addAll(Arrays.asList(FakeData.mDataItems));
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        binding.pullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                binding.pullRefreshRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.pullRefreshRecyclerView.onRefreshComplete();
                        if (page < 3) {
                            dataList.addAll(Arrays.asList(FakeData.mDataItems));
                            page++;
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.pullRefreshRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
                            adapter.addFooterView(loadAllView);
                        }
                    }
                }, 2000);
            }
        });
        dataList.addAll(Arrays.asList(FakeData.mDataItems));
        adapter.notifyDataSetChanged();
    }

}
