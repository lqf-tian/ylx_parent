package cn.lqf.core.controller;


import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.entity.SpecEntity;
import cn.lqf.core.pojo.specification.Specification;
import cn.lqf.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {


    @Reference
    private SpecificationService specificationService;
    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification spec, int page, int rows){
        return  specificationService.search(spec,page,rows);

    }

    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity){
        try {
            specificationService.add(specEntity);
            return  new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"添加失败");
        }

    }


    //根据主键查询
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        return specificationService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity){
        try{
            specificationService.update(specEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //删除
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //下拉框
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
