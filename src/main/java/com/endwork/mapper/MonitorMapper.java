package com.endwork.mapper;

import com.endwork.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MonitorMapper {
    Monitor getAMonitor(@Param("monitor_id")int monitor_id,
                        @Param("monitor_pwd")String monitor_pwd);

    List<Store> getStoreList(@Param("field_name")String field_name,
                             @Param("field_value")String field_value,
                             @Param("start")int start,
                             @Param("page_size")int page_size);

    List<Stock> getStockList(@Param("store_id")int store_id,
                             @Param("start")int start,
                             @Param("page_size") int page_size);

    Stock getAEditStock(@Param("store_id")int store_id,
                        @Param("goods_id") int goods_id);

    int save_edit_stock(@Param("store_id") int store_id,
                        @Param("goods_id") int goods_id,
                        @Param("amount") int amount);

    List<Goods> getGoodsList(@Param("field_name")String field_name,
                             @Param("field_value")String field_value,
                             @Param("start")int start,
                             @Param("page_size")int page_size);

    int add_a_goods(@Param("goods_id")int goods_id,
                    @Param("goods_name")String goods_name,
                    @Param("in_price")double in_price,
                    @Param("unit_sale_price")double unit_sale_price,
                    @Param("goods_introduce")String goods_introduce);

    Goods getAEditGoods(@Param("goods_id") int goods_id);

    int saveEditGoods(@Param("goods_id")int goods_id,
                      @Param("goods_name")String goods_name,
                      @Param("in_price")double in_price,
                      @Param("unit_sale_price")double unit_sale_price,
                      @Param("goods_introduce")String goods_introduce);

    Store getAEditStore(@Param("store_id") int store_id);

    int saveEditStore(@Param("store_id") int store_id,
                      @Param("store_name")String store_name,
                      @Param("cash")double cash,
                      @Param("address")String address);



    List<OrderSale> get_order_sale_list(@Param("order_id") int order_id);

    List<Goods> getStoreGoodsList(@Param("field_name")String field_name,
                                  @Param("field_value")String field_value,
                                  @Param("start")int start,
                                  @Param("page_size")int page_size);

    List<Goods> get_store_for_add_goods_list(@Param("field_name")String field_name,
                                             @Param("field_value")String field_value,
                                             @Param("start")int start,
                                             @Param("page_size")int page_size);

    int add_a_stock(@Param("store_id")int store_id,
                    @Param("goods_id")int goods_id,
                    @Param("amount")int amount);


    List<Order> getOrderList(@Param("field_name")String field_name,
                             @Param("field_value")String field_value,
                             @Param("start")int start,
                             @Param("page_size")int page_size);


    List<Order> getOrderListInPotency(@Param("field_name")String field_name,
                                  @Param("field_value")String field_value,
                                  @Param("start")int start,
                                  @Param("page_size")int page_size,
                                  @Param("order_status")int order_status);

}
