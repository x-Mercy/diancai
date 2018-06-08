package com.sziit.diancai.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sziit.diancai.R;
import com.sziit.diancai.adapter.OrderListAdapter;
import com.sziit.diancai.bean.MenuBean;
import com.sziit.diancai.utils.HttpPostUtil;


public class OrderActivity extends Activity {
    private ListView orderlistview;
    //菜单信息
    ArrayList<MenuBean> orderarrayList;
    private OrderHandler handler = new OrderHandler();
    // 座位号
    String num;
    // 日期，时间
    String datestr, timestr;
    //总价格
    int totalprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        // 获取由MenuActivity传过来的订单
        orderarrayList = (ArrayList<MenuBean>) getIntent()
                .getSerializableExtra("menuArrayList");
        // 订单列表
        orderlistview = (ListView) findViewById(R.id.order_listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(this,
                getData());
        orderlistview.setAdapter(orderListAdapter);

        // 获取系统时间日期
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm:ss");
        datestr = sDateFormat.format(new java.util.Date());
        timestr = sTimeFormat.format(new java.util.Date());

        // 日期
        TextView date = (TextView) findViewById(R.id.order_date);
        date.setText("日期:" + datestr);
        // 时间
        TextView time = (TextView) findViewById(R.id.order_time);
        time.setText("时间:" + timestr);

        // 座位号
        num = getIntent().getExtras().getString("num");
        TextView numTV = (TextView) findViewById(R.id.order_num);
        numTV.setText("座位号:" + num);

        // 合计
        totalprice = getIntent().getExtras().getInt("totalprice");
        TextView totalpriceTV = (TextView) findViewById(R.id.order_sum);
        totalpriceTV.setText("合计:" + totalprice);

        // 下单按钮
        Button orderbtn = (Button) findViewById(R.id.order_orderbtn);
        orderbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                crateAlertDialog().show();
            }
        });
    }

    private AlertDialog crateAlertDialog() {
        // 创建builder
        AlertDialog.Builder builder = new AlertDialog.Builder(
                OrderActivity.this);
        builder.setTitle("确认订单")
                // 标题
                .setCancelable(false)
                // 不响应back按钮
                .setMessage("是否确定下单")
                // 对话框显示内容
                // 设置按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderThread thread = new OrderThread();
                        thread.start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(OrderActivity.this, "取消",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // 创建Dialog对象
        AlertDialog dlg = builder.create();
        return dlg;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 解析传来的订单
    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> headmap = new HashMap<String, Object>();
        headmap.put("name", "菜名");
        headmap.put("price", "价格");
        headmap.put("quantity", "数量");
        arrayList.add(headmap);
        int i = 0;
        for (MenuBean menubean : orderarrayList) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", orderarrayList.get(i).getName());
            map.put("price", orderarrayList.get(i).getPrice());
            map.put("quantity", orderarrayList.get(i).getQuantity());
            i++;
            arrayList.add(map);
        }
        return arrayList;
    }

    class OrderThread extends Thread {
        @Override
        public void run() {
            HttpPostUtil httppostutil = new HttpPostUtil();
            // 传入http请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", num);
            params.put("date", datestr);
            params.put("time", timestr);
            params.put("totalprice", String.valueOf(totalprice));
            params.put("foodquantity", String.valueOf(orderarrayList.size()));
            for (int i = 0; i < orderarrayList.size(); i++) {
                params.put("food" + (i + 1), orderarrayList.get(i).getChinaName());
                params.put("quantity" + (i + 1),  String.valueOf(orderarrayList.get(i).getQuantity()));
            }
            String result = HttpPostUtil.sendPostMessage(params, "utf-8",
                    HttpPostUtil.ORDER_PATH);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("0")) {
                    showMessage(jsonObject.getString("mes"));
                    finish();
                } else {
                    showMessage(jsonObject.getString("mes"));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        private void showMessage(String message) {
            Message msg = Message.obtain(handler, OrderHandler.MESSAGE);
            msg.obj = message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class OrderHandler extends Handler {
        // 发送信息
        public static final int MESSAGE = 0x0001;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE) {
                Toast.makeText(OrderActivity.this, msg.obj.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}