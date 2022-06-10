package com.aiurt.boot.common.util.excel;

import com.aiurt.boot.common.util.excel.entity.ExcelImportEntity;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelImportUtil {
    public static List<ExcelImportEntity> getExcelImportEntityList(Class clz) {
        List<ExcelImportEntity> list = new ArrayList<ExcelImportEntity>();
        try {
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getSimpleName().equals("ExcelImport")) {
                        ExcelImportEntity entity = new ExcelImportEntity();
                        entity.setFieldName(field.getName());
                        Class<? extends Annotation> aClass = annotation.annotationType();
                        Method index = aClass.getDeclaredMethod("index");
                        entity.setIndex((int) index.invoke(annotation));
                        Method cls = aClass.getDeclaredMethod("clz");
                        entity.setClz((String) cls.invoke(annotation));
                        Method method = aClass.getDeclaredMethod("method");
                        entity.setMethod((String) method.invoke(annotation));
                        Method message = aClass.getDeclaredMethod("message");
                        entity.setMessage((String) message.invoke(annotation));
                        Method pattern = aClass.getDeclaredMethod("pattern");
                        entity.setParttern((String) pattern.invoke(annotation));
                        Method changeMethod = aClass.getDeclaredMethod("changeMethod");
                        entity.setChangeMethod((String) changeMethod.invoke(annotation));
                        list.add(entity);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取导入实体错误:{}", e.getMessage());
        }
        return list;
    }
}

