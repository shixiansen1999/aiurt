package com.aiurt.modules.stock.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
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
@EnableDataPerm
public interface StockInOrderLevel2Mapper extends BaseMapper<StockInOrderLevel2> {
    List<StockInOrderLevel2> pageList(Page<StockInOrderLevel2> page, @Param("condition") StockInOrderLevel2 stockInOrderLevel2);
}
