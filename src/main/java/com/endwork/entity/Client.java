package com.endwork.entity;

import java.util.List;

public class Client {
    int client_id;
    String client_pwd;
    String client_name;
    String client_phone;
    private double client_balance;
    List<Order> orderList;

    public double getClient_balance() {
        return client_balance;
    }

    public void setClient_balance(double client_balance) {
        this.client_balance = client_balance;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_pwd() {
        return client_pwd;
    }

    public void setClient_pwd(String client_pwd) {
        this.client_pwd = client_pwd;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
