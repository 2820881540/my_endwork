package com.endwork.mapper;

import com.endwork.entity.Client;
import com.endwork.entity.Goods;
import com.endwork.entity.Order;
import com.endwork.entity.OrderSale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface ClientMapper {


    Client getAClient(@Param("client_id")int client_id,
                      @Param("client_pwd")String client_pwd);

    List<Order> getClientOrderList(@Param("client_id")int client_id,
                                   @Param("field_name")String field_name,
                                   @Param("field_value")String field_value,
                                   @Param("start")int start,
                                   @Param("page_size")int page_size,
                                   @Param("order_status")int order_status);

    List<OrderSale> get_order_sale_list(@Param("order_id") int order_id);

    Order getABaseEditOrder(@Param("order_id")int order_id);

    int cancel_client_order_by_order_id(@Param("order_id") int order_id);

    int save_cancel_date(@Param("order_id")int order_id,
                         @Param("order_last_cancel_date")String order_last_cancel_date);

    int recover_client_order(@Param("order_id")int order_id);

    int consume_recover_client_order_chance(@Param("order_id")int order_id);

    List<Goods> get_goods_list_by_condition(@Param("field_name")String field_name,
                                            @Param("field_value")String field_value);

    List<Goods> get_store_goods_list(@Param("store_id")int store_id);

    int add_a_order(@Param("order_id")int order_id,
                     @Param("store_id")int store_id,
                     @Param("order_price")double order_price,
                     @Param("client_id")int client_id,
                     @Param("order_date")String order_date);
//    int add_a_order(Order order);

    int save_order_sale(@Param("order_id")int order_id,
                         @Param("goods_id")int goods_id,
                         @Param("sale_goods_amount")int sale_goods_amount);

    int add_order_sale(@Param("order_id")int order_id,
                       @Param("goods_id")int goods_id,
                       @Param("sale_goods_amount")int sale_goods_amount);

    Object get_max_order_id();

    void saveAEditClient(@Param("field_name")String field_name,
                         @Param("field_value")String field_value,
                         @Param("client_id") int client_id);
}
