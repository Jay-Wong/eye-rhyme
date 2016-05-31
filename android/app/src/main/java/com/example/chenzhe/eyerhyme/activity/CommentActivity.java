package com.example.chenzhe.eyerhyme.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.model.TheaterCommentItem;
import com.example.chenzhe.eyerhyme.model.TheaterItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentActivity extends AppCompatActivity {
    @Bind(R.id.tb_title)
    TextView tbTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private TextView theater_name;
    private TextView grade;
    private EditText content;
    private ImageView[] star = new ImageView[5];
    private Button submit;
    private TextView text_size_tips;
    private TheaterItem theaterItem;
    private int clickStar = 0;
    private Boolean flag = false;  // 值为true才可以提交评论
    public final static String SER_KEY = "com.comment.key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        theaterItem = (TheaterItem) getIntent().getSerializableExtra(TheaterDetailActivity.SER_KEY);
        bindView();
        setView();
        initToolbar();
    }

    private void initToolbar() {
        tbTitle.setText("添加评论");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindView() {
        theater_name = (TextView) findViewById(R.id.theater_name_in_theater_comment_page);

        grade = (TextView) findViewById(R.id.theater_score_in_theater_comment_page);
        content = (EditText) findViewById(R.id.content_theater_comment_page);
        text_size_tips = (TextView) findViewById(R.id.text_size_tips);
        star[0] = (ImageView) findViewById(R.id.comment_star_1);
        star[1] = (ImageView) findViewById(R.id.comment_star_2);
        star[2] = (ImageView) findViewById(R.id.comment_star_3);
        star[3] = (ImageView) findViewById(R.id.comment_star_4);
        star[4] = (ImageView) findViewById(R.id.comment_star_5);
        submit = (Button) findViewById(R.id.submit_theater_comment);
    }

    private void setView() {
        theater_name.setText(theaterItem.getName());


        // 保留小数点后1位
        double score = (double) theaterItem.getGrade();
        BigDecimal temp = new BigDecimal(score);
        double obj_value = temp.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        grade.setText(Double.toString(obj_value));

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int num = s.length();
                if (num >= 15 && clickStar > 0) {
                    submit.setBackgroundColor(Color.rgb(30, 144, 255));
                    flag = true;
                } else {
                    submit.setBackgroundColor(Color.rgb(112, 128, 144));
                    flag = false;
                }


                if (num < 15)
                    text_size_tips.setText("加油！至少还差" + (15 - num) + "个字哦~");
                else
                    text_size_tips.setText("评论至多100个字 " + num + "/100");
            }
        });

        star[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star[0].setImageResource(R.mipmap.comment_star_orange);
                for (int i = 1; i < 5; i++)
                    star[i].setImageResource(R.mipmap.comment_star_gray);
                clickStar = 1;
            }
        });
        star[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 2; i++)
                    star[i].setImageResource(R.mipmap.comment_star_orange);
                for (int i = 2; i < 5; i++)
                    star[i].setImageResource(R.mipmap.comment_star_gray);
                clickStar = 2;
            }
        });
        star[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++)
                    star[i].setImageResource(R.mipmap.comment_star_orange);
                for (int i = 3; i < 5; i++)
                    star[i].setImageResource(R.mipmap.comment_star_gray);
                clickStar = 3;
            }
        });
        star[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 4; i++)
                    star[i].setImageResource(R.mipmap.comment_star_orange);
                star[4].setImageResource(R.mipmap.comment_star_gray);
                clickStar = 4;
            }
        });
        star[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 5; i++)
                    star[i].setImageResource(R.mipmap.comment_star_orange);
                clickStar = 5;
            }
        });

        // 提交评论
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    // 从sharePreference获取昵称？
                    String t_name = "user_name";
                    int t_grade = clickStar * 2;
                    String t_content = content.getText().toString();
                    Calendar calendar = Calendar.getInstance();

                    // 日期格式不清楚，需要改
                    String t_date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) +
                            "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) +
                            ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
                    TheaterCommentItem item = new TheaterCommentItem();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                    item.setGrade(t_grade);
                    item.setContent(t_content);
                    item.setName(t_name);
                    item.setDate_time(formatter.format(new Date(System.currentTimeMillis())));

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SER_KEY, item);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
