package com.endwork.controller;


/*
*   long mills = System.currentTimeMillis();
    Date date = new Date(mills);

    String date_format = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(date_format);

    System.out.println(sdf.format(date));
* */

import com.endwork.entity.*;
import com.endwork.service.ClientService;
import com.endwork.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.List;

@Controller
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private MonitorService monitorService;


    @RequestMapping("show_client_main")
    public String show_main(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        if (client == null){
            return "redirect:show_client_login";
        }


        model.addAttribute("client", client);
        return "client_main";

    }

    @RequestMapping("show_client_login")
    public String show_client_login(){

        return "client_login";
    }

    @RequestMapping("check_client_login")
    public String check_client_login(HttpServletRequest request, RedirectAttributes attributes) throws Exception{
        int client_id = Integer.parseInt(request.getParameter("client_id"));
        String client_pwd = request.getParameter("client_pwd");

        //在当前用户信息 client 对象存入session之前，设置client对象的client_id和client_pwd
        //以便于将client_id作为之后的多表查询的参数
        Client client = clientService.verifyLogin(client_id, client_pwd);
        /*client.setClient_id(client_id);
        client.setClient_pwd(client_pwd);*/

        if (client!=null){
            request.getSession().setAttribute("client", client);
            return "redirect:show_client_main";
        }else {
            return "redirect:show_client_login";
        }
    }


    @RequestMapping("show_client_rigister")
    public String show_client_rigister(){
        return "client_rigister";
    }

    //返回管理员主页
    @RequestMapping("return_to_client_main")
    public String return_to_client_main(HttpServletRequest request){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }else {
            return "redirect:show_client_main";
        }
    }


    //管理员登出，设置管理员用户 Session 失效
    @RequestMapping("return_to_client_login")
    public String return_to_client_login(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:show_client_login";
    }


    @RequestMapping("show_client_order_list")
    public String show_client_order_list(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }

        String valid_order_field_name = request.getParameter("valid_order_field_name");
        String valid_order_field_value = request.getParameter("valid_order_field_value");
        int valid_order_start_page = Integer.parseInt(request.getParameter("valid_order_start_page"));
        int valid_order_page_size = Integer.parseInt(request.getParameter("valid_order_page_size"));

        String invalid_order_field_name = request.getParameter("invalid_order_field_name");
        String invalid_order_field_value = request.getParameter("invalid_order_field_value");
        int invalid_order_start_page = Integer.parseInt(request.getParameter("invalid_order_start_page"));
        int invalid_order_page_size = Integer.parseInt(request.getParameter("invalid_order_page_size"));



        System.out.println("valid_order_page_size:"+valid_order_page_size);
        //调用getClientOrderByCondition方法，多传入一个client_id参数，用于查询当前用户的订单
        List<Order> valid_order_list = clientService.getClientOrderByCondition(client.getClient_id(), valid_order_field_name, valid_order_field_value, valid_order_start_page, valid_order_page_size, 1);
        List<Order> invalid_order_list = clientService.getClientOrderByCondition(client.getClient_id(), invalid_order_field_name, invalid_order_field_value, invalid_order_start_page, invalid_order_page_size, 0);


        model.addAttribute("client", client);

        model.addAttribute("valid_order_list",valid_order_list);
        model.addAttribute("valid_order_field_name", valid_order_field_name);
        model.addAttribute("valid_order_field_value", valid_order_field_value);
        model.addAttribute("valid_order_start_page", valid_order_start_page);
        model.addAttribute("valid_order_page_size", valid_order_page_size);


        model.addAttribute("invalid_order_list",invalid_order_list);
        model.addAttribute("invalid_order_field_name", invalid_order_field_name);
        model.addAttribute("invalid_order_field_value", invalid_order_field_value);
        model.addAttribute("invalid_order_start_page", invalid_order_start_page);
        model.addAttribute("invalid_order_page_size", invalid_order_page_size);

        //映射至 show_store_list 页面
        return "show_client_order_list";
    }




    @RequestMapping("cancel_client_order")
    public String cancel_client_order(HttpServletRequest request, Model model, RedirectAttributes attributes){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }


        int order_id = Integer.parseInt(request.getParameter("order_id"));
        String valid_order_field_name = request.getParameter("valid_order_field_name");
        String valid_order_field_value = request.getParameter("valid_order_field_value");
        int valid_order_start_page = Integer.parseInt(request.getParameter("valid_order_start_page"));
        int valid_order_page_size = Integer.parseInt(request.getParameter("valid_order_page_size"));

        String invalid_order_field_name = request.getParameter("invalid_order_field_name");
        String invalid_order_field_value = request.getParameter("invalid_order_field_value");
        int invalid_order_start_page = Integer.parseInt(request.getParameter("invalid_order_start_page"));
        int invalid_order_page_size = Integer.parseInt(request.getParameter("invalid_order_page_size"));

        int flag = clientService.cancel_client_order_by_order_id(order_id, client);

        attributes.addAttribute("valid_order_field_name", valid_order_field_name);
        attributes.addAttribute("valid_order_field_value", valid_order_field_value);
        attributes.addAttribute("valid_order_start_page", valid_order_start_page);
        attributes.addAttribute("valid_order_page_size", valid_order_page_size);

        attributes.addAttribute("invalid_order_field_name", invalid_order_field_name);
        attributes.addAttribute("invalid_order_field_value", invalid_order_field_value);
        attributes.addAttribute("invalid_order_start_page", invalid_order_start_page);
        attributes.addAttribute("invalid_order_page_size", invalid_order_page_size);

        return "redirect:show_client_order_list";
    }



    @RequestMapping("recover_client_order")
    public String recover_client_order(HttpServletRequest request, Model model, RedirectAttributes attributes){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }


        int order_id = Integer.parseInt(request.getParameter("order_id"));
        String valid_order_field_name = request.getParameter("valid_order_field_name");
        String valid_order_field_value = request.getParameter("valid_order_field_value");
        int valid_order_start_page = Integer.parseInt(request.getParameter("valid_order_start_page"));
        int valid_order_page_size = Integer.parseInt(request.getParameter("valid_order_page_size"));

        String invalid_order_field_name = request.getParameter("invalid_order_field_name");
        String invalid_order_field_value = request.getParameter("invalid_order_field_value");
        int invalid_order_start_page = Integer.parseInt(request.getParameter("invalid_order_start_page"));
        int invalid_order_page_size = Integer.parseInt(request.getParameter("invalid_order_page_size"));



        int flag = clientService.recover_client_order(order_id, client);

        attributes.addAttribute("valid_order_field_name", valid_order_field_name);
        attributes.addAttribute("valid_order_field_value", valid_order_field_value);
        attributes.addAttribute("valid_order_start_page", valid_order_start_page);
        attributes.addAttribute("valid_order_page_size", valid_order_page_size);

        attributes.addAttribute("invalid_order_field_name", invalid_order_field_name);
        attributes.addAttribute("invalid_order_field_value", invalid_order_field_value);
        attributes.addAttribute("invalid_order_start_page", invalid_order_start_page);
        attributes.addAttribute("invalid_order_page_size", invalid_order_page_size);

        return "redirect:show_client_order_list";
    }


    @RequestMapping("show_client_store_list")
    public String show_client_store_list(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }

        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        List<Store> client_store_list = monitorService.getStoreByCondition(field_name, field_value, start_page, page_size);


        model.addAttribute("client", client);

        //将门店信息放入 model
        model.addAttribute("client_store_list", client_store_list);
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);
        //映射至 show_store_list 页面
        return "show_client_store_list";
    }


    @RequestMapping("show_add_client_order")
    public String show_add_client_order(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }


        int store_id = Integer.parseInt(request.getParameter("store_id"));
        String store_name = request.getParameter("store_name");
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        int start_page = Integer.parseInt(request.getParameter("start_page"));
        int page_size = Integer.parseInt(request.getParameter("page_size"));



        //查询指定门店的已上架的商品信息
        List<Goods> store_goods_list = clientService.get_store_goods_list(store_id);


        model.addAttribute("client", client);
        //将门店信息放入 model
        model.addAttribute("store_goods_list", store_goods_list);

        model.addAttribute("store_id", store_id);
        model.addAttribute("store_name", store_name);
        model.addAttribute("start_page", start_page);
        model.addAttribute("page_size", page_size);
        model.addAttribute("field_name", field_name);
        model.addAttribute("field_value", field_value);

        return "add_client_order";
    }



    //客户端下单，控制器接收到一个 List<OrderSale> 订单条目集合
    @RequestMapping(path = "add_client_order", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String add_client_order(@RequestBody List<OrderSale> orderSaleList, HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");
        if (client == null){
            return "redirect:show_client_login";
        }

        int flag = 0;
        flag = clientService.add_order_list(orderSaleList, client);




        if (flag>0){
            return "订单提交成功！";
        }else {
            return  "库存不足，订单提交失败！";
        }
    }
}
