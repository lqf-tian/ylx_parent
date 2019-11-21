package cn.lqf.core.service;

import cn.lqf.core.dao.specification.SpecificationOptionDao;
import cn.lqf.core.dao.template.TypeTemplateDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.specification.SpecificationOption;
import cn.lqf.core.pojo.specification.SpecificationOptionQuery;
import cn.lqf.core.pojo.template.TypeTemplate;
import cn.lqf.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    //分页条件查询
    @Override
    public PageResult seacher(TypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        TypeTemplateQuery templateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = templateQuery.createCriteria();
        if (typeTemplate != null){
            if (typeTemplate.getName() != null && typeTemplate.getName().length()>0){
                criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
        }
        Page<TypeTemplate> page = (Page<TypeTemplate>)typeTemplateDao.selectByExample(templateQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //添加
    @Override
    public void add(TypeTemplate template) {
        typeTemplateDao.insert(template);
    }

    //删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }
    //查找一个用来回显数据
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);

    }
    //修改
    @Override
    public void update(TypeTemplate template) {
        typeTemplateDao.updateByPrimaryKeySelective(template);
    }

    //新增商品分类的下拉框
    @Override
    public List<Map> selectOptionList() {
        return typeTemplateDao.selectOptionList();
    }

    /**
     *  1.根据模板id 查询模板对象
     *  2.从模板对象中 获取规格数据 获取的是json数据
     *  3.将json专List集合
     *  4.遍历集合对象
     *  5.遍历 根据规格id查询对应规格选项数据
     *  6.将规格选项 在封装到规格选项中一起返回
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        if (maps != null){
            for (Map map : maps) {
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                //根据规格id 获得规格选项数据
                criteria.andSpecIdEqualTo(specId);
                List<SpecificationOption> optionList = specificationOptionDao.selectByExample(query);
                //将规格封装到原来的map中
                map.put("options",optionList);
            }
        }
        return maps;
    }


}
