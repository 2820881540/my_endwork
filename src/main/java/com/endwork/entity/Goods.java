package com.endwork.entity;

public class Goods {
    private int goods_id;
    private String goods_name;
    private String goods_introduce;
    private double in_price;
    private double unit_sale_price;
    private Stock store_goods_stock;        //存储该商品在指定门店的库存信息

    public Stock getStore_goods_stock() {
        return store_goods_stock;
    }

    public void setStore_goods_stock(Stock store_goods_stock) {
        this.store_goods_stock = store_goods_stock;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_introduce() {
        return goods_introduce;
    }

    public void setGoods_introduce(String goods_introduce) {
        this.goods_introduce = goods_introduce;
    }

    public double getIn_price() {
        return in_price;
    }

    public void setIn_price(double in_price) {
        this.in_price = in_price;
    }

    public double getUnit_sale_price() {
        return unit_sale_price;
    }

    public void setUnit_sale_price(double unit_sale_price) {
        this.unit_sale_price = unit_sale_price;
    }
}

