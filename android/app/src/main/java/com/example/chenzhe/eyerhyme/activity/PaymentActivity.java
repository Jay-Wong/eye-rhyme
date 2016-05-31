package com.example.chenzhe.eyerhyme.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chenzhe.eyerhyme.R;
import com.example.chenzhe.eyerhyme.customInterface.viewController;
import com.example.chenzhe.eyerhyme.model.payResponse;
import com.example.chenzhe.eyerhyme.util.PostUtil;
import com.example.chenzhe.eyerhyme.util.ToastUtil;
import com.example.greendao.dao.Bill;
import com.example.greendao.dao.BillDao;
import com.example.greendao.dao.DaoMaster;
import com.example.greendao.dao.DaoSession;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.Query;

public class PaymentActivity extends AppCompatActivity implements viewController {
    @Bind(R.id.confirm_order)
    Button confirmOrder;
    @Bind(R.id.cancel_order)
    Button cancelOrder;
    private String payURL = "/movie_ticket/change_status";
    private ImageButton back;
    private TextView subtotal;
    private TextView timer;
    private int total;
    private int seconds = 900;
    private Handler handler;
    private SharedPreferences sharedPreferences;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;
    private Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        total = bundle.getInt("subtotal");
        bindView();
        setView();
        setupDatabase();
        Date curTime = new Date();
        Query query = getBillDao().queryBuilder().where(BillDao.Properties.TicketId.eq(getIntent().getSerializableExtra("ticket_id").toString())).build();

        List bills = query.list();
        bill = (Bill) bills.get(0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date billDate = null;
        try {
            billDate = formatter.parse(bill.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        seconds -= (curTime.getTime() - billDate.getTime()) / 1000;

        handler = new Handler();
        handler.postDelayed(runnable, 1000);
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

    private void bindView() {
        back = (ImageButton) findViewById(R.id.back_to_order);
        subtotal = (TextView) findViewById(R.id.payment_subtotal);
        timer = (TextView) findViewById(R.id.payment_page_time);
    }

    private void setView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        subtotal.setText("" + total);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPay();
            }
        });
    }

    private void pay() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", sharedPreferences.getInt("user_id", -1));
        map.put("ticket_id", getIntent().getSerializableExtra("ticket_id"));
        map.put("status", 1);
        PostUtil.newInstance().sendPost(this, payURL, map);
    }

    private void cancelPay() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", sharedPreferences.getInt("user_id", -1));
        map.put("ticket_id", getIntent().getSerializableExtra("ticket_id"));
        map.put("status", 2);
        PostUtil.newInstance().sendPost(this, payURL, map);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            if (seconds > 0) {
                int min = seconds / 60;
                int sec = seconds % 60;
                String min_str = "0" + min;
                String sec_str;
                if (sec < 10)
                    sec_str = "0" + sec;
                else
                    sec_str = "" + sec;
                timer.setText(min_str + ":" + sec_str);
            } else {
                finish();
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void updateView(String url, String response) {
        if (response == null) {
            ToastUtil.printToast(this, "network fail");
            return;
        }
        if (url.equals(payURL)) {
            payResponse detail = new Gson().fromJson(response, payResponse.class);
            if (detail.status) {
                getBillDao().delete(bill);
                Intent it = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(it);
            } else {
                ToastUtil.printToast(this, "支付/取消失败");
                return;
            }
        }
    }
}
