package com.example.chenzhe.eyerhyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.adapter.TheaterCommentAdapter;
import com.example.chenzhe.eyerhyme.customInterface.viewController;
import com.example.chenzhe.eyerhyme.model.TheaterCommentItem;
import com.example.chenzhe.eyerhyme.model.TheaterCommentResponse;
import com.example.chenzhe.eyerhyme.model.TheaterDetail;
import com.example.chenzhe.eyerhyme.model.TheaterDetailResponse;
import com.example.chenzhe.eyerhyme.model.TheaterItem;
import com.example.chenzhe.eyerhyme.util.PostUtil;
import com.example.chenzhe.eyerhyme.util.ToastUtil;
import com.example.chenzhe.eyerhyme.view.MyListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TheaterDetailActivity extends Activity implements viewController {
    private final String theater_detail_url = "/theater/get_theaer_detail";
    private final String get_theater_comment_url = "/theater/get_grades";
    private final String give_theater_comment_url = "/user/give_grades";
    private String date_time = "";          // 最后一条评论的时间
    private static int theater_id;          // 影院 id
    private static int user_id;             // 用户 id
    private static boolean change = false;  // 是否点赞
    private PostUtil postUtil;
    private TheaterItem item;               // 从上一个页面传递的反序列化对象
    private ImageButton back;

    private TextView name;
    private RelativeLayout location;        // 点击跳转到导航页面
    private RelativeLayout phone_call;      // 点击跳转到拨号页面
    private ImageView[] stars = new ImageView[5];
    private TextView score;                 // 影院的评分
    private TextView assessNum;             // 对影院评分的人数
    private TextView tvLoc;
    private TextView tvContact;
    private TextView tvChar;
    private RelativeLayout addComment;      // 对影院评分
    private static final int REQUEST_CODE = 1;

    private MyListView lv;                                // ListView
    private List<TheaterCommentItem> theaterCommentList;  // 评论链表
    private TheaterCommentAdapter commentAdapter;         // 评论适配器

    public final static String SER_KEY = "com.jay.key";
    private TheaterCommentItem theaterCommentItem;  // 用户对影院写的新评论（即点击“写评论”页面返回的对象）

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theater_detail);
        bindView();
        setView();
        postUtil = PostUtil.newInstance();
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        item = (TheaterItem)getIntent().getSerializableExtra("theater");

        user_id = sharedPreferences.getInt("user_id",-1);
        theater_id = item.getTheater_id();

        // 从后台获取影院的详细信息
        getTheaterInformation();

        // 从上一个页面获取的一个序列化TheaterItem对象


        lv = (MyListView)findViewById(R.id.theater_comment_list);
//        theaterCommentList = new ArrayList<TheaterCommentItem>();


//        commentAdapter = new TheaterCommentAdapter(TheaterDetailActivity.this, R.layout.theater_comment_item, theaterCommentList);
//        lv.setAdapter(commentAdapter);

        getTheaterComment();





    }
    private void bindView() {
        back = (ImageButton)findViewById(R.id.back_to_theater_list);

        name = (TextView)findViewById(R.id.theater_name_in_detail_page);
        location = (RelativeLayout)findViewById(R.id.address_in_detail_page);
        phone_call = (RelativeLayout)findViewById(R.id.phone_call_in_detail_page);
        stars[0] = (ImageView)findViewById(R.id.star_1);
        stars[1] = (ImageView)findViewById(R.id.star_2);
        stars[2] = (ImageView)findViewById(R.id.star_3);
        stars[3] = (ImageView)findViewById(R.id.star_4);
        stars[4] = (ImageView)findViewById(R.id.star_5);
        score = (TextView)findViewById(R.id.theater_score_in_detail_page);
        assessNum = (TextView)findViewById(R.id.assess_number);
        addComment = (RelativeLayout)findViewById(R.id.comment_button);

        tvLoc = (TextView)findViewById(R.id.theater_address_in_detail_page);
        tvContact = (TextView)findViewById(R.id.phone_number_in_detail_page);
        tvChar = (TextView)findViewById(R.id.character1_description);
    }
    private void setView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



//        name.setText(item.getName());
//
//        int grade = ((int)item.getGrade() + 1) / 2;
//        for (int i = 0; i < grade; i++)
//            stars[i].setImageResource(R.mipmap.star);
//
//        double f = (double)item.getGrade();
//        BigDecimal b = new BigDecimal(f);
//        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        score.setText(Double.toString(f1));
//
//        assessNum.setText("44人评价");

        // 跳转到导航页面
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        //跳转到拨号页面
        phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number;
                TextView pn = (TextView) findViewById(R.id.phone_number_in_detail_page);
                phone_number = pn.getText().toString();
                Uri uri=Uri.parse("tel:"+phone_number);
                Intent intent =  new Intent(Intent.ACTION_DIAL , uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(TheaterDetailActivity.SER_KEY, item);
                intent.setClass(TheaterDetailActivity.this, CommentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    // 处理影院评论：发往后台
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                theaterCommentItem = (TheaterCommentItem)data
                        .getSerializableExtra(CommentActivity.SER_KEY);
                Log.i("activity result", theaterCommentItem.getContent());

                // Send this comment to server
                sendComment(theaterCommentItem);

            }
        }
    }


    private void getTheaterInformation() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("theater_id", theater_id);
        postUtil.sendPost(this, theater_detail_url, map);
    }

    private void getTheaterComment() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("theater_id", theater_id);
//        map.put("date_time", date_time);
        postUtil.sendPost(this, get_theater_comment_url, map);
    }

    private void sendComment(TheaterCommentItem commentItem) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        map.put("type", 0);
        map.put("object_id", theater_id);
        map.put("value", (int)commentItem.getGrade());
        map.put("content", commentItem.getContent());
        postUtil.sendPost(this, give_theater_comment_url, map);
    }

    @Override
    public void updateView(String url, String response) {
        if (response == null) {
            Toast.makeText(TheaterDetailActivity.this, "network fail", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (url.equals(theater_detail_url)) {
                TheaterDetailResponse detail = new Gson().fromJson(response, TheaterDetailResponse.class);
                if (detail.status) {
                    final TheaterDetail result = detail.result;
                    name.setText(result.getName());

                    int grade = ((int)result.getGrade() + 1) / 2;
                    for (int i = 0; i < grade; i++)
                        stars[i].setImageResource(R.mipmap.star);

                    DecimalFormat df = new DecimalFormat("######0.0");
                    score.setText(df.format(result.getGrade())+"");

                    assessNum.setText((int)result.getGrade_num()+"人评价");

                    tvLoc.setText(result.getLocation());
                    tvContact.setText(result.getContact());
                    tvChar.setText(result.getDescription());
                    location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(TheaterDetailActivity.this, RoutingActivity.class);
                            it.putExtra("longitude", result.getLongitude());
                            it.putExtra("latitude", result.getLatitude());
                            startActivity(it);
                        }
                    });
                } else {
                    ToastUtil.printToast(this, "获取影院详情失败");
                    return;
                }
            } else if (url.equals(get_theater_comment_url)) {
                TheaterCommentResponse detail = new Gson().fromJson(response, TheaterCommentResponse.class);
                if (detail.status) {
                    theaterCommentList = detail.result;
                    commentAdapter = new TheaterCommentAdapter(TheaterDetailActivity.this, R.layout.theater_comment_item, theaterCommentList);
                    lv.setAdapter(commentAdapter);
                } else {
                    ToastUtil.printToast(this, "获取评论失败");
                    return;
                }
            } else if (url.equals(give_theater_comment_url)) {
                Log.i("give comment response", response);
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {

                    theaterCommentList.add(0, theaterCommentItem);
                    commentAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.printToast(this, "评论失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
