package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockApplyExcel;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartApplyMapper extends BaseMapper<SparePartApply> {

    List<StockApplyExcel> selectExportXls(@Param("ids") List<Integer> ids);
}
