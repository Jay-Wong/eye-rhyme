package example.jay.com.eyeryhme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TheaterListActivity extends Activity implements viewController {
    // 序列化的Key
    public final static String SER_KEY = "com.andy.ser";

    private ListView lv;
    private List<TheaterItem> theaterList;
    private TheaterItemAdapter adapter;

    public static double longitude = 23.067964;
    public static double latitude = 113.401478;

    private static int LAST_THEATER_ID = 0;

    private PostUtil postUtil;
    private static String getTheaterUrl = "/theater/get_theaters_nearby";

    private ImageButton back;

    private String[] name = {"金逸国际影城（大学城店）", "东圃摩登电影城（东圃大马路店）",
            "哈艺时尚影城", "广州金逸影城番禺大石店",
            "横店电影城（广州长兴店）", "金逸国际影城（大学城店）", "东圃摩登电影城（东圃大马路店）",
            "哈艺时尚影城", "广州金逸影城番禺大石店",
            "横店电影城（广州长兴店）"};
    private String[] address = {"广州大学城gogo新天地二期二楼", "天河区东圃大马路14号东圃摩登城3楼",
            "海珠区石榴岗路10号（生物工程大厦1楼）", "广州市番禺区大石街建华汇商业中心东街17号楼F座3楼",
            "长兴路13号楼高德汇3楼", "广州大学城gogo新天地二期二楼", "天河区东圃大马路14号东圃摩登城3楼",
            "海珠区石榴岗路10号（生物工程大厦1楼）", "广州市番禺区大石街建华汇商业中心东街17号楼F座3楼",
            "长兴路13号楼高德汇3楼"};
    private int[] lowestPrice = {28, 34, 34, 40, 34, 28, 34, 34, 40, 34};
    private float[] score = {8.0f, 7.5f, 7.0f, 7.6f, 7.2f, 8.0f, 7.5f, 7.0f, 7.6f, 7.2f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_list);
        init();
        handleEvent();
    }

    private void init() {
        postUtil = PostUtil.newInstance();
        lv = (ListView)findViewById(R.id.theater_list);
        back = (ImageButton)findViewById(R.id.back_to_main);
        theaterList = new ArrayList<TheaterItem>();

        initTheaterList();

        adapter = new TheaterItemAdapter(this, R.layout.theater_item, theaterList);
        lv.setAdapter(adapter);
    }

    private void handleEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(SER_KEY, theaterList.get(position));
                intent.setClass(TheaterListActivity.this, TheaterDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initTheaterList() {
        /*
        // 加载10个影院
        getTheaterList();
        getTheaterList();
        */

        // 测试数据：加载10个影院
        for (int i = 0; i < 10; i++)
            theaterList.add(new TheaterItem(0, name[i], address[i], lowestPrice[i], score[i], 23.048, 128.21));

        // Set footer
        TextView footer = new TextView(this);
        footer.setText("加载更多...");
        LinearLayout footerParent = new LinearLayout(this);
        footerParent.setGravity(Gravity.CENTER);
        footerParent.setMinimumHeight(160);
        footerParent.addView(footer);
        footerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTheaterList();
                adapter.notifyDataSetChanged();
            }
        });
        lv.addFooterView(footerParent);
    }

    private void getTheaterList() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("theater_id", LAST_THEATER_ID);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        postUtil.sendPost(this, getTheaterUrl, map);
    }

    @Override
    public void updateView(String url, String response) {
        if (response == null) {
            Toast.makeText(TheaterListActivity.this, "network fail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (url.equals(getTheaterUrl)) {
            try {
                Log.i("response", response);
                JSONObject json = new JSONObject(response);
                if (json.getBoolean("status")) {
                    JSONArray arr = json.getJSONArray("theaters");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = (JSONObject) arr.get(i);
                        int t_id = item.getInt("theater_id");
                        String t_name = item.getString("name");
                        String t_location = item.getString("location");
                        int t_lowestPrice = item.getInt("lowest_price");
                        float t_grade = (float)item.getDouble("grade");
                        double t_longitude = item.getDouble("longitude");
                        double t_latitude = item.getDouble("latitude");

                        theaterList.add(theaterList.size(), new TheaterItem(t_id, t_name, t_location,
                                t_lowestPrice, t_grade, t_longitude, t_latitude));
                    }
                    if (arr.length() > 0) {
                        JSONObject last = (JSONObject) arr.get(arr.length()-1);
                        LAST_THEATER_ID = last.getInt("theater_id");
                    } else {
                        Toast.makeText(TheaterListActivity.this, "已加载完毕", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TheaterListActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Context myContext() {
        return this;
    }

    public static int getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return (int)d;
    }
}
