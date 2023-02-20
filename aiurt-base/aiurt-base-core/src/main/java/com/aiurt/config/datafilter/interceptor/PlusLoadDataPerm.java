package com.aiurt.config.datafilter.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.aiurt.common.aspect.annotation.*;
import com.aiurt.config.datafilter.config.DataFilterProperties;
import com.aiurt.config.datafilter.entity.ModelDataPermInfo;
import com.aiurt.config.datafilter.utils.ApplicationContextHolder;
import com.aiurt.config.datafilter.utils.MyModelUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/616:47
 */
@Slf4j
@Component
public class PlusLoadDataPerm {

    @Autowired
    private DataFilterProperties properties;

    /**
     * 对象缓存。由于Set是排序后的，因此在查找排除方法名称时效率更高。
     * 在应用服务启动的监听器中(LoadDataFilterInfoListener)，会调用当前对象的(loadInfoWithDataFilter)方法，加载缓存。
     */
    private final Map<String, ModelDataPermInfo> cachedDataPermMap = new ConcurrentHashMap<>();

    /**
     * 预先加载与数据过滤相关的数据到redis，该函数会在(LoadDataFilterInfoListener)监听器中调用。
     */
    public void loadInfoWithDataFilter() {
        // 获取一个接口下所有实现类 执行方法或者获取实现类对象等
        Map<String, BaseMapper> mapperMap = ApplicationContextHolder.getApplicationContext().getBeansOfType(BaseMapper.class);

        for (BaseMapper<?> mapperProxy : mapperMap.values()) {
            // 优先处理jdk的代理
            Object proxy = ReflectUtil.getFieldValue(mapperProxy, "h");
            // 如果不是jdk的代理，再看看cjlib的代理。
            if (proxy == null) {
                proxy = ReflectUtil.getFieldValue(mapperProxy, "CGLIB$CALLBACK_0");
            }
            Class<?> mapperClass = (Class<?>) ReflectUtil.getFieldValue(proxy, "mapperInterface");
            if (properties.getEnabledDataPermFilter()) {
                if (mapperClass != null) {
                    EnableDataPerm rule = mapperClass.getAnnotation(EnableDataPerm.class);
                    if (rule != null) {
                        loadDataPermFilterRules(mapperClass, rule);
                    }
                }
            }
        }
    }

    private void loadDataPermFilterRules(Class<?> mapperClass, EnableDataPerm rule) {
        String sysPermissionMapper = "SysPermissionMapper";
        // 由于给数据权限Mapper添加@EnableDataPerm，将会导致无限递归，因此这里检测到之后，
        // 会在系统启动加载监听器的时候，及时抛出异常,SysPermissionMapper类上的@EnableDataPerm进行移除即可
        if (StringUtils.equals(sysPermissionMapper, mapperClass.getSimpleName())) {
            throw new IllegalStateException("Add @EnableDataPerm annotation to SysPermissionMapper is ILLEGAL!");
        }

        // 这里开始获取当前Mapper已经声明的的SqlId中，有哪些是需要排除在外的。
        // 排除在外的将不进行数据过滤。
        Set<String> excludeMethodNameSet = null;
        String[] excludes = rule.excluseMethodName();
        if (excludes.length > 0) {
            excludeMethodNameSet = new HashSet<>();
            for (String excludeName : excludes) {
                excludeMethodNameSet.add(excludeName);
                // 这里是给pagehelper中，分页查询先获取数据总量的查询。
                excludeMethodNameSet.add(excludeName + "_COUNT");
            }
        }

        // 获取Mapper关联的主表信息，包括表名，user、dept、line、station、major、system等过滤字段名。
        // clazz.getGenericSuperclass(); 获取父类的类型
        // p.getActualTypeArguments()[0]; 获取第一个参数
        Class<?> modelClazz = (Class<?>)
                ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Field[] fields = ReflectUtil.getFields(modelClazz);
        Field userFilterField = null;
        Field deptFilterField = null;
        Field lineFilterField = null;
        Field stationFilterField = null;
        Field majorFilterField = null;
        Field systemFilterField = null;
        for (Field field : fields) {
            if (null != field.getAnnotation(UserFilterColumn.class)) {
                userFilterField = field;
            }
            if (null != field.getAnnotation(DeptFilterColumn.class)) {
                deptFilterField = field;
            }
            if (null != field.getAnnotation(LineFilterColumn.class)) {
                lineFilterField = field;
            }
            if (null != field.getAnnotation(StaionFilterColumn.class)) {
                stationFilterField = field;
            }
            if (null != field.getAnnotation(MajorFilterColumn.class)) {
                majorFilterField = field;
            }
            if (null != field.getAnnotation(SystemFilterColumn.class)) {
                systemFilterField = field;
            }
            if (userFilterField != null
                    && deptFilterField != null
                    && lineFilterField != null
                    && stationFilterField != null
                    && majorFilterField != null
                    && systemFilterField != null) {
                break;
            }
        }

        // 通过注解解析与Mapper关联的Model，并获取与数据权限关联的信息，并将结果缓存。
        ModelDataPermInfo info = new ModelDataPermInfo();
        info.setMainTableName(MyModelUtil.mapToTableName(modelClazz));
        info.setMustIncludeUserRule(rule.mustIncludeUserRule());
        info.setExcludeMethodNameSet(excludeMethodNameSet);
        if (userFilterField != null) {
            info.setUserFilterColumn(MyModelUtil.mapToColumnName(userFilterField, modelClazz));
        }
        if (deptFilterField != null) {
            info.setDeptFilterColumn(MyModelUtil.mapToColumnName(deptFilterField, modelClazz));
        }
        if (lineFilterField != null) {
            info.setLineFilterColumn(MyModelUtil.mapToColumnName(lineFilterField, modelClazz));
        }
        if (stationFilterField != null) {
            info.setStationFilterColumn(MyModelUtil.mapToColumnName(stationFilterField, modelClazz));
        }
        if (majorFilterField != null) {
            info.setMajorFilterColumn(MyModelUtil.mapToColumnName(majorFilterField, modelClazz));
        }
        if (systemFilterField != null) {
            info.setSystemFilterColumn(MyModelUtil.mapToColumnName(systemFilterField, modelClazz));
        }
        cachedDataPermMap.put(mapperClass.getName(), info);
    }

    public ModelDataPermInfo getCachedDataPermMap(String key) {
        if (MapUtil.isNotEmpty(cachedDataPermMap)) {
            ModelDataPermInfo modelDataPermInfo = cachedDataPermMap.get(key);
            if (ObjectUtil.isNotEmpty(modelDataPermInfo)) {
                return modelDataPermInfo;
            }
        }
        return null;
    }
}
