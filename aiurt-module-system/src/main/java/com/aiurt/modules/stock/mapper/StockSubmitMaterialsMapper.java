package com.aiurt.modules.stock.mapper;

import com.aiurt.modules.stock.entity.StockLevel2;
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
public interface StockSubmitMaterialsMapper extends BaseMapper<StockSubmitMaterials> {
    /**
     * 分页查询
     * @param page
     * @param stockSubmitMaterials
     * @return
     */
    List<StockSubmitMaterials> pageList(Page<StockSubmitMaterials> page, @Param("condition") StockSubmitMaterials stockSubmitMaterials);
}
