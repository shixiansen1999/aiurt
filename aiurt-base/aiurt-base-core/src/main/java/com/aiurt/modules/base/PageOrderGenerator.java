package com.aiurt.modules.base;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SqlInjectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
     * @param parameterMap request.getParameterMap()
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

        // todo 需要处理该字段是否存在的情况, 只查询实体对

        String s = StrUtil.toUnderlineCase(column);

        List<String> columnList = StrUtil.split(s, ',');
        List<OrderItem> orderList = columnList.stream().map(v -> {
            OrderItem item = new OrderItem();
            item.setColumn(v);
            if (StrUtil.indexOfIgnoreCase(order, ORDER_TYPE_ASC) >= 0) {
                item.setAsc(true);
            } else {
                item.setAsc(false);
            }
            return item;
        }).collect(Collectors.toList());

        page.setOrders(orderList);

        log.debug("---排序规则初始化完成,耗时:{}毫秒----", System.currentTimeMillis()-start);
    }
}
