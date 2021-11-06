package com.endwork.service;

import com.endwork.entity.*;
import com.endwork.mapper.MonitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MonitorService {
    @Autowired
    private MonitorMapper monitorMapper;

    public Monitor verifyLogin(int monitor_id, String monitor_pwd) {
        return monitorMapper.getAMonitor(monitor_id, monitor_pwd);
    }

    public List<Store> getStoreByCondition(String field_name, String field_value, int start_page, int page_size) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }

        List<Store> storeList = monitorMapper.getStoreList(field_name, field_value, start, page_size);

        for (int i=0; i<storeList.size();i++){
            System.out.println(storeList.get(i));
        }

        return storeList;
    }

    public List<Stock> getStockList(int store_id, int start_page, int page_size) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }

        List<Stock> stockList = monitorMapper.getStockList(store_id, start, page_size);

        for (int i = 0; i < stockList.size(); i++) {
            Stock stock = stockList.get(i);
            Goods goods = getAEditGoods(stockList.get(i).getGoods_id());

            stock.setIn_price(goods.getIn_price());
            stockList.set(i, stock);
        }

        return stockList;
    }

    public Stock getAEditStock(int store_id, int goods_id) {
        return monitorMapper.getAEditStock(store_id, goods_id);
    }

    public int save_edit_stock(Stock stock) {
        return monitorMapper.save_edit_stock(stock.getStore_id(), stock.getGoods_id(), stock.getAmount());
    }

    public List<Goods> get_goods_list(String field_name, String field_value, int start_page, int page_size) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }

        List<Goods> goodsList = null;

        //若在查找商品时，要求根据门店编号 store_id 查找，则需采用多表查询，否则只需进行单表查询
        if(field_name.equals("store_id")){
            goodsList = monitorMapper.getStoreGoodsList(field_name, field_value, start, page_size);

            //按照门店编号 store_id 查找到对应门店的所有商品后， 还要根据 store_id 和各个 goods_id 查找相应的库存信息，
            //最后将查找到的库存信息一一存入 goodsList 对应的 Goods 对象
            for (int i = 0; i < goodsList.size(); i++) {
                Goods goods = goodsList.get(i);
                Stock stock = getAEditStock(Integer.parseInt(field_value), goods.getGoods_id());
                goods.setStore_goods_stock(stock);
                goodsList.set(i, goods);
            }

        }else {
            goodsList = monitorMapper.getGoodsList(field_name, field_value, start, page_size);
        }

        return goodsList;
    }

    public int add_a_goods(Goods goods) {
        return monitorMapper.add_a_goods(goods.getGoods_id(), goods.getGoods_name(), goods.getIn_price(), goods.getUnit_sale_price(), goods.getGoods_introduce());
    }

    public Goods getAEditGoods(int goods_id) {
        return monitorMapper.getAEditGoods(goods_id);
    }

    public int saveEditGoods(Goods goods) {
        return monitorMapper.saveEditGoods(goods.getGoods_id(), goods.getGoods_name(), goods.getIn_price(), goods.getUnit_sale_price(), goods.getGoods_introduce());
    }

    public Store getAEditStore(int store_id) {
        return monitorMapper.getAEditStore(store_id);
    }

    public int saveEditStore(Store store) {
        return monitorMapper.saveEditStore(store.getStore_id(), store.getStore_name(), store.getCash(), store.getAddress());
    }

    public List<Order> getOrderByCondition(String field_name, String field_value, int start_page, int page_size, int order_status) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }

        //getOrderListInPotency()方法根据订单效力只获取各个订单的订单号，门店id，订单金额，订单日期
        List<Order> orderList;
        if (order_status==1){
            orderList = monitorMapper.getOrderListInPotency(field_name, field_value, start, page_size, order_status);
        }else {
            orderList = monitorMapper.getOrderListInPotency(field_name, field_value, start, page_size, order_status);
        }


        //根据指定订单状态，获取符合要求的订单
        /*List<Order> assign_status_order_list = new ArrayList<Order>();
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getOrder_status()==order_status){
                assign_status_order_list.add(orderList.get(i));
            }
        }

        orderList = assign_status_order_list;*/

        for (int i=0; i<orderList.size(); i++){
            //get_order_sale_list()方法获取指定订单的各个商品条目信息 orderSaleList，完善该订单
            List<OrderSale> orderSaleList = monitorMapper.get_order_sale_list(orderList.get(i).getOrder_id());

            Order order = orderList.get(i);
            order.setOrderSaleList(orderSaleList);

            //把完善后的订单存回订单列表 orderList
            orderList.set(i, order);
        }

        return orderList;
    }

    public int add_goods_list(List<Goods> goodsList) {
        int insertNum = 0;
        for (int i=0; i<goodsList.size(); i++){
            insertNum += add_a_goods(goodsList.get(i));
        }

        return insertNum;
    }

    public List<Goods> get_store_for_add_goods_list(String field_name, String field_value, int start_page, int page_size) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }

        List<Goods> goodsList = monitorMapper.get_store_for_add_goods_list(field_name, field_value, start, page_size);


        for (int i=0; i<goodsList.size();i++){
            System.out.println(goodsList.get(i));
        }

        return goodsList;
    }


    public int add_stock_list(List<Stock> checked_goods_stock_list) {
        //向库存表 stock 中插入新记录
        int insertNum = 0;
        for (int i=0; i<checked_goods_stock_list.size(); i++){
            insertNum += add_a_stock(checked_goods_stock_list.get(i));

            //库存变化后，还要修改门店表 store 中对应门店的资金
            int store_id = checked_goods_stock_list.get(i).getStore_id();
            Store store = getAEditStore(store_id);
            store.setCash(store.getCash() - checked_goods_stock_list.get(i).getGoods_stock_price());
            saveEditStore(store);
        }





        return insertNum;
    }

    private int add_a_stock(Stock stock) {
        return monitorMapper.add_a_stock(stock.getStore_id(), stock.getGoods_id(), stock.getAmount());
    }

    public int save_edit_stock_list(List<Stock> stockList) {
        //改变指定门店对应的库存信息前，先验证该门店资金是否足够
        int total_price = 0;
        for (int i = 0; i < stockList.size(); i++) {
            total_price += stockList.get(i).getGoods_stock_price();
        }
        Store store = getAEditStore(stockList.get(0).getStore_id());
        if (store.getCash() < total_price){
            return 0;
        }


        //更新指定门店对应的库存信息
        int updateNum = 0;
        for (int i = 0; i < stockList.size(); i++) {
            updateNum += save_edit_stock(stockList.get(i));
        }

        //更新库存信息成功之后，把对应门店的资金扣去相应的总货款
        if (updateNum==stockList.size()){
            store.setCash(store.getCash() - total_price);
            int flag = saveEditStore(store);

            if (flag==1){
                return 1;
            }

        }
        return 0;
    }
}
