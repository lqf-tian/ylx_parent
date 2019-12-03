package cn.lqf.core.service;

import cn.lqf.core.pojo.address.Address;

import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/3 20:21
 */
public interface AddressService {
    public List<Address> findListByLoginUser(String userName);
}
