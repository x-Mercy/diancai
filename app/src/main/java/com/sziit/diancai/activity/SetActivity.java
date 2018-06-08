package com.sziit.diancai.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sziit.diancai.R;
import com.sziit.diancai.utils.HttpPostUtil;

public class SetActivity extends Activity {

    private Button confirm_btn = null;
    private Button cancel_btn = null;
    private EditText ip = null;
    private EditText port = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        // 获取确定按钮控件与设置其单击监听事件
        confirm_btn = (Button) findViewById(R.id.confirm);
        confirm_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 限定ip输入框输入类型
                ip = (EditText) findViewById(R.id.login_ip);
                // 限定port输入框输入类型
                port = (EditText) findViewById(R.id.login_port);
                String ips = ip.getText().toString().trim();
                String ports = port.getText().toString().trim();
                int num = 0;
                for (int i = 0; i < ips.length(); i++) {
                    if (ips.charAt(i) == '.')
                        num++;
                }
                if (num == 3 && !(ips.endsWith(".")) && !(ips.startsWith("."))) {

                    HttpPostUtil.setBaseUrl(ips, ports);
                    // 结束当前视图
                    SetActivity.this.finish();
                } else {
                    Toast.makeText(SetActivity.this, "IP格式错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        cancel_btn = (Button) findViewById(R.id.cancel);
        cancel_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束当前视图
                SetActivity.this.finish();
            }
        });
    }

}
