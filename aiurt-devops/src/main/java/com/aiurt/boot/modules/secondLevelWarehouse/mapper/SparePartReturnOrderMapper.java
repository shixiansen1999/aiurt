package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartReturnOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartReturnQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartReturnVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
public interface SparePartReturnOrderMapper extends BaseMapper<SparePartReturnOrder> {

    /**
     * 分页查询
     * @param page
     * @param queryWrapper
     * @param sparePartReturnQuery
     * @return
     */
    IPage<SparePartReturnVO> queryPageList(IPage<SparePartReturnVO> page, Wrapper<SparePartReturnVO> queryWrapper, @Param("param") SparePartReturnQuery sparePartReturnQuery);

    /**
     * 导出excel
     * @param sparePartReturnQuery
     * @return
     */
    List<SparePartReturnVO> exportXls(@Param("param") SparePartReturnQuery sparePartReturnQuery);
}
