package cn.lqf.core.controller;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.template.TypeTemplate;
import cn.lqf.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    //列表
    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate typeTemplate, int page, int rows){
        return typeTemplateService.seacher(typeTemplate,page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate template){
        try {
            typeTemplateService.add(template);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

    //删除

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }

    //查找单个
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
       return typeTemplateService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate template){
        try {
            typeTemplateService.update(template);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }

    //新增商品分类的下拉框
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return typeTemplateService.selectOptionList();
    }
}
