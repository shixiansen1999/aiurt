package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;
import com.aiurt.common.result.FaultSparePartResult;
import com.aiurt.common.result.SparePartResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartOutOrderMapper extends BaseMapper<SparePartOutOrder> {

    /**
     * 备件出库分页所需数据
     * @param page
     * @param sparePartLendQuery
     * @return
     */
    IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery);

    /**
     * 备件出库导出excel所需数据
     * @param sparePartLendQuery
     * @return
     */
    List<SparePartOutExcel> exportXls(@Param("sparePartLendQuery")SparePartLendQuery sparePartLendQuery);

    /**
     * 备件出库-批量插入
     * @param outOrderList
     * @return
     */
    Integer insertBatchList(@Param("outOrderList") List<SparePartOutOrder> outOrderList);

    /**
     * 履历-查询故障更换备件信息
     * @param id
     * @return
     */
    List<SparePartResult> selectByFaultChangeSparePartId(Long id);

    /**
     * 履历-查询故障信息
     * @param id
     * @return
     */
    List<FaultSparePartResult> getFaultDetail(Long id);
}
