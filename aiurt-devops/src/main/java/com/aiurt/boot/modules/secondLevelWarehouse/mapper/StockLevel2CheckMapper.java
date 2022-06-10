package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface StockLevel2CheckMapper extends BaseMapper<StockLevel2Check> {

    IPage<Stock2CheckVO> queryPageList(IPage<Stock2CheckVO> page, String stockCheckCode,
                                       String warehouseCode, String startTime, String endTime);

    List<StockLevel2CheckExcel> exportXls(@Param("ids") List<Integer> ids);
}
