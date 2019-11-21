package cn.lqf.core.service;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;


public interface TypeTemplateService {

    //分页搜索查询
    PageResult seacher(TypeTemplate typeTemplate,int pageNum,int pageSize);

    //添加
    void add(TypeTemplate template);

    //删除
    void delete(Long[] ids);

    //查单个
    TypeTemplate findOne(Long id);

    //修改
    void update(TypeTemplate template);

    //新增商品分类的下拉框
    List<Map> selectOptionList();

    public List<Map> findBySpecList(Long id);
}
