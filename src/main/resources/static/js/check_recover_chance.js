$(function(){
    //遍历获取的input button元素对象数组，绑定click事件
    var len = $("input[name='recover_chance']").length;
    for(var i = 0; i < len; i++){
        if($("input[name='recover_chance']").eq(i).val() < 1){
            $("input[name='recover_chance']").eq(i).next().click(function(){
                alert("恢复次数已耗尽！");
            });

            $("input[name='recover_chance']").eq(i).next().css("color","gray");

            $("input[name='recover_chance']").eq(i).next().css("font-style","oblique");

        }
    }
});