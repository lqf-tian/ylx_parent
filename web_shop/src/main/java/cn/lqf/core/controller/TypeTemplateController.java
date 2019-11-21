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

    //查找单个
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
       return typeTemplateService.findOne(id);
    }


    //根据模板id 查询规格的集合 和规格选项集合
    @RequestMapping("findBySpecList")
    public List<Map> findBySpecList(Long id){
        List<Map> list = typeTemplateService.findBySpecList(id);

        return list;
    }
}
