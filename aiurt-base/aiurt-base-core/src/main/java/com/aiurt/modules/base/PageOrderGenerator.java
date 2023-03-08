package com.aiurt.modules.base;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SqlInjectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.query.QueryGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
public class PageOrderGenerator {
    public static final String ORDER_TYPE_ASC = "ASC";


    /**
     * 获取排序规则构造器page排序
     * @param searchObj 查询实体
     * @param baseEntity 基类
     * @return QueryWrapper实例
     */
    public static <T> void  initPage(Page<T> page, T searchObj, BaseEntity baseEntity){
        long start = System.currentTimeMillis();
        String order = baseEntity.getOrder();
        String column = baseEntity.getColumn();

        // 排序字段为空
        if (StrUtil.isBlank(column) ||  StrUtil.isBlank(order)) {
            return ;
        }
        log.debug("排序规则>>列:" + column + ",排序方式:" + order);
        //字典字段，去掉字典翻译文本后缀
        if(column.endsWith(CommonConstant.DICT_TEXT_SUFFIX)) {
            column = column.substring(0, column.lastIndexOf(CommonConstant.DICT_TEXT_SUFFIX));
        }

        //SQL注入check
        SqlInjectionUtil.filterContent(column);

        List<OrderItem> orderItemList = new ArrayList<>();
        List<String> columnList = StrUtil.split(column, ',');
        List<String> orderList = StrUtil.split(order, ',');
        // 多字段排序，column 以及order 大小必须相等， 一一对应
        if (CollectionUtils.isNotEmpty(columnList) && CollectionUtils.isNotEmpty(orderList)
                && (orderList.size() == columnList.size())) {
            for (int i = 0; i < columnList.size(); i++) {
                String v = columnList.get(i);

                // 字典翻译处理
                if (v.endsWith(CommonConstant.DICT_TEXT_SUFFIX)) {
                    v = v.substring(0, v.lastIndexOf(CommonConstant.DICT_TEXT_SUFFIX));
                }

                // 字典值处理
                String tableFieldName = QueryGenerator.getTableFieldName(searchObj.getClass(), v);

                if (StrUtil.isBlank(tableFieldName)) {
                    log.debug("---排序规则初始化完成,耗时:{}毫秒----", System.currentTimeMillis()-start);
                    return;
                }

                //将字段的驼峰转换成下划线
                v = StrUtil.toUnderlineCase(tableFieldName);

                OrderItem item = new OrderItem();
                item.setColumn(v);
                if (StrUtil.indexOfIgnoreCase(orderList.get(i), PageOrderGenerator.ORDER_TYPE_ASC) >= 0) {
                    item.setAsc(true);
                } else {
                    item.setAsc(false);
                }
                orderItemList.add(item);
            }
        }
        page.setOrders(orderItemList);

        log.debug("---排序规则初始化完成,耗时:{}毫秒----", System.currentTimeMillis()-start);
    }
}
