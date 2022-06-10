package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface StockInOrderLevel2Mapper extends BaseMapper<StockInOrderLevel2> {

    IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page,
                                              @Param("stockInOrderLevel2") StockInOrderLevel2 stockInOrderLevel2,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime);

    List<StockInOrderLevel2Excel> selectExcelData(@Param("ids") List<Integer> ids);
}
