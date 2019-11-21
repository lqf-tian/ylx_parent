package cn.lqf.core.controller;

import cn.lqf.core.entity.PageResult;
import cn.lqf.core.entity.Result;
import cn.lqf.core.pojo.ad.Content;
import cn.lqf.core.pojo.ad.ContentCategory;
import cn.lqf.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    //查询
    @RequestMapping("/findAll")
    public List<Content> getBrandList(){
        List<Content> list = contentService.findAll();
        return list;
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody Content content){
        try {
            contentService.add(content);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody Content content){
        try {
            contentService.update(content);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //根据id查找
    @RequestMapping("/findOne")
    public Content findOne(Long id){
        return contentService.findOne(id);
    }

    //分页条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, int page, int rows){
        return contentService.findPage(content,page,rows);
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            contentService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }
}
