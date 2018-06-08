package com.sziit.diancai.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sziit.diancai.R;
import com.sziit.diancai.utils.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    //用map记录点菜数量
    Map<Integer, Integer> quantitymap;
    //异步加载图片类
    private ImageLoader imageLoader;


    public MenuListAdapter(Context context,
                           ArrayList<HashMap<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);

        quantitymap = new HashMap<Integer, Integer>();
        for (int i = 0; i < data.size(); i++) {
            quantitymap.put(i, 0);
        }

        imageLoader = new ImageLoader(context);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        final int q = quantitymap.get(position);
        String sq = String.valueOf(q);
        if (convertView == null) {
            //将布局视图封装到对象中，方便存储
            holder = new Holder();
            // 获取组件布局
            convertView = layoutInflater.inflate(R.layout.menu_list_item, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.menu_list_image);
            holder.name = (TextView) convertView.findViewById(R.id.menu_list_name);
            holder.price = (TextView) convertView.findViewById(R.id.menu_list_price);
            holder.reduce = (ImageButton) convertView.findViewById(R.id.menu_list_reduce);
            holder.add = (ImageButton) convertView.findViewById(R.id.menu_list_add);
            holder.quantity = (EditText) convertView.findViewById(R.id.menu_list_quantity);

            //将视图存储起来,用于重复使用
            convertView.setTag(holder);
        }else{
            //从第二进调用getView开始，coverView不再为空，且其Tag值还附带了一个视图模板
            holder = (Holder) convertView.getTag();
        }
        // 绑定数据、以及事件触发
//		holder.imageview.setImageResource((Integer) data.get(position).get("image"));

        //加载食物图片
        imageLoader.DisplayImage((String) data.get(position).get("image"), holder.imageview);
        holder.name.setText((String) data.get(position).get("name"));
        holder.price.setText((String) data.get(position).get("price"));
        holder.quantity.setText(sq);

        holder.add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                quantitymap.put(position, q+1);
                notifyDataSetChanged();
            }
        });

        holder.reduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //判断减少到0就不在减
                int temp = q-1;
                if(temp>0){
                    quantitymap.put(position,temp );
                }else{
                    quantitymap.put(position, 0 );
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class Holder {
        ImageView imageview;
        TextView name, price;
        ImageButton add, reduce;
        EditText quantity;
    }

}
