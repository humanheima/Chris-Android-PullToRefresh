package com.handmark.pulltorefresh.samples;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.OnCheckCanDoRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshFrameLayout;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.samples.adapter.CommonAdapter;
import com.handmark.pulltorefresh.samples.adapter.CommonViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PullToRefreshFrameLayoutActivity extends Activity {

    private PullToRefreshFrameLayout pullToRefreshFrameLayout;
    private PullToRefreshRecyclerView pullToRefreshRecyclerView;
    private RecyclerView recyclerView;

    private List<String> dataList;
    private int page = 1;
    private View loadAllView;
    private CommonAdapter<String> adapter;

    //recyclerView竖直方向上滑动距离
    private int offsetY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_frame_layout);
        dataList = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        loadAllView = getLayoutInflater().inflate(R.layout.item_load_all, null);
        pullToRefreshFrameLayout = findViewById(R.id.pull_to_refresh_frame_layout);
        pullToRefreshRecyclerView = findViewById(R.id.pull_refresh_recycler_view);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                offsetY += dy;
            }
        });

        pullToRefreshFrameLayout.setOnCheckCanDoRefreshListener(new OnCheckCanDoRefreshListener() {
            @Override
            public boolean checkCanPullStart() {
                return offsetY == 0;
            }
        });

        pullToRefreshFrameLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<FrameLayout>() {
            @Override
            public void onRefresh(PullToRefreshBase<FrameLayout> refreshView) {
                pullToRefreshFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshFrameLayout.onRefreshComplete();
                        if (adapter != null)
                            adapter.removeFooterView();
                        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                        page = 1;
                        dataList.clear();
                        dataList.addAll(Arrays.asList(FakeData.mDataItems));
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullToRefreshRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshRecyclerView.onRefreshComplete();
                        if (page < 3) {
                            dataList.addAll(Arrays.asList(FakeData.mDataItems));
                            page++;
                            adapter.notifyDataSetChanged();
                        } else {
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
                            adapter.addFooterView(loadAllView);
                        }
                    }
                }, 2000);
            }
        });

        pullToRefreshFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshFrameLayout.setRefreshing();
            }
        }, 300);
    }

}
