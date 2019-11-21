package cn.lqf.core.controller;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.ad.ContentCategory;
import cn.lqf.core.pojo.good.Brand;
import cn.lqf.core.service.CategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/contentCategory")
@RestController
public class CategoryController {
    @Reference
    private CategoryService categoryService;

    //查询
    @RequestMapping("/findAll")
    public List<ContentCategory> getBrandList(){
        List<ContentCategory> list = categoryService.findAll();
        return list;
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory contentCategory){
        try {
            categoryService.add(contentCategory);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try {
            categoryService.update(contentCategory);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //根据id查找
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return categoryService.findOne(id);
    }

    //分页条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory, int page, int rows){
        return categoryService.findPage(contentCategory,page,rows);
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            categoryService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }

}
