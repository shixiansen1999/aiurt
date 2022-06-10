package com.aiurt.boot.common.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/23 23:02
 */
public class MyBeanUtils {
    public static <T> List copyList(List<T> list, Class tClass) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList();
        }
        return JSON.parseArray(JSON.toJSONString(list), tClass);
    }

}
