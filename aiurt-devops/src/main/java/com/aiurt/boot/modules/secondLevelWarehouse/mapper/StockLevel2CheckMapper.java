package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface StockLevel2CheckMapper extends BaseMapper<StockLevel2Check> {

    /**
     * 二级库盘点列表-分页查询
     * @param page
     * @param stockCheckCode 盘点编号
     * @param warehouseCode 仓库编号
     * @param startTime 盘点时间范围开始时间
     * @param endTime 盘点时间范围结束时间
     * @return
     */
    IPage<Stock2CheckVO> queryPageList( IPage<Stock2CheckVO> page,String stockCheckCode,
                                       String warehouseCode, String startTime, String endTime);

    /**
     * 二级库盘点导出Excel所需数据
     * @param stockLevel2CheckDTO
     * @return
     */
    List<StockLevel2CheckExcel> exportXls(@Param("param") StockLevel2CheckDTO stockLevel2CheckDTO);
}
