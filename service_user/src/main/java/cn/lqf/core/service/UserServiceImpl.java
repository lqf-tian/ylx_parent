package cn.lqf.core.service;

import cn.lqf.core.dao.user.UserDao;
import cn.lqf.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lqf
 * @Date: 2019/11/28 15:36
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue ssDestination;
    @Value("${template_code}")
    private String template_code;
    @Value("${sign_name}")
    private String sign_name;
    @Autowired
    private UserDao userDao;


    //发送验证码
    @Override
    public void sendCode(final String phone) {
        //1.生成一个随机的六位数
        StringBuffer sb = new StringBuffer();
        for (int i= 1 ; i < 7 ; i ++){
            int n = new Random().nextInt(10);
            sb.append(n);
        }
        //2.将手机号码为键  验证码位置存到redis  设置生存时间为10分钟
        redisTemplate.boundValueOps(phone).set(sb.toString(),60*10, TimeUnit.SECONDS);
       final String smsCode= sb.toString();
        //3.将手机号  短信内容  模板编号 签名 封装到map 消息发送给消息服务器
        jmsTemplate.send(ssDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",phone);
                mapMessage.setString("template_code",template_code);
                mapMessage.setString("sign_name",sign_name);
                Map map = new HashMap<>();
                map.put("code",smsCode);
                mapMessage.setString("param", JSON.toJSONString(map));
                return (Message)mapMessage;
            }
        });
    }

    //验证码校验
    @Override
    public Boolean checkSmsCode(String phone, String smsCode) {
        if (phone == null || smsCode == null || "".equals(phone) || "".equals(smsCode)){
            return false;
        }
        //1.获得redis中验证码
        String  redisSmsCode =(String) redisTemplate.boundValueOps(phone).get();
        //2.判断页面传入的数据和redis中获取到的验证码是否一致
        if (smsCode.equals(redisSmsCode)){
            return true;
        }
        return false;
    }

    //添加
    @Override
    public void add(User user) {
        userDao.insert(user);
    }
}
