package cn.lqf.core.service;

import cn.lqf.core.dao.specification.SpecificationDao;
import cn.lqf.core.dao.specification.SpecificationOptionDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.SpecEntity;
import cn.lqf.core.pojo.specification.Specification;
import cn.lqf.core.pojo.specification.SpecificationOption;
import cn.lqf.core.pojo.specification.SpecificationOptionQuery;
import cn.lqf.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    //分页条件查询
    @Override
    public PageResult search(Specification spec, int pageName, int pageSize) {
        PageHelper.startPage(pageName, pageSize);
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if (spec != null) {
            if (spec.getSpecName() != null && spec.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + spec.getSpecName() + "%");
            }
        }
        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(query);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //添加
    @Override
    public void add(SpecEntity specEntity) {
        //添加规格对象
        specificationDao.insertSelective(specEntity.getSpecification());
        //添加规格选项对象
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                option.setSpecId(specEntity.getSpecification().getId());
                specificationOptionDao.insertSelective(option);
            }
        }
    }

    //查询单个
    @Override
    public SpecEntity findOne(Long id) {
        //查询规格对象
        Specification specification = specificationDao.selectByPrimaryKey(id);
        //查询规格选项对象
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
        //将规格对象和选项集合封装到返回实体对象中
        return new SpecEntity(specification, specificationOptionList);
    }

    //修改
    @Override
    public void update(SpecEntity specEntity) {
        specificationDao.updateByPrimaryKeySelective(specEntity.getSpecification());

        //根据id删除对应饿规格选项集合数据
        SpecificationOptionQuery optionQuery=new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        specificationOptionDao.deleteByExample(optionQuery);
        //将新规格选项集合添加到规格选项表中
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                option.setSpecId(specEntity.getSpecification().getId());
                specificationOptionDao.insertSelective(option);
            }
        }
    }

    //删除
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            SpecificationOptionQuery optionQuery=new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationDao.deleteByPrimaryKey(id);
            specificationOptionDao.deleteByExample(optionQuery);
        }
    }

    //下拉框
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
