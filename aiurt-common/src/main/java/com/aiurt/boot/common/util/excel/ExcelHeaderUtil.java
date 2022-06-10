package com.aiurt.boot.common.util.excel;

import com.aiurt.boot.common.util.excel.entity.ExcelHeaderEntity;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelHeaderUtil {
    public static List<ExcelHeaderEntity> getHeadres(Class clz) {
        List<ExcelHeaderEntity> list = new ArrayList<ExcelHeaderEntity>();
        try {
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getSimpleName().equals("Excel")) {
                        ExcelHeaderEntity entity = new ExcelHeaderEntity();
                        entity.setField(field.getName());
                        entity.setAClass(field.getType());
                        Class<? extends Annotation> aClass = annotation.annotationType();
                        Method[] declaredMethods = aClass.getDeclaredMethods();
                        for (Method method : declaredMethods) {
                            if (method.getName().contains("name")) {
                                entity.setName((String) method.invoke(annotation));
                                continue;
                            }
                            if (method.getName().contains("width")) {
                                entity.setWidth((Double) method.invoke(annotation));
                                continue;
                            }
                            if (field.getType().getName().contains("Date")) {
                                if (method.getName().contains("format")) {
                                    entity.setFormat((String) method.invoke(annotation));
                                    continue;
                                }
                            }
                        }
                        list.add(entity);
                    }
                }
            }
        } catch (Exception e) {
            log.error("表头错误信息:{}", e.getMessage());
        }
        return list;
    }
}
