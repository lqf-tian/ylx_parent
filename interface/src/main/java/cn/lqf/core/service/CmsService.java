package cn.lqf.core.service;

import java.util.Map;

public interface CmsService {
    //取数据
    public Map<String,Object> findGoodsData(Long goodsid);
    //根据取到的数据生成页面
    public void createStaticPage(Long goodsId,Map<String,Object> rootmap) throws Exception;
}
