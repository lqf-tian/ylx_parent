package cn.lqf.core.controller;

import cn.lqf.core.pojo.ad.Content;
import cn.lqf.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    //广告查询
   /* @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }*/
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        List<Content> list=contentService.findByCategoryFormRedis(categoryId);
        return list;
    }

}
