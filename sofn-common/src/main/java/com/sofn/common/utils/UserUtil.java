package com.sofn.common.utils;

import com.sofn.common.constants.Constants;
import org.apache.shiro.SecurityUtils;

/**
 * 用户相关工具类
 * Created by heyongjie on 2019/6/5 16:21
 */
public class UserUtil {

    private static RedisHelper redisHelper = SpringContextHolder.getBean(RedisHelper.class);

    /**
     * 获取当前登录用户ID
     * @return  String
     */
    public static String getLoginUserId(){
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        Object obj = redisHelper.hget(token,Constants.UserSession.id);
        if (obj == null) return "no user find";
        return obj.toString();
    }

}
