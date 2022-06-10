package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApply;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockApplyExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockOutExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartApplyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartApplyMapper extends BaseMapper<SparePartApply> {

    /**
     * 查询备件申领出excel需要的数据
     * @param sparePartQuery
     * @return
     */
    List<StockApplyExcel> selectExportXls(@Param("sparePartQuery") SparePartQuery sparePartQuery);

    /**
     * 备件出库-分页列表查询
     * @param page
     * @param sparePartQuery
     * @return
     */
    IPage<SparePartApplyVO> queryPageList(Page<SparePartApplyVO> page,
                                          @Param("sparePartQuery") SparePartQuery sparePartQuery);

    /**
     * 为机库入库-分页列表查询
     * @param page
     * @param sparePartQuery
     * @return
     */
    IPage<SparePartApplyVO> queryPageListLevel2(Page<SparePartApplyVO> page,
                                          @Param("sparePartQuery") SparePartQuery sparePartQuery);


    /**
     * 二级库出库导出excel
     * @param selections 选中行的ids
     * @return
     */
    List<StockOutExcel> selectStock2ExportXls(@Param("selections") List<Integer> selections);
}
