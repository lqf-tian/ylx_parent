package cn.lqf.core.dao.address;

import cn.lqf.core.pojo.address.Address;
import cn.lqf.core.pojo.address.AddressQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressDao {
    int countByExample(AddressQuery example);

    int deleteByExample(AddressQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Address record);

    int insertSelective(Address record);

    List<Address> selectByExample(AddressQuery example);

    Address selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Address record, @Param("example") AddressQuery example);

    int updateByExample(@Param("record") Address record, @Param("example") AddressQuery example);

    int updateByPrimaryKeySelective(Address record);

    int updateByPrimaryKey(Address record);
}