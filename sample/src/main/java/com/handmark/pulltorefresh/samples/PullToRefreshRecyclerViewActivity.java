/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.samples.adapter.CommonAdapter;
import com.handmark.pulltorefresh.samples.adapter.CommonViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PullToRefreshRecyclerViewActivity extends AppCompatActivity {

    private String[] mDataItems = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};

    private List<String> dataList;
    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private int page = 1;
    private View loadAllView;
    private CommonAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_recycler);
        dataList = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        loadAllView = getLayoutInflater().inflate(R.layout.item_load_all, null);
        mPullRefreshRecyclerView = findViewById(R.id.pull_refresh_recycler);
        mRecyclerView = mPullRefreshRecyclerView.getRefreshableView();
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
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
        mRecyclerView.setAdapter(adapter);

        mPullRefreshRecyclerView.setOnRefreshListener(new OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mPullRefreshRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshRecyclerView.onRefreshComplete();
                        if (adapter != null)
                            adapter.removeFooterView();
                        mPullRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                        page = 1;
                        dataList.clear();
                        dataList.addAll(Arrays.asList(mDataItems));
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mPullRefreshRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshRecyclerView.onRefreshComplete();
                        if (page < 3) {
                            dataList.addAll(Arrays.asList(mDataItems));
                            page++;
                            adapter.notifyDataSetChanged();
                        } else {
                            mPullRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            adapter.addFooterView(loadAllView);
                        }
                    }
                }, 2000);
            }
        });
        mPullRefreshRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullRefreshRecyclerView.setRefreshing();
            }
        }, 300);
    }

}
