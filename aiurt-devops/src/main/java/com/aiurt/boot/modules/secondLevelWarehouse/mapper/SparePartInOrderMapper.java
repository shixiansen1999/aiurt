package com.aiurt.boot.modules.secondLevelWarehouse.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartInVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartInOrderMapper extends BaseMapper<SparePartInOrder> {

    IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, @Param("sparePartInQuery") SparePartInQuery sparePartInQuery);

    List<SparePartInExcel> exportXls(@Param("sparePartInQuery") SparePartInQuery sparePartInQuery);
}
