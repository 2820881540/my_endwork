//批量添加
$("#list_add").click(function () {
    var goods_list=[];
    //循环tbody里面所有的tr，并取出每行相对的值,填充到数组中
    $("#goodss tr").each(function (index,obj) {
        goods_list.push({
            goods_id:$("input[name='goods_id']",obj).val(),
            goods_name:$("input[name='goods_name']",obj).val(),
            goods_introduce:$("textarea[name='goods_introduce']",obj).val(),
            in_price:$("input[name='in_price']",obj).val(),
            unit_sale_price:$("input[name='unit_sale_price']",obj).val()

        })
    })
    //发起post请求
    $.post({
        url:"add_goods_list",
        success: function(data) {
            alert("添加成功！");
            window.location.href ="show_goods_list?field_name=all&field_value=&start_page=1&page_size=10";            /*$.post({
                url:"add_goods_list",
                success: function(data) {
                    //$(".result").html(data);
                    if (data.success) {
                        location.href = "@{show_goods_list(field_name='all', field_value='',start_page=0, page_size=10)}";
                        //
                    }
                },
                contentType:"application/json",
                data:JSON.stringify(goods_list)//将对象转为字符

            });*/
        },
        error: function(data) {

        },
        contentType:"application/json",
        data:JSON.stringify(goods_list),//将对象转为字符
        dataType:'text'
    });

});
//复制tr节点的内容
function addElement(x){
    $(x).parents("tr").clone().appendTo($("#goodss"));
}
//移除tr节点
function delElement(x){
    if($("#goods_table > tbody > tr").length > 1){
        $(x).parents("tr").remove();
    }
}