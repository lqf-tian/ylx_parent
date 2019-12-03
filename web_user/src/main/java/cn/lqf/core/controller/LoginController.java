package cn.lqf.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Lqf
 * @Date: 2019/12/2 14:27
 */
@RequestMapping("/login")
@RestController
public class LoginController {
    //显示用户名
    @RequestMapping("/name")
    public Map getLoginName(){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> map = new HashMap<>();
        map.put("loginName",loginName);
        return map;
    }
}
