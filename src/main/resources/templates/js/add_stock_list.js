$(function(){
    //遍历获取的input button元素对象数组，绑定click事件
    var len = $("input[type='button']").length;
    for(var i = 0; i < len; i++){
        if($("input[type='button']").eq(i).val() == "+"){
            $("input[type='button']").eq(i).click(function(){
                var initial_amount = $(this).prev().val();
                $(this).prev().val(++initial_amount);
            });
        }else if($("input[type='button']").eq(i).val() == "-"){
            $("input[type='button']").eq(i).click(function(){
                var initial_amount = $(this).next().val();
                if(initial_amount>0){
                    $(this).next().val(initial_amount-1);
                }

            });
        }
    }
});

$("#list_add").click(function add_checked_goods_stock_list(){
    var checked_goods_stock_list=[];
    var total_price = 0;
    $("input[type='checkbox']:checked").each(function(index, obj){

        var goods_id = $(this).next().val();
        var store_id = $(this).next().next().next().val();
        var amount = $(this).prev().prev().val();
        var in_price = $(this).next().next().next().next().val();
        var goods_stock_price = amount*in_price;

        total_price += goods_stock_price;

        checked_goods_stock_list.push({
            goods_id:goods_id,
            store_id:store_id,
            amount:amount,
            goods_stock_price:goods_stock_price
        })
    });

    alert("货款总额为" + total_price + "元");

    if (checked_goods_stock_list.length>0){
        $.post({
            url:"add_goods_list_to_store",
            data:JSON.stringify(checked_goods_stock_list),
            dataType:"text",
            contentType:"application/json",
            success: function(data){

                var store_id=$(" input[ name='store_id' ] ").val();
                var store_name=$(" input[ name='store_name' ] ").val();
                var field_name=$(" input[ name='field_name' ] ").val();
                var field_value=$(" input[ name='field_value' ] ").val();
                var start_page=$(" input[ name='start_page' ] ").val();
                var page_size=$(" input[ name='page_size' ] ").val();
                var store_exist_goods_start_page=$(" input[ name='store_exist_goods_start_page' ] ").val();
                var store_exist_goods_page_size=$(" input[ name='store_exist_goods_page_size' ] ").val();
                var store_for_add_goods_start_page=$(" input[ name='store_for_add_goods_start_page' ] ").val();
                var store_for_add_goods_page_size=$(" input[ name='store_for_add_goods_page_size' ] ").val();


                alert("添加成功！");
                window.location.href ="show_add_checked_goods_stock_list_to_store?"+
                    "store_id="+store_id+
                    "&store_name="+store_name+
                    "&field_name="+field_name+
                    "&field_value="+field_value+
                    "&start_page="+start_page+
                    "&page_size="+page_size+
                    "&store_exist_goods_start_page="+store_exist_goods_start_page+
                    "&store_exist_goods_page_size="+store_exist_goods_page_size+
                    "&store_for_add_goods_start_page="+store_for_add_goods_start_page+
                    "&store_for_add_goods_page_size="+store_for_add_goods_page_size;
            },
            error: function(data){
                alert("添加失败！");
            },
            traditional:true
        });
    }else {
        alert("未选中任何待添加商品！")
    }


});
