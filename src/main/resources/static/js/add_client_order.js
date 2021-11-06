$(function(){
    if($("input[type='number']").eq(0).val()==0){
        alert("该门店暂未正式营业！");
    }

    //遍历获取的input button元素对象数组，绑定click事件
    var len = $("input[type='button']").length;
    for(var i = 0; i < len; i++){
        if($("input[type='button']").eq(i).val() == "+"){
            $("input[type='button']").eq(i).click(function(){
                var amount = $(this).prev().prev().val();
                $(this).prev().prev().val(++amount);
            });
        }else if($("input[type='button']").eq(i).val() == "-"){
            $("input[type='button']").eq(i).click(function(){
                var amount = $(this).next().next().val();
                if(amount>0){
                    $(this).next().next().val(amount-1);
                }

            });
        }
    }
});

$("#place_client_order").click(function place_client_order(){
    var order_sale_list=[];
    var total_price = 0;
    var price_normal = 1;
    var client_balance = 0;
    $("input[type='checkbox']:checked").each(function(index, obj){

        var goods_id = $(this).next().val();
        var store_id = $(this).next().next().val();
        var sale_goods_amount = $(this).prev().prev().prev().prev().val();
        var unit_sale_price = $(this).next().next().next().val();

        client_balance = $(this).next().next().next().next().val();
        total_price += sale_goods_amount * unit_sale_price;

        if(total_price>0){
            order_sale_list.push({
                goods_id:goods_id,
                store_id:store_id,
                sale_goods_amount:sale_goods_amount,
                unit_sale_price:unit_sale_price
            })
        }else {
            price_normal = 0;
            $(this).prev().prev().prev().prev().val(0);
            alert("商品数量“"+sale_goods_amount+"”填写有误！");
        }

    });


    if (order_sale_list.length>0 && price_normal==1 && client_balance>=total_price){
        alert("订单总额为" + total_price + "元");
        $.post({
            url:"add_client_order",
            data:JSON.stringify(order_sale_list),
            dataType:"text",
            contentType:"application/json",
            success: function(data){
                alert(data)

                var str = "订单提交成功！";
                if(data.indexOf(str) != -1){
                    window.location.href ="show_client_order_list?"+
                        "valid_order_field_name=all&valid_order_field_value=0"+
                        "&valid_order_start_page=1&valid_order_page_size=10"+
                        "&invalid_order_field_name=all&invalid_order_field_value=0"+
                        "&invalid_order_start_page=1&invalid_order_page_size=10";
                }

            },
            error: function(data){
                alert("订单提交异常！");
            },
            traditional:true
        });
    }else if(client_balance<total_price){
        alert("余额不足，订单提交失败！");
    }else if(order_sale_list.length==0){
        alert("您未选中任何商品或商品数量有误，请重新填写！");
    }else {
        alert("请重新填写商品数量！");
    }


});