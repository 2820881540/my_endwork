package com.endwork.entity;

public class OrderSale {
    private int order_id;
    private int store_id;
    private int goods_id;
    private int client_id;
    private String goods_name;
    private int sale_goods_amount;
    private double unit_sale_price;


    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getSale_goods_amount() {
        return sale_goods_amount;
    }

    public double getUnit_sale_price() {
        return unit_sale_price;
    }

    public void setUnit_sale_price(double unit_sale_price) {
        this.unit_sale_price = unit_sale_price;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

}
