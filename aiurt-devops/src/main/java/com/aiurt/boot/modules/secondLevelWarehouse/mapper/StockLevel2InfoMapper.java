package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.CsStockLevelTwoVO;

import java.util.List;

/**
 * @Description: 二级库仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface StockLevel2InfoMapper extends BaseMapper<CsStockLevelTwoVO> {

    List<CsStockLevelTwoVO> selectStockList();
}
