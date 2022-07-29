package com.aiurt.modules.stock.mapper;

import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.entity.StockLevel2CheckDetail;
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
public interface StockLevel2CheckDetailMapper extends BaseMapper<StockLevel2CheckDetail> {
    List<StockLevel2CheckDetail> pageList(Page<StockLevel2CheckDetail> page, @Param("condition") StockLevel2CheckDetail stockLevel2CheckDetail);
}
