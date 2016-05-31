package com.example.chenzhe.eyerhyme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.adapter.TheatersNearbyAdapter;
import com.example.chenzhe.eyerhyme.customInterface.viewController;
import com.example.chenzhe.eyerhyme.model.TheaterItem;
import com.example.chenzhe.eyerhyme.model.TheatersNearbyResponse;
import com.example.chenzhe.eyerhyme.util.PostUtil;
import com.example.chenzhe.eyerhyme.util.ToastUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class TheaterListActivity extends AppCompatActivity implements viewController, BGARefreshLayout.BGARefreshLayoutDelegate{
    // 序列化的Key
    public final static String SER_KEY = "com.andy.ser";
    @Bind(R.id.tb_title)
    TextView tbTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.refreshlayout)
    BGARefreshLayout refreshlayout;

    private ArrayList<TheaterItem> theaterList;
    private TheatersNearbyAdapter adapter;


    private static String getTheaterUrl = "/theater/get_theaters_nearby";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_list);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        initToolbar();
        initRefreshLayout();
        initListView();

        theaterList = null;
        getTheaters(-1);


    }

    private void initListView() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(TheaterListActivity.this, ChoosingMovieAndDateActivity.class);
                it.putExtra("theater", theaterList.get(position));
                startActivity(it);
            }
        });
    }

    private void initRefreshLayout() {
        refreshlayout.setDelegate(this);
        BGARefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(this, true);
        refreshlayout.setRefreshViewHolder(viewHolder);
    }

    private void initToolbar() {

        toolbar.setTitle("");
        tbTitle.setText("影院列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTheaters(int theater_id) {
        HashMap<String ,Object> map = new HashMap<>();
        map.put("longitude", MainActivity.longitude);
        map.put("latitude", MainActivity.latitude);
        if (theater_id != -1)
            map.put("theater_id", theater_id);

        PostUtil.newInstance().sendPost(this, getTheaterUrl, map);
    }

    @Override
    public void updateView(String url, String response) {
        if (response == null) {
            Toast.makeText(TheaterListActivity.this, "network fail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (url.equals(getTheaterUrl)) {
            refreshlayout.endLoadingMore();
            refreshlayout.endRefreshing();
            TheatersNearbyResponse response1 = new Gson().fromJson(response, TheatersNearbyResponse.class);
            if (response1.status) {
                if (theaterList == null) {
                    theaterList = response1.theaters;
                    adapter = new TheatersNearbyAdapter(theaterList, this);
                    listview.setAdapter(adapter);
                } else {
                    theaterList.addAll(response1.theaters);
                    adapter.notifyDataSetChanged();
                }
            } else {
                ToastUtil.printToast(this, "获取影院失败");
            }
        }
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        theaterList = null;
        getTheaters(-1);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (theaterList == null)
            getTheaters(-1);
        else
            getTheaters(theaterList.get(theaterList.size()-1).getTheater_id());
        return true;
    }


}
