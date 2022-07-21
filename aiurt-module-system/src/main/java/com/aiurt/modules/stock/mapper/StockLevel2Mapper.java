package com.aiurt.modules.stock.mapper;

import com.aiurt.modules.stock.entity.StockLevel2;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface StockLevel2Mapper extends BaseMapper<StockLevel2> {

    List<StockLevel2> pageList(Page<StockLevel2> page, @Param("condition") StockLevel2 stockLevel2);
}
