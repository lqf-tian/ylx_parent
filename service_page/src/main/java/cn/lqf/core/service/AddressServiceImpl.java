package cn.lqf.core.service;

import cn.lqf.core.dao.address.AddressDao;
import cn.lqf.core.pojo.address.Address;
import cn.lqf.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/3 20:22
 */
@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    private AddressDao addressDao;
    @Override
    public List<Address> findListByLoginUser(String userName) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userName);
        List<Address> addressesList = addressDao.selectByExample(query);
        return addressesList;
    }
}
