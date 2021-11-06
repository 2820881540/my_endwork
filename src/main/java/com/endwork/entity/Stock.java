package com.endwork.entity;

public class Stock {
    private int goods_id;
    private int store_id;
    private int amount;
    private String goods_name;
    private String store_name;
    private double in_price;
    private double goods_stock_price;   //向指定门店添加新产品时，goods_stock_price 存储当前 goods_id 对应商品初始库存的进价总额（即 amount * in_price）


    public double getIn_price() {
        return in_price;
    }

    public void setIn_price(double in_price) {
        this.in_price = in_price;
    }

    public double getGoods_stock_price() {
        return goods_stock_price;
    }

    public void setGoods_stock_price(double goods_stock_price) {
        this.goods_stock_price = goods_stock_price;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "goods_id=" + goods_id +
                ", store_id=" + store_id +
                ", amount=" + amount +
                ", goods_name='" + goods_name + '\'' +
                ", store_name='" + store_name + '\'' +
                ", goods_stock_price=" + goods_stock_price +
                '}';
    }
}
