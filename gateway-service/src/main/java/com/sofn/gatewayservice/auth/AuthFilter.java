package com.sofn.gatewayservice.auth;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sofn.common.constants.Constants;
import com.sofn.common.model.Result;
import com.sofn.common.utils.BoolUtils;
import com.sofn.common.utils.JsonUtils;
import com.sofn.common.utils.JwtUtils;
import com.sofn.common.utils.RedisHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 鉴权过滤器
 */
public class AuthFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger("AuthFilter");

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        logger.info(request.getRequestURI());
        if ("/sys/user/login".equalsIgnoreCase(request.getRequestURI())) {
            return false;
        }
        else if ("/sys/user/captcha".equalsIgnoreCase(request.getRequestURI())){
            return false;
        }
        else if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")){
            return false;
        }


        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank((token))) {
            token = request.getParameter("token");
        }

        if (StringUtils.isBlank(token) || JwtUtils.verify(token) || !redisHelper.hasKey(token)) {
            // 过滤该请求，不对其进行路由
            ctx.setSendZuulResponse(false);
            //返回错误代码
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());

            ctx.getResponse().setContentType("application/json; charset=utf-8");
            Result result = Result.error(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.getReasonPhrase());
            ctx.setResponseBody(JsonUtils.obj2json(result));
        }else {
            Object rememberMe = redisHelper.hget(token, Constants.UserSession.rememberMe);
            // 记住密码不需要每次刷新
            if (rememberMe == null || BoolUtils.isFalse(rememberMe.toString())){
                // 刷新过期时间
                redisHelper.expire(token, Constants.UserSession.expireTime);
            }
        }

        return null;
    }
}
