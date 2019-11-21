package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.SpecEntity;
import cn.lqf.core.pojo.specification.Specification;
import cn.lqf.core.pojo.specification.SpecificationOption;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    //带条件查询
    PageResult search(Specification spec, int pageName, int pageSize);

    //添加
    void add(SpecEntity specEntity);

    //通过id查询单个
    SpecEntity findOne(Long id);

    //对通过id查找的单个对象修改
    void update(SpecEntity specEntity);

    //删除
    void delete(Long[] ids);

    //规格下拉框
    List<Map> selectOptionList();


}
