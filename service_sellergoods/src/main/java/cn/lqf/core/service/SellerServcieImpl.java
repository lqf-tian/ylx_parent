package cn.lqf.core.service;

import cn.lqf.core.dao.seller.SellerDao;
import cn.lqf.core.entity.PageResult;
import cn.lqf.core.pojo.seller.Seller;
import cn.lqf.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
public class SellerServcieImpl implements SellerService{

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Seller seller) {
        //1.手动添加创建时间
        seller.setCreateTime(new Date());
        //2.手动添加状态
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    //后台审核用户注册
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    //分页条件查询
    @Override
    public PageResult search(Seller seller, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        if (seller != null){
            //审核状态
            if (seller.getStatus() != null && !"".equals(seller.getStatus())){
                criteria.andStatusEqualTo(seller.getStatus());
            }
            //店铺名称
            if (seller.getName() != null && seller.getName().length()>0){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            //公司名称
            if (seller.getNickName() !=null && seller.getNickName().length()>0){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }

        }
        Page<Seller> page = (Page<Seller>)sellerDao.selectByExample(query);

        return new PageResult(page.getTotal(),page.getResult());
    }

    //查询单个用于回显
    @Override
    public Seller findOne(String sellerId) {

        return sellerDao.selectByPrimaryKey(sellerId);
    }
}
