$(function(){
    if($("input[type='number']").eq(0).val()==0){
        alert("该门店暂未上架任何商品！");
    }

    //遍历获取的input button元素对象数组，绑定click事件
    var len = $("input[type='button']").length;
    for(var i = 0; i < len; i++){
        if($("input[type='button']").eq(i).val() == "+"){
            $("input[type='button']").eq(i).click(function(){
                var amount = $(this).prev().val();
                $(this).prev().val(++amount);
            });
        }else if($("input[type='button']").eq(i).val() == "-"){
            $("input[type='button']").eq(i).click(function(){
                var amount = $(this).next().val();
                if(amount>0){
                    $(this).next().val(amount-1);
                }

            });
        }
    }
});

$("#place_add_stock_list").click(function place_client_order(){
    var add_stock_list=[];
    var goods_stock_price = 0;
    var total_price = 0
    var amount_normal = 1;


    var goods_id;
    var store_id;
    var add_amount;
    var current_amount;
    var in_price;

    var store_name;
    var field_name;
    var field_value;
    var start_page;
    var page_size;
    var store_stock_start_page;
    var store_stock_page_size;

    $("input[type='checkbox']:checked").each(function(index, obj){

        goods_id = $(this).next().val();
        store_id = $(this).next().next().val();
        add_amount = $(this).prev().prev().val();
        current_amount = $(this).next().next().next().next().next().next().next().next().next().next().next().val();
        in_price = $(this).next().next().next().val();



        store_name = $(this).next().next().next().next().val();
        field_name = $(this).next().next().next().next().next().val();
        field_value = $(this).next().next().next().next().next().next().val();
        start_page = $(this).next().next().next().next().next().next().next().val();
        page_size = $(this).next().next().next().next().next().next().next().next().val();
        store_stock_start_page = $(this).next().next().next().next().next().next().next().next().next().val();
        store_stock_page_size = $(this).next().next().next().next().next().next().next().next().next().next().val();


        goods_stock_price = add_amount * in_price;
        total_price += goods_stock_price;

        if(goods_stock_price>0){
            add_stock_list.push({
                goods_id:goods_id,
                store_id:store_id,
                amount:Number(current_amount) + Number(add_amount),
                goods_stock_price:goods_stock_price
            })
        }else if(goods_stock_price==0){
            alert("库存增加数量不得为 0 ！")
        }else {
            amount_normal = 0;
            $(this).prev().prev().val(0);
            alert("新增库存数量“"+add_amount+"”填写有误！");
        }

    });


    if (add_stock_list.length>0 && amount_normal==1){
        alert("货款总额为" + total_price + "元");
        $.post({
            url:"add_store_stock_amount",
            data:JSON.stringify(add_stock_list),
            dataType:"text",
            contentType:"application/json",
            success: function(data){    //"库存增加成功！" 或者 "门店资金不足！"

                alert(data);//增加失败但正常返回至 success，则说明门店资金不足

                var str = "库存增加成功！";
                if(data.indexOf(str) != -1){

                    window.location.href ="show_store_stock_list?"+
                        "store_id="+store_id+"&store_name="+store_name+
                        "&field_name="+field_name+"&field_value="+field_value+
                        "&start_page="+start_page+"&page_size="+page_size+
                        "&store_stock_start_page="+store_stock_start_page+"&store_stock_page_size="+store_stock_page_size;

                }

            },
            error: function(data){
                alert("库存增加异常！");
            },
            traditional:true
        });
    }else if(add_stock_list.length==0){
        alert("您未选中任何商品！");
    }else{
        alert("请重新填写新增库存数量！");
    }

});