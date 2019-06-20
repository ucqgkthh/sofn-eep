package com.sofn.common.log;

import com.sofn.common.model.Result;
import com.sofn.common.utils.JsonUtils;
import com.sofn.common.utils.WebUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 日志切面
 */
@Aspect
@Component
public class SofnLogAspect {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Pointcut("@annotation(sofnLog)")
    public void systemLogPointcut(SofnLog sofnLog) {
    }

    @Around("systemLogPointcut(sofnLog)")
    public Object aroundMethod(ProceedingJoinPoint point, SofnLog sofnLog) throws Throwable {
        long time = System.currentTimeMillis();
        try {
            Object returns = point.proceed();
            save(point, returns, sofnLog, System.currentTimeMillis() - time);
            return returns;
        } catch (Throwable e) {
            save(point, e, sofnLog, System.currentTimeMillis() - time);
            throw e;
        }
    }

    private void save(ProceedingJoinPoint point, Object returns, SofnLog sofnLog, Long time) {
        String sign = point.getSignature().toString();

        // 获取相关参数
        WebUtil wu = WebUtil.getInstance();
        // 请求对象
        HttpServletRequest req = wu.getRequest();
        // 当前用户
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        // 查询参数
        String qs = req.getQueryString();
        // url
        String url = req.getRequestURL().append(qs == null ? "" : "?" + qs).toString();
        // IP地址
        String ip = wu.getIpAddress();
        // 方法
        String method = req.getMethod();
        // 协议
        String protocol = req.getProtocol();
        int code = 0;
        String msg = null;
        String desc = !StringUtils.isEmpty(sofnLog.value()) ? sofnLog.value() : "";
        if (returns instanceof Result) {
            Result result = (Result) returns;
            code = result.getCode();
            msg = result.getMsg();
        }

        String text = null;
        if (returns != null) {
            String tmp;
            if (returns instanceof CharSequence) {
                tmp = returns.toString();
            } else if (returns instanceof Throwable) {
                String m = ((Throwable) returns).getMessage();
                String n = returns.getClass().getSimpleName();
                tmp = m == null ? n : n + ": " + m;
            } else {
                tmp = JsonUtils.obj2json(returns);
            }
            text = tmp;
        }

        // 构造入库参数
        Log log = new Log();
        log.setId(UUID.randomUUID().toString());
        // 用户信息
        log.setUsername(username);
        // 请求信息
        log.setIp(ip);
        log.setReqMethod(method + " " + protocol);
        log.setReqUri(url);
        // 执行信息
        log.setExecMethod(sign);
        log.setExecTime(time);
        // 请求参数
        if ("POST".equals(method)) {
            log.setArgs(JsonUtils.obj2json(point.getArgs()[0]));
        }
        log.setStatus(msg);
        log.setExecDesc(desc);
        // 响应信息
        log.setReturnVal(text);
        try {
            // 入库
            applicationEventPublisher.publishEvent(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
