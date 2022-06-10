package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface StockInOrderLevel2Mapper extends BaseMapper<StockInOrderLevel2> {

    /**
     * 分页查询二级库入库单
     * @param page
     * @param stockInOrderLevel2
     * @param startTime 入库时间范围开始时间
     * @param endTime 入库时间范围结束时间
     * @return
     */
    IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page,
                                              @Param("stockInOrderLevel2") StockInOrderLevel2 stockInOrderLevel2,
                                              @Param("startTime")String startTime,
                                              @Param("endTime")String endTime);

    /**
     * 查询二级库入库导出excel所需数据
     * @param selections 选中的行ids
     * @return
     */
    List<StockInOrderLevel2Excel> selectExcelData(@Param("selections") List<Integer> selections);
}
