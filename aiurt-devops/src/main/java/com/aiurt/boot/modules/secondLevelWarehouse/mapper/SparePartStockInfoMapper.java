package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStockVO;

import java.util.List;

/**
 * @Description: 备件仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartStockInfoMapper extends BaseMapper<SparePartStockVO> {

    List<SparePartStockVO> queryList();
}
