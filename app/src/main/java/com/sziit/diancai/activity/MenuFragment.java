package com.sziit.diancai.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sziit.diancai.R;
import com.sziit.diancai.adapter.MenuListAdapter;
import com.sziit.diancai.bean.MenuBean;
import com.sziit.diancai.utils.HttpPostUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends Fragment {

    private String tag;
    private MenuHandler handler = new MenuHandler();

    private ListView listview;
    private View rootView;

    private ArrayList<HashMap<String, Object>> chineseArrayList;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        tag = getTag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container,false);

        final String num = getArguments().getString("num");


        Button ok = (Button) rootView.findViewById(R.id.ok);
        listview = (ListView) rootView.findViewById(R.id.listView);


        GetDateThread getDateThread = new GetDateThread();
        getDateThread.start();



        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                MenuBean menuBean = null;
                ArrayList<MenuBean> menuArrayList =  new ArrayList<MenuBean>();
                int sum = 0;


                for(int i = 0; i < listview.getCount(); i++){
                    LinearLayout linearlayout = (LinearLayout)listview.getAdapter().getView(i, null, null);
                    EditText et = (EditText) linearlayout.findViewById(R.id.menu_list_quantity);
                    int temp = Integer.parseInt(et.getText().toString());
                    //如果数量大于0才发送到下个activity
                    if(temp>0){
                        TextView nametv = (TextView) linearlayout.findViewById(R.id.menu_list_name);
                        TextView pricetv = (TextView) linearlayout.findViewById(R.id.menu_list_price);
                        menuBean = new MenuBean();
                        menuBean.setName(nametv.getText().toString());
                        menuBean.setPrice(pricetv.getText().toString());
                        menuBean.setQuantity(temp);

                        //设置中文名
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map = chineseArrayList.get(i);
                        menuBean.setChinaName((String) map.get("name"));

                        menuArrayList.add(menuBean);
                        sum += Integer.parseInt(pricetv.getText().toString())*temp;
                    }
                }

                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), OrderActivity.class);
                intent.putExtra("menuArrayList", menuArrayList);
                intent.putExtra("num", num);
                intent.putExtra("totalprice", sum);
                startActivity(intent);
            }
        });
        return rootView;
    }




    class GetDateThread extends Thread{
        @Override
        public void run() {
            HttpPostUtil httppostutil = new HttpPostUtil();
            Map<String, String> params = new HashMap<String, String>();
            String result = HttpPostUtil.sendPostMessage(params, "utf-8", HttpPostUtil.MENU_PATH);

            ArrayList<HashMap<String, Object>> data = getData(result);
            showDate(data);
        }


        private void showDate(ArrayList<HashMap<String, Object>> data) {
            Message msg = Message.obtain(handler,MenuHandler.GET_MENU);
            msg.obj = data;
            msg.sendToTarget();
        }

        private ArrayList<HashMap<String, Object>> getData(String MenuResult) {
            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

            ArrayList<HashMap<String, Object>> cnarrayList = new ArrayList<HashMap<String, Object>>();
            try {
                JSONObject jsonobject = new JSONObject(MenuResult);
                JSONArray jsonarray = jsonobject.getJSONArray("menulist");
                // 根据需求添加一些数据,
                for(int i=0;i < jsonarray.length();i++){
                    HashMap<String, Object> chinesemap = new HashMap<String, Object>();
                    JSONObject json = (JSONObject) jsonarray.get(i);
                    chinesemap.put("image", json.getString("photo"));
                    chinesemap.put("name", json.getString("name"));
                    chinesemap.put("price", json.getString("price"));
                    cnarrayList.add(chinesemap);
                }

                if(tag.equals("中文菜单")){
                    arrayList = cnarrayList;
                }else if(tag.equals("英文菜单")){
                    for(int i=0;i < jsonarray.length();i++){
                        HashMap<String, Object> englishmap = new HashMap<String, Object>();
                        JSONObject json = (JSONObject) jsonarray.get(i);
                        englishmap.put("image", json.getString("photo"));
                        englishmap.put("name", json.getString("englishname"));
                        englishmap.put("price", json.getString("price"));
                        arrayList.add(englishmap);
                    }
                }else if(tag.equals("韩文菜单")){
                    for(int i=0;i < jsonarray.length();i++){
                        HashMap<String, Object> koreamap = new HashMap<String, Object>();
                        JSONObject json = (JSONObject) jsonarray.get(i);
//						koreamap.put("image", R.drawable.icon);
                        koreamap.put("image", json.getString("photo"));
                        koreamap.put("name", json.getString("koreaname"));
                        koreamap.put("price", json.getString("price"));
                        arrayList.add(koreamap);
                    }
                }else if(tag.equals("日文菜单")){
                    for(int i=0;i < jsonarray.length();i++){
                        HashMap<String, Object> japmap = new HashMap<String, Object>();
                        JSONObject json = (JSONObject) jsonarray.get(i);
//						japmap.put("image", R.drawable.icon);
                        japmap.put("image", json.getString("photo"));
                        japmap.put("name", json.getString("japname"));
                        japmap.put("price", json.getString("price"));
                        arrayList.add(japmap);
                    }
                }
                Message msg = Message.obtain(handler,MenuHandler.GET_CHINESE_MENU);
                msg.obj = cnarrayList;
                msg.sendToTarget();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return arrayList;
        }
    }

    class MenuHandler extends Handler{
        // 更新菜单
        public static final int GET_MENU = 0x0001;
        //获取中文菜单
        public static final int GET_CHINESE_MENU = 0x0002;
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GET_MENU) {
                ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) msg.obj;
                MenuListAdapter menulistadapter = new MenuListAdapter(
                        rootView.getContext(), data);
                listview.setAdapter(menulistadapter);
            }
            if(msg.what == GET_CHINESE_MENU){
                chineseArrayList =(ArrayList<HashMap<String, Object>>) msg.obj;
            }
        }
    }

}
