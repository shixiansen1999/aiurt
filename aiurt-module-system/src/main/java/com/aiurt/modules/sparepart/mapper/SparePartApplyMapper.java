package com.aiurt.modules.sparepart.mapper;

import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;


/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartApplyMapper extends BaseMapper<SparePartApply> {

    List<StockApplyExcel> selectExportXls(@Param("ids") List<Integer> ids);
}
