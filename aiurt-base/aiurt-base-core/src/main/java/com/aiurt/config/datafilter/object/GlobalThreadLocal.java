package com.aiurt.config.datafilter.object;

import cn.hutool.core.util.BooleanUtil;

import java.util.Map;

/**
 * 线程本地化数据管理的工具类。可根据需求自行添加更多的线程本地化变量及其操作方法。
 *
 * @author aiurt
 * @date 2022-07-18
 */
public class GlobalThreadLocal {

    /**
     * 存储数据权限过滤是否启用的线程本地化对象。
     * 目前的过滤条件，包括数据权限和租户过滤。
     */
    private static final ThreadLocal<Boolean> DATA_FILTER_ENABLE = ThreadLocal.withInitial(() -> Boolean.TRUE);
    private static final ThreadLocal<Boolean> DATA_DELETE = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final ThreadLocal<String> DATA_STRING = ThreadLocal.withInitial(() -> null); // 存储String类型的数据

    /**
     * 设置数据过滤是否打开。如果打开，当前Servlet线程所执行的SQL操作，均会进行数据过滤。
     *
     * @param enable 打开为true，否则false。
     * @return 返回之前的状态，便于恢复。
     */
    public static boolean setDataFilter(boolean enable) {
        boolean oldValue = DATA_FILTER_ENABLE.get();
        DATA_FILTER_ENABLE.set(enable);
        return oldValue;
    }

    /**
     * 判断当前Servlet线程所执行的SQL操作，是否进行数据过滤。
     *
     * @return true 进行数据权限过滤，否则false。
     */
    public static boolean enabledDataFilter() {
        return BooleanUtil.isTrue(DATA_FILTER_ENABLE.get());
    }

    /**
     * 清空该存储数据，主动释放线程本地化存储资源。
     */
    public static void clearDataFilter() {
        DATA_FILTER_ENABLE.remove();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private GlobalThreadLocal() {
    }

    /**
     * 设置 DataString 的值
     */
    public static String setDataString(String DataString){
        String oldValue = DATA_STRING.get();
        DATA_STRING.set(DataString);
        return oldValue;
    }

    /**
     * 获取 DataString 的值
     */
    public static String getDataString(){
        return DATA_STRING.get();
    }

    /**
     * 设置 DataString 的值
     */
    public static boolean setDataDelete(boolean DataDelete){
        boolean oldValue = DATA_DELETE.get();
        DATA_DELETE.set(DataDelete);
        return oldValue;
    }

    /**
     * 获取 DataString 的值
     */
    public static boolean getDataDelete(){
        return DATA_DELETE.get();
    }

}
