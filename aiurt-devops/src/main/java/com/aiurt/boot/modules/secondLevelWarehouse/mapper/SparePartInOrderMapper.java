package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartInVO;

import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartInOrderMapper extends BaseMapper<SparePartInOrder> {

    /**
     * 备件入库分页查询
     * @param page
     * @param sparePartInQuery
     * @return
     */
    IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, @Param("sparePartInQuery") SparePartInQuery sparePartInQuery);

    /**
     * 备件入库导出excel所需数据
     * @param sparePartInQuery
     * @return
     */
    List<SparePartInExcel> exportXls(@Param("sparePartInQuery")SparePartInQuery sparePartInQuery);

    /**
     * 通过id确认
     * @param id
     * @return
     */
    int confirm(String id);
}
