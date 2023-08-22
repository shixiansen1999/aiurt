package com.aiurt.modules.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.converter.CustomDateConverter;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author fgw
 */
@Service
public class ReflectionService {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionService.class);

    private static final String HTTP_PREFIX = "http://";

    private static final String CODE_FIELD_NAME = "code";
    private static final String RESULT_FIELD_NAME = "result";
    private static final int SUCCESS_CODE = 200;

    @Resource
    private ApplicationContext applicationContext;

    private static final List<Class> WRAP_CLASS = Arrays.asList(Integer.class, Boolean.class, Double.class,Byte.class,Short.class, Long.class, Float.class, Double.class, BigDecimal.class, String.class);



    @Autowired
    private RestTemplate restTemplate;

    /**
     *
     * @param service
     * @param paramMap
     * @return
     */
    public Object proxy(String service, Map<String,Object> paramMap)  {

        // 检查输入参数的有效性
        if (service == null || paramMap == null) {
            throw new IllegalArgumentException("Invalid input parameter");
        }

        if (StrUtil.startWithIgnoreCase(service, HTTP_PREFIX)) {
            // 构造请求头
            HttpHeaders headers = createHttpHeaders();
            // 请求体
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);
            return doPost(httpEntity, service);
        } else {
            // 本地方法
            List<String> className = StrUtil.split(service, '.');
            try {
                return this.invokeService(className.get(0), className.get(1), paramMap);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new AiurtBootException("调用本地服务异常!");
            }
        }
    }


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
                // 需要转换，暂时不清楚原因, 否则属性值无法注入
                JSONObject jsonObject = new JSONObject(paramMap);
                String s = JSONObject.toJSONString(jsonObject);
                // 时间转换问题
                ConverterRegistry.getInstance().putCustom(java.util.Date.class, new CustomDateConverter(java.util.Date.class, null));
                BeanUtil.copyProperties(JSONObject.parseObject(s), object);
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
            return new HashMap(16);
        }else if(parameterType.isAssignableFrom(Set.class)) {
            return  new HashSet();
        }
        return parameterType.newInstance();
    }

    /**
     * 构造请求头， token， contentType
     * @return
     */
    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(CommonConstant.X_ACCESS_TOKEN, TokenUtil.getToken());
        return headers;
    }

    /**
     * post 请求
     * @param httpEntity
     * @param url
     * @return
     */
    private Object doPost(HttpEntity<Map<String, Object>> httpEntity, String url) {
        logger.info("post 请求：{}，请求参数：", url, httpEntity.getBody());
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, httpEntity, JSONObject.class);
        JSONObject body = getResponseBody(responseEntity);
        logger.info("请求结果：{}", JSONObject.toJSONString(body));
        int code = body.getIntValue(CODE_FIELD_NAME);
        String result = body.getString(RESULT_FIELD_NAME);
        String message = body.getString("message");
        if (code != SUCCESS_CODE && StrUtil.isBlank(result)) {
            throw new AiurtBootException("流程创建或者提交失败！"+ message);
        }
        return result;
    }

    private JSONObject getResponseBody(ResponseEntity<JSONObject> responseEntity) {
        JSONObject body = responseEntity.getBody();
        if (body == null) {
            throw new AiurtBootException("请求接口异常");
        }
        return body;
    }
}
