package com.sziit.diancai.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.sziit.diancai.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public OrderListAdapter(Context context,
                            ArrayList<HashMap<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);

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
        if (convertView == null) {
            //将布局视图封装到对象中，方便存储
            holder = new Holder();
            // 获取组件布局
            convertView = layoutInflater.inflate(R.layout.order_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.order_item_name);
            holder.price = (TextView) convertView.findViewById(R.id.order_item_price);
            holder.quantity = (TextView) convertView.findViewById(R.id.order_item_quantity);
            //将视图存储起来,用于重复使用
            convertView.setTag(holder);
        }else{
            //从第二进调用getView开始，coverView不再为空，且其Tag值还附带了一个视图模板
            holder = (Holder) convertView.getTag();
        }
        holder.name.setText((String)data.get(position).get("name"));
        holder.price.setText((String)data.get(position).get("price"));

        holder.quantity.setText(String.valueOf(data.get(position).get("quantity")));

        return convertView;
    }

    class Holder {
        TextView name,price,quantity;
    }

}
