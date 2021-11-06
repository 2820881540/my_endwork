package com.endwork.service;

import com.endwork.entity.*;
import com.endwork.mapper.ClientMapper;
import com.endwork.mapper.MonitorMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ClientService {
    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private MonitorMapper monitorMapper;


    public Client verifyLogin(int client_id, String client_pwd) {
        return clientMapper.getAClient(client_id, client_pwd);
    }



    public List<Order> getClientOrderByCondition(int client_id ,String field_name, String field_value, int start_page, int page_size, int order_status) {
        int start = (start_page-1)*page_size;
        if (start < 0){
            start = 0;
        }



        System.out.println("订单状态"+order_status+" 页面大小:"+page_size);
        //getClientOrderList()方法只获取当前用户的各个订单的订单号，门店id，门店名，订单金额，订单日期
        List<Order> orderList = clientMapper.getClientOrderList(client_id, field_name, field_value, start, page_size, order_status);

        if(order_status==1){
            System.out.println("有效订单数："+orderList.size());
        }else {
            System.out.println("无效订单数："+orderList.size());
        }



        for (int i=0; i<orderList.size();i++){
            //get_order_sale_list()方法根据当前客户的各个订单的id,获取当前客户的各个订单的各个商品条目信息 client_order_sale_list，完善该订单
            List<OrderSale> client_order_sale_list = clientMapper.get_order_sale_list(orderList.get(i).getOrder_id());

            Order order = orderList.get(i);
            order.setOrderSaleList(client_order_sale_list);

            //把完善后的订单存回订单列表 orderList
            orderList.set(i, order);
        }

        return orderList;
    }

    //getAEditOrder 方法既要获取订单表的基本信息，还要获取订单条目表的信息
    public Order getAEditOrder(int order_id) {
        //1、获取订单表的基本信息
        Order order = clientMapper.getABaseEditOrder(order_id);

        //2、获取订单条目表的信息
            //get_order_sale_list()方法获取指定订单的各个商品条目信息 orderSaleList，完善该订单
        List<OrderSale> orderSaleList = monitorMapper.get_order_sale_list(order.getOrder_id());
        order.setOrderSaleList(orderSaleList);

        return order;
    }

    public int cancel_client_order_by_order_id(int order_id, Client client) {
        // field_name 一般指定为 "order_id"，field_value 一般指定为 order_id
        //用户取消订单，需先根据订单编号 order_id 把指定订单的订单状态设为 0，并存入取消日期，同时，门店退款（即修改门店资金 和对应的库存）


        //1、先根据订单编号 order_id 把指定要取消的订单的订单状态设为 0
        int isCancel = clientMapper.cancel_client_order_by_order_id(order_id);
        int isSave_cancel_date = 0;
        int isGet_refund = 0;
        int isUpdate_stock = 0;

        if (isCancel==1) {

            //获取当前系统时间
            long mills = System.currentTimeMillis();
            Date order_last_cancel_date = new Date(mills);
            String date_format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(date_format);

            //2、存入取消日期
            isSave_cancel_date = clientMapper.save_cancel_date(order_id, sdf.format(order_last_cancel_date));

            //3、门店退款（即修改门店资金）
            Order order = getAEditOrder(order_id);
            Store store = monitorMapper.getAEditStore(order.getStore_id());
            store.setCash(store.getCash() - order.getOrder_price());

            isGet_refund = monitorMapper.saveEditStore(store.getStore_id(), store.getStore_name(), store.getCash(), store.getAddress());

            //4、客户余额增加
            client.setClient_balance(client.getClient_balance() + order.getOrder_price());
            clientMapper.saveAEditClient("client_balance", client.getClient_balance()+"",client.getClient_id());

            //5、修改门店对应的库存信息
            //（1）先获取指定订单的对应订单条目
            List<OrderSale> orderSaleList = order.getOrderSaleList();
            int updateNum = 0;
            for (int i = 0; i < orderSaleList.size(); i++) {
                //（2）获取要取消订单的对应门店的库存信息
                Stock stock = monitorMapper.getAEditStock(store.getStore_id(), orderSaleList.get(i).getGoods_id());

                //（2）修改要取消订单的对应门店的库存信息（取消订单，库存增加）
                updateNum += monitorMapper.save_edit_stock(store.getStore_id(), orderSaleList.get(i).getGoods_id(), stock.getAmount() + orderSaleList.get(i).getSale_goods_amount());
            }
            if (updateNum==orderSaleList.size()){
                isUpdate_stock = 1;
            }

        }


        if(isCancel==1 && isSave_cancel_date==1 &&isGet_refund==1 && isUpdate_stock==1){
            return 1;
        }
        return 0;

    }



    public int recover_client_order(int order_id, Client client) {
        //用户恢复订单，需先根据订单编号 order_id 查看指定订单的 order_revover_chance机会，若机会为 0，则说明该订单不能恢复
        //若机会为 1，则可以恢复 ，先将指定订单的订单状态设为 1，同时，门店收款（即修改门店资金 和对应的库存）
        Order order = getAEditOrder(order_id);
        if(order.getRecover_chance()>0){
            //先将指定订单的订单状态设为 1
            int isRevover = clientMapper.recover_client_order(order_id);

            int isConsumeChance = clientMapper.consume_recover_client_order_chance(order_id);

            //门店收款（即修改门店资金）
            Store store = monitorMapper.getAEditStore(order.getStore_id());
            store.setCash(store.getCash() + order.getOrder_price());
            int isPay = monitorMapper.saveEditStore(store.getStore_id(), store.getStore_name(), store.getCash(), store.getAddress());

            //客户余额被相应扣除
            client.setClient_balance(client.getClient_balance() - order.getOrder_price());
            clientMapper.saveAEditClient("client_balance", client.getClient_balance()+"",client.getClient_id());


            //修改门店对应的库存信息
            //（1）先获取指定订单的对应订单条目
            List<OrderSale> orderSaleList = order.getOrderSaleList();
            int updateNum = 0;
            int isUpdate_stock = 0;
            for (int i = 0; i < orderSaleList.size(); i++) {
                //（2）获取要恢复的订单的对应门店的库存信息
                Stock stock = monitorMapper.getAEditStock(store.getStore_id(), orderSaleList.get(i).getGoods_id());

                //（2）修改要取消订单的对应门店的库存信息（恢复订单，库存减少）
                updateNum += monitorMapper.save_edit_stock(store.getStore_id(), orderSaleList.get(i).getGoods_id(), stock.getAmount() - orderSaleList.get(i).getSale_goods_amount());
            }
            if (updateNum==orderSaleList.size()){
                isUpdate_stock = 1;
            }

            if(isRevover==1 && isConsumeChance==1 && isPay==1 && isUpdate_stock==1){
                return 1;
            }
        }

        return 0;

    }

    public List<Goods> get_goods_list_by_condition(String field_name, String field_value) {
        return clientMapper.get_goods_list_by_condition(field_name, field_value);
    }

    public List<Goods> get_store_goods_list(int store_id) {
        return clientMapper.get_store_goods_list(store_id);

    }



    //用指定的订单条目集合组成一个新的订单，需新增一条订单表 order 的记录，新增多条订单条目表 order_sale 表的记录，
    //修改对应门店的资金， 修改对应门店的库存
    public int add_order_list(List<OrderSale> orderSaleList, Client client) {
        //用户未选中任何商品，则订单提交失败，返回 0
        if(orderSaleList.size()>0){
            for (int i = 0; i < orderSaleList.size(); i++) {
                OrderSale orderSale = orderSaleList.get(i);
                orderSale.setClient_id(client.getClient_id());
                orderSaleList.set(i, orderSale);
            }
        }else{
            return 0;
        }

        //若订单条目中任意一项商品库存不足 ，则提交订单失败
        for (int i = 0; i < orderSaleList.size(); i++) {
            Stock stock = monitorMapper.getAEditStock(orderSaleList.get(i).getStore_id(), orderSaleList.get(i).getGoods_id());
            if (stock.getAmount() < orderSaleList.get(i).getSale_goods_amount()){
                return 0;
            }
        }


        int total_price = 0;
        for (int i = 0; i < orderSaleList.size(); i++) {
            total_price += orderSaleList.get(i).getUnit_sale_price() * orderSaleList.get(i).getSale_goods_amount();

        }

        //获取当前系统时间
        long mills = System.currentTimeMillis();
        Date add_order_date = new Date(mills);
        String date_format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        String date = sdf.format(add_order_date);

        Order order = new Order();
        order.setStore_id(orderSaleList.get(0).getStore_id());
        order.setOrder_price(total_price);
        order.setClient_id(orderSaleList.get(0).getClient_id());
        order.setOrder_date(date);

        int max_order_id = 1;
        Object obj = clientMapper.get_max_order_id();
        if (obj==null){
            order.setOrder_id(max_order_id);
        }else{
            max_order_id = (int)obj+1;
            order.setOrder_id(max_order_id);
        }


        System.out.println(max_order_id);

        //1、新增订单记录
        int add_order = clientMapper.add_a_order(order.getOrder_id(), order.getStore_id(), order.getOrder_price(),order.getClient_id(), order.getOrder_date());

        //2、新增多条订单条目表的记录
        int order_sale_add_num = 0, add_order_sale = 0;
        for (int i = 0; i < orderSaleList.size(); i++) {
            order_sale_add_num += clientMapper.add_order_sale(order.getOrder_id(), orderSaleList.get(i).getGoods_id(), orderSaleList.get(i).getSale_goods_amount());
        }
        if (order_sale_add_num==orderSaleList.size()){
            add_order_sale = 1;
        }

        //3、修改客户余额
        client.setClient_balance(client.getClient_balance() - total_price);
        clientMapper.saveAEditClient("client_balance", client.getClient_balance()+"", client.getClient_id());

        //4、修改门店资金
        Store store = monitorMapper.getAEditStore(orderSaleList.get(0).getStore_id());
        store.setCash(store.getCash() + total_price);
        int save_store = monitorMapper.saveEditStore(store.getStore_id(), store.getStore_name(), store.getCash(), store.getAddress());

        //逐一修改对应门店的库存信息
        int update_stock_num = 0, update_stock = 0;
        for (int i = 0; i < orderSaleList.size(); i++) {
            Stock stock = monitorMapper.getAEditStock(orderSaleList.get(i).getStore_id(), orderSaleList.get(i).getGoods_id());
            stock.setAmount(stock.getAmount() - orderSaleList.get(i).getSale_goods_amount());
            update_stock_num = monitorMapper.save_edit_stock(stock.getStore_id(), stock.getGoods_id(), stock.getAmount());
        }
        if (update_stock_num==orderSaleList.size()){
            update_stock = 1;
        }


        //if (add_order==1 && add_order_sale==1 && save_store==1 && update_stock==1){
        if (add_order==1){
            return 1;
        }

        return 0;
    }
}
