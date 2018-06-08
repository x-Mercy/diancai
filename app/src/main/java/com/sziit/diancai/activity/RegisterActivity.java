package com.sziit.diancai.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sziit.diancai.R;
import com.sziit.diancai.activity.OrderActivity.OrderHandler;
import com.sziit.diancai.utils.HttpPostUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    private EditText user;
    private EditText password;
    private EditText repassword;
    private EditText num;

    private RegisterHandler handler = new RegisterHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 添加actionbar左上角的返回键
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (EditText) findViewById(R.id.register_User);
        password = (EditText) findViewById(R.id.register_Password);
        repassword = (EditText) findViewById(R.id.register_RePassword);
        num = (EditText) findViewById(R.id.register_num);

        Button register = (Button) findViewById(R.id.register);

        //监听注册按钮
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                RegisterThread thread = new RegisterThread();
                thread.start();
            }
        });

    }

    //监听左上角返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    //判断密码和重复密码是否相同
    private Boolean isEqual(){
        if(password.getText().toString().equals(repassword.getText().toString())){
            return true;
        }else {
            return false;
        }
    }

    //判断是否有空
    private Boolean isNotNull(){
        if(user.getText().toString().equals("")||password.getText().toString().equals("")||repassword.getText().toString().equals("")||num.getText().toString().equals("")){
            return false;
        }else{
            return true;
        }
    }

    class RegisterThread extends Thread {
        @Override
        public void run() {
            if(isEqual()&&isNotNull()){
                HttpPostUtil httppostutil = new HttpPostUtil();
                // 传入http请求参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", user.getText().toString());
                params.put("password", password.getText().toString());
                params.put("num", num.getText().toString());
                String result = HttpPostUtil.sendPostMessage(params, "utf-8",
                        HttpPostUtil.REGISTER_PATH);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equals("0")) {
                        showMessage(jsonObject.getString("mes"));
                        finish();
                    }else{
                        showMessage(jsonObject.getString("mes"));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showMessage("json解析错误");
                }
            }else{
                showMessage("密码和重复密码不相符或者参数未填");
            }
        }
        /*
         * 调用本方法通过handler对象，将信息发送到主线程
         */
        private void showMessage(String message) {
            Message msg = Message.obtain(handler, OrderHandler.MESSAGE);
            msg.obj = message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class RegisterHandler extends Handler {
        // 发送信息
        public static final int MESSAGE = 0x0001;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE) {
                Toast.makeText(RegisterActivity.this, msg.obj.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
