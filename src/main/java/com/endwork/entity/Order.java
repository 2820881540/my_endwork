package com.endwork.entity;

import java.util.List;

public class Order {
    private int order_id;
    private int store_id;
    private String store_name;
    private double order_price;
    private String order_date;
    private int client_id;
    private String client_name;
    private int order_status;
    private int recover_chance;




    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public int getRecover_chance() {
        return recover_chance;
    }

    public void setRecover_chance(int recover_chance) {
        this.recover_chance = recover_chance;
    }

    private List<OrderSale> orderSaleList;

    public double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(double order_price) {
        this.order_price = order_price;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public List<OrderSale> getOrderSaleList() {
        return orderSaleList;
    }

    public void setOrderSaleList(List<OrderSale> orderSaleList) {
        this.orderSaleList = orderSaleList;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }


    public void setOrder_price(int order_price) {
        this.order_price = order_price;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }
}
