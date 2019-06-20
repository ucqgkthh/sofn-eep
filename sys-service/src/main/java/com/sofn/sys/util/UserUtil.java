package com.sofn.sys.util;

import com.sofn.common.constants.Constants;
import com.sofn.common.utils.RedisHelper;
import com.sofn.common.utils.SpringContextHolder;
import com.sofn.sys.model.SysUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
        Object obj = null;
        try {
            String token = (String) SecurityUtils.getSubject().getPrincipal();
            obj = redisHelper.hget(token,Constants.UserSession.id);
        }finally {
            if (obj == null) return null;
            return obj.toString();
        }
    }

}
