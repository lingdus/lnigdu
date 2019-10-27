package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * 获取token验证
 */
@RequestMapping("/wechat")
@Controller
public class WechatController {

    //获取token
    @RequestMapping(value = "/wx.do")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {


        System.out.println("========WechatController========= ");

        Enumeration pNames = request.getParameterNames();
        while (pNames.hasMoreElements()) {
            String name = (String) pNames.nextElement();
            String value = request.getParameter(name);
            // out.print(name + "=" + value);

            String log = "name =" + name + "     value =" + value;
        }

        String signature = request.getParameter("signature");/// 微信加密签名
        String timestamp = request.getParameter("timestamp");/// 时间戳
        String nonce = request.getParameter("nonce"); /// 随机数
        String echostr = request.getParameter("echostr"); // 随机字符串
        PrintWriter out = response.getWriter();

        //if (SignUtil.checkSignature(signature, timestamp, nonce)) {
        out.print(echostr);
//		}
        out.close();
//		out = null;
    }
}
