package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 跳转的
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    @ResponseBody
    public String login(int organ,HttpServletRequest request){
        System.err.println(organ);
        return "跳转至C端 机构Id：" + organ;
    }
}
