package cn.lqf.core.controller;

import cn.lqf.core.pojo.address.Address;
import cn.lqf.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/3 20:17
 */
@RequestMapping("/address")
@RestController
public class AddressController {
    @Reference
    private AddressService addressService;
    @RequestMapping("/findListByLoginUser")
    public List<Address> findListByLoginUser(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> addressList = addressService.findListByLoginUser(userName);
        return addressList;
    }
}
