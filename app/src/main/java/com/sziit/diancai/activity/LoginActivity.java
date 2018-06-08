package com.sziit.diancai.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sziit.diancai.R;
import com.sziit.diancai.utils.HttpPostUtil;

public class LoginActivity extends Activity {

    private LoginHandler handler = new LoginHandler();

    private ImageView ivCode = null;
    private EditText username,password,code ;
    private Button login,reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 添加actionbar左上角的返回键
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        code = (EditText) findViewById(R.id.login_code);
        login = (Button) findViewById(R.id.login);
        reset = (Button) findViewById(R.id.reset);

        //获取一个SharedPreferences对象
        SharedPreferences settings = getSharedPreferences("user_info", 0);
        String usernameStr = settings.getString("username", "");  //取出保存的NAME
        String passwordStr = settings.getString("password", ""); //取出保存的PASSWORD
        //Set value
        username.setText(usernameStr);  //将取出来的用户名赋予
        password.setText(passwordStr);  //将取出来的密码赋予

        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                username.setText("");
                password.setText("");
                code.setText("");
            }
        });

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LoginThread thread = new LoginThread();
                thread.start();
            }
        });

        // 验证码图片控件设置点击事件，刷新验证码
        ivCode = (ImageView) findViewById(R.id.code_image);
        ImageThread thread = new ImageThread();
        thread.start();

        ivCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageThread thread = new ImageThread();
                thread.start();
            }
        });

    }

    // 监听左上角返回
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
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override

    protected void onStop(){
        super.onStop();
        //获取一个SharedPreferences对象
        SharedPreferences settings = getSharedPreferences("user_info", 0);
        settings.edit()
                .putString("username", username.getText().toString())
                .putString("password", password.getText().toString())
                .commit();
    } //将用户名和密码保存进去


    class LoginHandler extends Handler {
        // 更新验证码
        public static final int SHOW_NETWORK_IMAGE = 0x0001;
        // 显示信息
        public static final int SHOW_MESSAGE = 0x0002;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_NETWORK_IMAGE) {
                Bitmap bitmap = (Bitmap) msg.obj;
                ivCode.setImageBitmap(bitmap);
            }
            if (msg.what == SHOW_MESSAGE) {
                Toast.makeText(LoginActivity.this, msg.obj.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class ImageThread extends Thread {
        @Override
        public void run() {
            HttpPostUtil httppostutil = new HttpPostUtil();
            Bitmap bitmap = HttpPostUtil.getHttpBitmap(HttpPostUtil.IMAGE_PATH);
            showImage(bitmap);
        }

        /*
         * 通过handler对象将验证码图片发送给主线程并提醒其更新主界面验证码
         */
        private void showImage(Bitmap bitmap) {
            // 显示图片的操作不能出现在子线程中，需要由Handler来完成
            Message msg = Message.obtain(handler,LoginHandler.SHOW_NETWORK_IMAGE);
            msg.obj = bitmap;
            msg.sendToTarget();
        }
    }

    /*
     * 登陆线程，向服务端发送登陆验证请求的线程
     */
    class LoginThread extends Thread {
        @Override
        public void run() {
            HttpPostUtil httppostutil = new HttpPostUtil();
            //传入http请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username.getText().toString());
            params.put("password", password.getText().toString());
            params.put("code", code.getText().toString());
            String result = HttpPostUtil.sendPostMessage(params, "utf-8", HttpPostUtil.LOGIN_PATH);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("0")){
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("num", jsonObject.getString("num"));
                    intent.putExtras(bundle);
                    intent.setClass(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    showMessage(jsonObject.getString("mes"));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*
         * 如果登陆或者错误，则调用本方法通过handler对象，将登陆信息发送到主线程
         */
        private void showMessage(String message) {
            Message msg = Message.obtain(handler, LoginHandler.SHOW_MESSAGE);
            msg.obj = message;
            msg.sendToTarget();
            if (!message.contains("无法连接远程服务器")) {
                ImageThread thread = new ImageThread();
                thread.start();
            }
        }
    }

}
