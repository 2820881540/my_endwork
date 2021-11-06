package com.endwork.controller;


import com.endwork.entity.*;
import com.endwork.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.io.IOException;
import java.util.List;

/*
*   long mills = System.currentTimeMillis();
    Date date = new Date(mills);

    String date_format = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(date_format);

    System.out.println(sdf.format(date));
* */


/*用户可以取消订单，取消的订单并不从数据库中删除，而是把对应订单的 order_status设为0（即无效），订单记录依然保存
*/
@Controller
public class MonitorController {
    @Autowired
    private MonitorService monitorService;

    @RequestMapping("show_monitor_main")
    public String show_main(){
        return "monitor_main";

    }

    @RequestMapping("show_monitor_login")
    public String show_monitor_login(){
        return "monitor_login";
    }


    @RequestMapping("show_goods_list")
    public String show_goods_list(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }

        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        List<Goods> goodsList = monitorService.get_goods_list(field_name, field_value, start_page, page_size);

        //将门店信息放入 model
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        //映射至 show_store_list 页面
        return "show_goods_list";
    }


    @RequestMapping(path = "show_add_goods")
    public String addGoods(HttpServletRequest request, Model model){

        model.addAttribute("field_name", request.getParameter("field_name"));
        model.addAttribute("field_value", request.getParameter("field_value"));
        model.addAttribute("start_page", request.getParameter("start_page"));
        model.addAttribute("page_size", request.getParameter("page_size"));

        return "add_goods_list";
    }


    @RequestMapping("add_a_goods")
    public String add_a_goods(HttpServletRequest request, RedirectAttributes redirectAttributes){
        int flag = 0;

        Goods goods = new Goods();

        goods.setGoods_id(Integer.parseInt(request.getParameter("goods_id")));
        goods.setGoods_name(request.getParameter("goods_name"));
        goods.setGoods_introduce(request.getParameter("goods_introduce"));
        goods.setIn_price(Double.parseDouble(request.getParameter("in_price")));
        goods.setUnit_sale_price(Double.parseDouble(request.getParameter("unit_sale_price")));

        flag = monitorService.add_a_goods(goods);

        if (flag>0){
            redirectAttributes.addAttribute("start_page", 1);
            redirectAttributes.addAttribute("page_size", 10);
            redirectAttributes.addAttribute("field_name", "all");
            redirectAttributes.addAttribute("field_value","");

            return "redirect:show_goods_list";
        }else {
            return  "error";
        }
    }


    @RequestMapping(path = "add_goods_list", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String add_goods_list(@RequestBody List<Goods> goodsList, Model model, RedirectAttributes redirectAttributes){
        int insertNum = 0;
        insertNum = monitorService.add_goods_list(goodsList);

        System.out.println(insertNum);

        if (insertNum == goodsList.size()){
            redirectAttributes.addAttribute("field_name", "all");
            redirectAttributes.addAttribute("field_value","");
            redirectAttributes.addAttribute("start_page", 1);
            redirectAttributes.addAttribute("page_size", 10);

            return "redirect:show_goods_list";
        }else {
            return  "error";
        }
    }


    @RequestMapping(path = "add_goods_list_to_store", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String add_goods_list_to_store(@RequestBody List<Stock> checked_goods_stock_list, Model model, RedirectAttributes redirectAttributes){

        for (int i = 0; i < checked_goods_stock_list.size(); i++) {
            System.out.println(checked_goods_stock_list.get(i));
        }

        //向指定门店批量添加新产品，实际上就是修改库存表，由新产品的商品编号和初始库存及指定的门店编号组成新的库存信息，存入库存表
        //增加库存表信息后，还要修改对应门店的资金
        int insertNum = 0;
        insertNum = monitorService.add_stock_list(checked_goods_stock_list);

        System.out.println(insertNum);

        if (insertNum == checked_goods_stock_list.size()){
            return "true";
        }else {
            return  "error";
        }
    }

    @RequestMapping(path = "show_add_checked_goods_stock_list_to_store")
    public String show_add_checked_goods_stock_list_to_store(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }

        //field_name、field_value、start_page、page_size为show_store_list页面使用的参数，
        //这里跳转后也会将这4个参数一起带入 show_store_stock_list.html页面，使得在用户
        //要求跳转回 show_store_list.html页面时，仍能定位到原来离开 show_store_list.html页面之前的
        //访问到的页数及设定的页面大小
        String store_id = request.getParameter("store_id");
        String store_name = request.getParameter("store_name");
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));


        int store_exist_goods_start_page = Integer.parseInt(request.getParameter("store_exist_goods_start_page"));
        int store_exist_goods_page_size = Integer.parseInt(request.getParameter("store_exist_goods_page_size"));
        int store_for_add_goods_start_page = Integer.parseInt(request.getParameter("store_for_add_goods_start_page"));
        int store_for_add_goods_page_size = Integer.parseInt(request.getParameter("store_for_add_goods_page_size"));

        //调用get_goods_list方法，传入一个store_id参数，用于查询指定门店的商品
        List<Goods> store_exist_goods_list = monitorService.get_goods_list("store_id", store_id, store_exist_goods_start_page, store_exist_goods_page_size);

        //调用 get_store_for_add_goods_list 方法，传入一个store_id参数，用于查询指定门店还没有（待添加）的商品
        List<Goods> store_for_add_goods_list = monitorService.get_store_for_add_goods_list("store_id", store_id, store_for_add_goods_start_page, store_for_add_goods_page_size);

        model.addAttribute("store_id", store_id);
        model.addAttribute("store_name", store_name);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("store_exist_goods_list", store_exist_goods_list);
        model.addAttribute("store_for_add_goods_list", store_for_add_goods_list);
        model.addAttribute("store_exist_goods_start_page", store_exist_goods_start_page);
        model.addAttribute("store_exist_goods_page_size", store_exist_goods_page_size);
        model.addAttribute("store_for_add_goods_start_page", store_for_add_goods_start_page);
        model.addAttribute("store_for_add_goods_page_size", store_for_add_goods_page_size);


        return "add_checked_goods_stock_list_to_store";
    }

    @RequestMapping("show_edit_goods")
    public String  show_edit_goods(HttpServletRequest request, Model model){

        int goods_id = Integer.parseInt(request.getParameter("goods_id"));
        Goods goods = monitorService.getAEditGoods(goods_id);


        model.addAttribute("goods", goods);
        model.addAttribute("field_name", request.getParameter("field_name"));
        model.addAttribute("field_value", request.getParameter("field_value"));
        model.addAttribute("start_page", request.getParameter("start_page"));
        model.addAttribute("page_size", request.getParameter("page_size"));

        return "show_edit_goods";
    }


    @RequestMapping("show_edit_store")
    public String  show_edit_store(HttpServletRequest request, Model model){

        int store_id = Integer.parseInt(request.getParameter("store_id"));
        Store store = monitorService.getAEditStore(store_id);


        model.addAttribute("store", store);
        model.addAttribute("field_name", request.getParameter("field_name"));
        model.addAttribute("field_value", request.getParameter("field_value"));
        model.addAttribute("start_page", request.getParameter("start_page"));
        model.addAttribute("page_size", request.getParameter("page_size"));

        return "show_edit_store";
    }


    @RequestMapping("save_edit_store")//
    public String save_edit_store(HttpServletRequest request, RedirectAttributes attributes){
        int flag = 0;

        Store store = new Store();
        store.setStore_id(Integer.parseInt(request.getParameter("store_id")));
        store.setStore_name( request.getParameter("store_name"));
        store.setAddress(request.getParameter("address"));
        store.setCash(Double.parseDouble(request.getParameter("cash")));

        flag = monitorService.saveEditStore(store);

        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");

        if (flag>0){
            attributes.addAttribute("field_name", field_name);
            attributes.addAttribute("field_value",field_value);
            attributes.addAttribute("start_page", start_page);
            attributes.addAttribute("page_size", page_size);

            return "redirect:show_store_list";
        }else{
            return "error";
        }
    }

    @RequestMapping("check_monitor_login")
    //@ResponseBody 注释此句，否则重定向会失败
    public String check_monitor_login(HttpServletRequest request, RedirectAttributes attributes) throws Exception{
        int monitor_id = Integer.parseInt(request.getParameter("monitor_id"));
        String monitor_pwd = request.getParameter("monitor_pwd");


        Monitor monitor = monitorService.verifyLogin(monitor_id, monitor_pwd);

        if (monitor!=null){
            request.getSession().setAttribute("monitor", monitor);
            return "redirect:show_monitor_main";
        }else {
            return "redirect:show_monitor_login";
        }
    }

    //返回管理员主页
    @RequestMapping("return_to_monitor_main")
    public String return_to_monitor_main(HttpServletRequest request){
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }else {
            return "redirect:show_monitor_main";
        }
    }

    //管理员登出，设置管理员用户 Session 失效
    @RequestMapping("return_to_monitor_login")
    public String return_to_monitor_login(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:show_monitor_login";
    }

    @RequestMapping("save_edit_goods")//
    public String save_edit_goods(HttpServletRequest request, RedirectAttributes attributes){
        int flag = 0;

        Goods goods = new Goods();
        goods.setGoods_id(Integer.parseInt(request.getParameter("goods_id")));
        goods.setGoods_name( request.getParameter("goods_name"));
        goods.setIn_price(Double.parseDouble(request.getParameter("in_price")));
        goods.setUnit_sale_price(Double.parseDouble(request.getParameter("unit_sale_price")));
        goods.setGoods_introduce(request.getParameter("goods_introduce"));

        flag = monitorService.saveEditGoods(goods);

        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");

        if (flag>0){
            attributes.addAttribute("field_name", field_name);
            attributes.addAttribute("field_value",field_value);
            attributes.addAttribute("start_page", start_page);
            attributes.addAttribute("page_size", page_size);

            return "redirect:show_goods_list";
        }else{
            return "error";
        }
    }


    @RequestMapping("show_store_list")
    public String show_store_list(HttpServletRequest request, Model model){
        //每个请求都要有验证代码
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }


        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        List<Store> storeList = monitorService.getStoreByCondition(field_name, field_value, start_page, page_size);

        //将门店信息放入 model
        model.addAttribute("storeList", storeList);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);

        //映射至 show_store_list 页面
        return "show_store_list";
    }


    @RequestMapping("show_order_list")
    public String show_order_list(HttpServletRequest request, Model model){
        //每个请求都要有验证代码
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }


        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");

        int valid_order_start_page = Integer.parseInt(request.getParameter("valid_order_start_page"));
        int valid_order_page_size = Integer.parseInt(request.getParameter("valid_order_page_size"));
        int invalid_order_start_page = Integer.parseInt(request.getParameter("invalid_order_start_page"));
        int invalid_order_page_size = Integer.parseInt(request.getParameter("invalid_order_page_size"));


        List<Order> valid_order_list = monitorService.getOrderByCondition(field_name, field_value, valid_order_start_page, valid_order_page_size, 1);
        List<Order> invalid_order_list = monitorService.getOrderByCondition(field_name, field_value, invalid_order_start_page, invalid_order_page_size, 0);

        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);

        model.addAttribute("valid_order_list", valid_order_list);
        model.addAttribute("valid_order_start_page", valid_order_start_page);
        model.addAttribute("valid_order_page_size", valid_order_page_size);

        model.addAttribute("invalid_order_list", invalid_order_list);
        model.addAttribute("invalid_order_start_page", invalid_order_start_page);
        model.addAttribute("invalid_order_page_size", invalid_order_page_size);

        return "show_order_list";
    }


    @RequestMapping("show_store_order_list")
    public String show_store_order_list(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }

        //field_name、field_value、start_page、page_size为show_store_list页面使用的参数，
        //这里跳转后也会将这4个参数一起带入 show_store_order_list.html页面，使得在用户
        //要求跳转回 show_store_list.html页面时，仍能返回到原来离开 show_store_list.html页面之前的
        //访问到的页数及设定的页面大小
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        //
        int store_valid_order_start_page = Integer.parseInt(request.getParameter("store_valid_order_start_page"));
        int store_valid_order_page_size = Integer.parseInt(request.getParameter("store_valid_order_page_size"));
        int store_invalid_order_start_page = Integer.parseInt(request.getParameter("store_invalid_order_start_page"));
        int store_invalid_order_page_size = Integer.parseInt(request.getParameter("store_invalid_order_page_size"));
        String store_id = request.getParameter("store_id");
        String store_name = request.getParameter("store_name");

        //调用getStoreOrderByCondition方法，传入一个store_id参数，用于查询指定门店的订单
        List<Order> store_valid_order_list = monitorService.getOrderByCondition("store_id", store_id, store_valid_order_start_page, store_valid_order_page_size, 1);
        List<Order> store_invalid_order_list = monitorService.getOrderByCondition("store_id", store_id, store_invalid_order_start_page, store_invalid_order_page_size, 0);



        model.addAttribute("store_id", store_id);
        model.addAttribute("store_name", store_name);

        model.addAttribute("store_valid_order_list", store_valid_order_list);
        model.addAttribute("store_valid_order_start_page", store_valid_order_start_page);
        model.addAttribute("store_valid_order_page_size", store_valid_order_page_size);

        model.addAttribute("store_invalid_order_list", store_invalid_order_list);
        model.addAttribute("store_invalid_order_start_page", store_invalid_order_start_page);
        model.addAttribute("store_invalid_order_page_size", store_invalid_order_page_size);


        //field_name、field_value、start_page、page_size也带入 show_store_order_list.html页面
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        //映射至 show_store_order_list 页面
        return "show_store_order_list";
    }

    @RequestMapping("show_store_stock_list")
    public String show_store_stock_list(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Monitor monitor = (Monitor) session.getAttribute("monitor");
        if (monitor == null){
            return "redirect:show_monitor_login";
        }

        //field_name、field_value、start_page、page_size为show_store_list页面使用的参数，
        //这里跳转后也会将这4个参数一起带入 show_store_stock_list.html页面，使得在用户
        //要求跳转回 show_store_list.html页面时，仍能定位到原来离开 show_store_list.html页面之前的
        //访问到的页数及设定的页面大小
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        //
        int store_stock_start_page = Integer.parseInt(request.getParameter("store_stock_start_page"));
        int store_stock_page_size = Integer.parseInt(request.getParameter("store_stock_page_size"));
        int store_id = Integer.parseInt(request.getParameter("store_id"));
        String store_name = request.getParameter("store_name");

        //调用getStockList方法，传入一个store_id参数，用于查询指定门店的库存
        List<Stock> store_stock_list = monitorService.getStockList(store_id, store_stock_start_page, store_stock_page_size);

        //将门店信息放入 model
        model.addAttribute("store_stock_list", store_stock_list);
        model.addAttribute("store_id", store_id);
        model.addAttribute("store_name", store_name);
        model.addAttribute("store_stock_start_page", store_stock_start_page);
        model.addAttribute("store_stock_page_size", store_stock_page_size);

        //field_name、field_value、start_page、page_size也带入 show_store_order_list.html页面
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        //映射至 show_store_order_list 页面
        return "show_store_stock_list";
    }


    @RequestMapping(path = "add_store_stock_amount", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String add_store_stock_amount(@RequestBody List<Stock> stockList, Model model, RedirectAttributes redirectAttributes){
        int flag = 0;
        flag= monitorService.save_edit_stock_list(stockList);

        //"库存增加成功！" 或者 "门店资金不足！"
        if (flag==1){
            return "库存增加成功！";
        }else {
            return  "门店资金不足";
        }
    }


    @RequestMapping("save_edit_stock")//
    public String save_edit_stock(HttpServletRequest request, RedirectAttributes attributes){
        int flag = 0;

        Stock stock = new Stock();
        stock.setStore_id( Integer.parseInt(request.getParameter("store_id")));
        stock.setGoods_id(Integer.parseInt(request.getParameter("goods_id")));
        stock.setGoods_name(request.getParameter("goods_name"));
        stock.setAmount(Integer.parseInt(request.getParameter("amount")) + Integer.parseInt(request.getParameter("addAmount")));

        flag = monitorService.save_edit_stock(stock);

        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");

        if (flag>0){
            attributes.addAttribute("store_id", stock.getStore_id());
            attributes.addAttribute("start_page", start_page);
            attributes.addAttribute("page_size", page_size);

            return "redirect:show_store_stockList";
        }else{
            return "error";
        }
    }
}
