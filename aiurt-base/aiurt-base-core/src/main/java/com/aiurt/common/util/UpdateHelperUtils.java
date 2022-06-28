package com.aiurt.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/289:39
 */
public class UpdateHelperUtils {
    public UpdateHelperUtils() {
    }
    /**
     *
     * <p>Title: UpdateHelperUtils</p>
     * <p>Description: 对象copy </p>
     * @param
     * @return
     */
    public static Map<String, String> copyNullProperties(Object source, Object target) {
        Map<String, String> map = new HashMap(16);
        String[] properties = getNoNullProperties(target);
        String[] var4 = properties;
        int var5 = properties.length;
        for(int var6 = 0; var6 < var5; ++var6) {
            String key = var4[var6];
            try {
                Field nameField = source.getClass().getDeclaredField(key);
                Field nameField2 = target.getClass().getDeclaredField(key);
                nameField.setAccessible(true);
                nameField2.setAccessible(true);
                if(!nameField.get(source).toString().equals(nameField2.get(target).toString())) {
                    map.put(key, nameField.get(source).toString() + "=>" + nameField2.get(target).toString());
                }
            } catch (Exception var10) {

            }
        }
        BeanUtils.copyProperties(source, target, properties);
        return map;
    }

    /**
     *
     * <p>Title: UpdateHelperUtils</p>
     * <p>Description: 对象copy </p>
     * @param
     * @return
     */
    private static String[] getNoNullProperties(Object target) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet();
        PropertyDescriptor[] var4 = pds;
        int var5 = pds.length;
        for(int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor p = var4[var6];
            Object value = srcBean.getPropertyValue(p.getName());
            if(value != null) {
                noEmptyName.add(p.getName());
            }
        }
        String[] result = new String[noEmptyName.size()];
        return (String[])noEmptyName.toArray(result);
    }

}
