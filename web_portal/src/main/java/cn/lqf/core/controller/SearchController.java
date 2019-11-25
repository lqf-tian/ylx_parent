package cn.lqf.core.controller;

import cn.lqf.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {

    @Reference
    private SearchService searchService;

    //搜索功能的实现
    @RequestMapping("/search")
    public Map<String ,Object> sarcha(@RequestBody Map paramMap){
        Map<String, Object> resultMap = searchService.search(paramMap);
        return resultMap;
    }
}
