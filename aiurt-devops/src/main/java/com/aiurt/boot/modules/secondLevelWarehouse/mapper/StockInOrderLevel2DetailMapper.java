package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInDetailVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 二级入库单详细信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface StockInOrderLevel2DetailMapper extends BaseMapper<StockInOrderLevel2Detail> {

    /**
     * 根据入库单号查询入库备件列表
     * @param page
     * @param applyCode
     * @return
     */
    IPage<StockInDetailVO> selectPageList(IPage<StockInDetailVO> page,@Param("applyCode") String applyCode);

}
