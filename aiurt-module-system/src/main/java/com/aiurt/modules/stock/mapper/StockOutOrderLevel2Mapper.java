package com.aiurt.modules.stock.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
import java.util.List;

import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description: stock_out_order_level2
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface StockOutOrderLevel2Mapper extends BaseMapper<StockOutOrderLevel2> {
    List<StockOutOrderLevel2> pageList(Page<StockOutOrderLevel2> page, @Param("condition") StockOutOrderLevel2 stockInOrderLevel2);
}
