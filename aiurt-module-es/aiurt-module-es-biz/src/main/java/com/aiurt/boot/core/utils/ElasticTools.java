package com.aiurt.boot.core.utils;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.annotation.ElasticId;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.HighlightField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工具类
 */
public class ElasticTools {

    /**
     * 根据对象中的注解获取ID的字段值
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String getElasticId(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            ElasticId id = f.getAnnotation(ElasticId.class);
            if (id != null) {
                Object value = f.get(obj);
                if (value == null) {
                    return null;
                } else {
                    return value.toString();
                }
            }
        }
        return null;
    }

    /**
     * 返回实体类@Document注解上标注的索引名称
     *
     * @param clazz
     * @return
     */
    public static String getIndexName(Class<?> clazz) {
        Document document = clazz.getAnnotation(Document.class);
        if (ObjectUtil.isEmpty(document)) {
            return null;
        }
        String indexName = document.indexName();
        return indexName;
    }

    /**
     * @param sourceList 待分割的数据集合
     * @param isParallel 是否并行处理
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> sourceList, boolean isParallel) {
        final int BULK_COUNT = 5000;
        if (sourceList.size() <= BULK_COUNT) {
            List<List<T>> splitList = new ArrayList<>();
            splitList.add(sourceList);
            return splitList;
        }
        // 子列表数
        int limit = (sourceList.size() + BULK_COUNT - 1) / BULK_COUNT;
        if (isParallel) {
            return Stream.iterate(0, n -> n + 1)
                    .limit(limit)
                    .parallel()
                    .map(a -> sourceList.stream()
                            .skip(a * BULK_COUNT)
                            .limit(BULK_COUNT)
                            .parallel()
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
        } else {
            final List<List<T>> splitList = new ArrayList<>();
            Stream.iterate(0, n -> n + 1)
                    .limit(limit)
                    .forEach(i -> splitList.add(sourceList.stream().skip(i * BULK_COUNT).limit(BULK_COUNT).collect(Collectors.toList())));
            return splitList;
        }
    }

    /**
     * 获取Object中所有的字段有值的map组合
     *
     * @return
     */
    public static Map getFieldValue(Object obj) throws IllegalAccessException {
        Map map = new HashMap();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (ObjectUtil.isNotEmpty(field.get(obj))) {
                map.put(field.getName(), field.get(obj));
            }
        }
        return map;
    }

    /**
     * 返回实体类中存在@HighlightField注解标注的高亮名称
     *
     * @param clazz
     * @return
     */
    public static List<String> getHighlightField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> highlightFields = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            HighlightField annotation = field.getAnnotation(HighlightField.class);
            if (ObjectUtil.isNotEmpty(annotation)) {
                String name = annotation.name();
                if (ObjectUtil.isNotEmpty(name)) {
                    highlightFields.add(name);
                }
            }
        }
        return highlightFields;
    }
}
