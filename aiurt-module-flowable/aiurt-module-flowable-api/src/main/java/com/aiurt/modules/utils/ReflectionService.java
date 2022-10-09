package com.aiurt.modules.utils;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author fgw
 */
@Service
public class ReflectionService {

    @Resource
    private ApplicationContext applicationContext;

    private static final List<Class> WRAP_CLASS = Arrays.asList(Integer.class, Boolean.class, Double.class,Byte.class,Short.class, Long.class, Float.class, Double.class, BigDecimal.class, String.class);


    /**
     * 反射调用spring bean方法的入口
     * @param classz 类名
     * @param methodName 方法名
     * @param paramMap 实际参数
     * @throws Exception
     */
    public Object invokeService(String classz, String methodName, Map<String,Object> paramMap) throws Exception {
        if(!applicationContext.containsBean(classz)) {
            throw new RuntimeException("Spring找不到对应的Bean");
        }

        // 从Spring中获取代理对象（可能被JDK或者CGLIB代理）
        Object proxyObject = applicationContext.getBean(classz);

        // 获取代理对象执行的方法
        Method method = getMethod(proxyObject.getClass(), methodName);

        // 获取代理对象中的目标对象
        Class target = AopUtils.getTargetClass(proxyObject);

        // 获取目标对象的方法，为什么获取目标对象的方法：只有目标对象才能通过 DefaultParameterNameDiscoverer 获取参数的方法名，代理对象由于可能被JDK或CGLIB代理导致获取不到参数名
        Method targetMethod = getMethod(target, methodName);

        if(method == null) {
            throw new RuntimeException(String.format("没有找到%s方法", methodName));
        }

        // 获取方法执行的参数
        List<Object> objects = getMethodParamList(targetMethod, paramMap);

        // 执行方法
        Object invoke = method.invoke(proxyObject, objects.toArray());

        return invoke;
    }

    /**
     * 获取目标方法
     * @param proxyObject
     * @param methodStr
     * @return
     */
    private Method getMethod(Class proxyObject, String methodStr) {
        Method[] methods = proxyObject.getDeclaredMethods();

        for(Method method : methods) {
            System.out.println(method.getName());
            if(method.getName().equalsIgnoreCase(methodStr)) {
                return method;
            }
        }

        return null;
    }



    /**
     * 获取方法实际参数，不支持基本类型
     * @param method
     * @param paramMap
     * @return
     */
    private List<Object> getMethodParamList(Method method, Map<String, Object> paramMap) throws Exception {
        List<Object> objectList = new ArrayList<>();

        // 利用Spring提供的类获取方法形参名
        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] param =  nameDiscoverer.getParameterNames(method);

        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> parameterType = method.getParameterTypes()[i];

            Object object = null;
            // 基本类型不支持，支持包装类
            if(WRAP_CLASS.contains(parameterType)) {
                if(param != null && paramMap.containsKey(param[i])){
                    object = paramMap.get(param[i]);

                    object = ConvertUtils.convert(object, parameterType);
                }

            }else if (!parameterType.isPrimitive() ) {
                object = getInstance(parameterType);

                // 赋值
                //BeanUtils.populate(object, paramMap);
                BeanUtil.copyProperties(paramMap, object);
            }

            objectList.add(object);
        }

        return objectList;
    }

    /**
     * 获取类型实例
     * @param parameterType
     * @return
     * @throws Exception
     */
    private Object getInstance(Class<?> parameterType) throws Exception {
        if(parameterType.isAssignableFrom(List.class)) {
            return  new ArrayList();

        }else if(parameterType.isAssignableFrom(Map.class)) {
            return new HashMap();
        }else if(parameterType.isAssignableFrom(Set.class)) {
            return  new HashSet();
        }
        return parameterType.newInstance();
    }
}
