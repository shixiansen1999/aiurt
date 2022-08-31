package com.aiurt.common.aspect;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/8/2918:01
 */

import com.aiurt.common.aspect.annotation.LimitSubmit;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LimitSubmitAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.aiurt.common.aspect.annotation.LimitSubmit)")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object handleSubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息
        LimitSubmit limitSubmit = method.getAnnotation(LimitSubmit.class);
        int submitTimeLimiter = limitSubmit.limit();
        String redisKey = limitSubmit.key();
        boolean needAllWait = limitSubmit.needAllWait();
        String key = getRedisKey(sysUser, joinPoint, redisKey);
        Object result = redisUtil.get(key);
        if (result != null) {
            throw new AiurtBootException("请勿重复访问！");
        }
        redisUtil.set(key, sysUser.getId(), submitTimeLimiter);
        try {
            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable e) {
            log.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
            throw e;
        } finally {
            if (!needAllWait) {
                redisUtil.del(redisKey);
            }
        }
    }

    /**
     * 支持多参数，从请求参数进行处理
     */
    private String getRedisKey(LoginUser sysUser, ProceedingJoinPoint joinPoint, String key) {
        // 这时候的key就是你在注解里面写的表达式：addAnnualNewPlan:#id
        if (key.contains("%s")) {
            key = String.format(key, sysUser.getId());
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                String item = parameterNames[i];
                // 这里是要将#id替换成真正的id值
                if (key.contains("#" + item)) {
                    key = key.replace("#" + item, joinPoint.getArgs()[i].toString());
                }
            }
        }
        return key.toString();
    }
}
