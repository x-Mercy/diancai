package com.sziit.diancai.bean;

import java.io.Serializable;

public class MenuBean implements Serializable {
    //菜名
    String name;
    //价格
    String price;
    //数量
    int quantity;
    //中文菜名
    String chinaName;

    public String getChinaName() {
        return chinaName;
    }
    public void setChinaName(String chinaName) {
        this.chinaName = chinaName;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
