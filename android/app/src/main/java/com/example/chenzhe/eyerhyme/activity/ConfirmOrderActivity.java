package com.example.chenzhe.eyerhyme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.customInterface.viewController;
import com.example.chenzhe.eyerhyme.model.MovieProductItem;
import com.example.chenzhe.eyerhyme.model.makeOrderResponse;
import com.example.chenzhe.eyerhyme.util.PostUtil;
import com.example.chenzhe.eyerhyme.util.ToastUtil;
import com.example.greendao.dao.Bill;
import com.example.greendao.dao.BillDao;
import com.example.greendao.dao.DaoMaster;
import com.example.greendao.dao.DaoSession;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfirmOrderActivity extends AppCompatActivity implements viewController {
    @Bind(R.id.tb_title)
    TextView tbTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ImageButton back;
    private ImageView editPhone;
    private static TextView timer;
    private TextView movieName;
    private TextView theaterName;
    private TableLayout seatsSelected;
    private TextView theaterHall;
    private TextView movieTime;

    private EditText phone;
    private TextView ticketsPrice;
    private TextView discount;
    private TextView servicePrice;
    private TextView orderPrice;
    private Button backToSelect;
    private Button confirm;
    private int seconds = 420;
    private Handler handler;
    private MovieProductItem item;
    private int product_id;
    private String makeOrderURL = "/movie_ticket/make_order";
    private SharedPreferences sharedPreferences;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.bind(this);
        item = (MovieProductItem) getIntent().getSerializableExtra(SeatActivity.SER_KEY);
        product_id = getIntent().getIntExtra("product_id",-1);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        bindView();
        initToolbar();
        setupDatabase();
        setView();
//        handler = new Handler();
//        handler.postDelayed(runnable, 1000);
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private BillDao getBillDao() {
        return daoSession.getBillDao();
    }
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            seconds--;
//            if (seconds > 0) {
//                int min = seconds/60;
//                int sec = seconds % 60;
//                String min_str = "0" + min;
//                String sec_str;
//                if (sec < 10)
//                    sec_str = "0" + sec;
//                else
//                    sec_str = "" + sec;
//                timer.setText(min_str+":"+sec_str);
//            } else {
//                finish();
//            }
//            handler.postDelayed(this, 1000);
//        }
//    };


    private void initToolbar() {
        tbTitle.setText("确认订单");
        toolbar.setTitle("");
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

    private void bindView() {

        editPhone = (ImageView) findViewById(R.id.edit_phone);

        movieName = (TextView) findViewById(R.id.confirm_order_page_movie_name);
        theaterName = (TextView) findViewById(R.id.confirm_order_page_theater_name);
        seatsSelected = (TableLayout) findViewById(R.id.confirm_order_page_seats);
        theaterHall = (TextView) findViewById(R.id.confirm_order_page_theater_hall);
        movieTime = (TextView) findViewById(R.id.confirm_order_page_movie_time);
        phone = (EditText) findViewById(R.id.confirm_order_page_phone);
        ticketsPrice = (TextView) findViewById(R.id.confirm_order_page_total_price);
        discount = (TextView) findViewById(R.id.confirm_order_page_discount);
        servicePrice = (TextView) findViewById(R.id.confirm_order_page_service_price);
        orderPrice = (TextView) findViewById(R.id.confirm_order_page_order_total);
//        backToSelect = (Button)findViewById(R.id.cancel_order);
        confirm = (Button) findViewById(R.id.confirm_order);
    }

    private void setView() {

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.setCursorVisible(true);
                phone.setFocusable(true);
                phone.setSelection(item.getPhone().length());
                InputMethodManager imm = (InputMethodManager) phone.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        });
        movieName.setText(item.getMovieName());
        theaterName.setText(item.getTheaterName());
        theaterHall.setText(item.getTheaterHall());
        movieTime.setText(item.getDate());
        phone.setText(item.getPhone());
        phone.setCursorVisible(false);
        ticketsPrice.setText(item.getPrice() + " * " + item.getAmount() + " = "
                + (item.getPrice() * item.getAmount()) + "元");
        discount.setText((item.getDiscount()) + "%");
        servicePrice.setText(item.getServiceFee() + "元/张");
        orderPrice.setText(getIntent().getIntExtra("total",-1) + "元");

        int[] seats = item.getSeats();
        for (int x = 0; x < seats.length / 6 + 1; x++) { // 循环设置表格行
            TableRow row = new TableRow(this); // 定义表格行
            int num = 6;
            if (x == seats.length / 6)
                num = seats.length % 6;
            for (int y = x * 6, i = 0; i < num; i++, y++) {
                TextView text = new TextView(this);
                text.setText("  " + (seats[y] / 8 + 1) + "排" + (seats[y] % 8 + 1) + "座");
                row.addView(text, i);
            }
            seatsSelected.addView(row); // 向表格之中增加表格行
        }


        // 跳转到支付页面
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOrder();
            }
        });
    }

    private void makeOrder() {
        HashMap<String , Object> map = new HashMap<>();
        map.put("user_id", sharedPreferences.getInt("user_id", -1));
        map.put("product_id", product_id);
        map.put("type", 0);
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < item.getSeats().length; i++) {
            temp.add(item.getSeats()[i]);
        }
        map.put("position_num", temp);
        PostUtil.newInstance().sendPost(this, makeOrderURL, map);
    }

    @Override
    public void updateView(String url, String response) {
        if (response == null) {
            ToastUtil.printToast(this, "network fail");
            return;
        }
        if (url.equals(makeOrderURL)) {
            makeOrderResponse detail = new Gson().fromJson(response, makeOrderResponse.class);
            if (detail.status) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = format.format(new Date());
                Bill bill = new Bill(null, sharedPreferences.getInt("user_id",-1),
                        getIntent().getIntExtra("movie_id",-1), date, item.getTheaterName(), item.getMovieName(),
                        getIntent().getIntExtra("total",-1), detail.ticket_id.toString());
                getBillDao().insert(bill);


                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ticket_id", detail.ticket_id);
                bundle.putInt("subtotal", getIntent().getIntExtra("total",-1));
                intent.setClass(ConfirmOrderActivity.this, PaymentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ToastUtil.printToast(this, "下订单失败");
                return;
            }
        }
    }
}
