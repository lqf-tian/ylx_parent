package cn.lqf.core.listener;

import cn.lqf.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @Author: Lqf
 * @Date: 2019/11/27 14:44
 */
public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;
        try {
            String goodsId = atm.getText();
            solrManagerService.saveItemToSolr(Long.parseLong(goodsId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
