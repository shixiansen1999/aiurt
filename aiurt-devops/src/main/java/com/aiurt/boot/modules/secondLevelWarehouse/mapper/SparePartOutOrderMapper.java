package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartOutOrderMapper extends BaseMapper<SparePartOutOrder> {

    IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery);

    List<SparePartOutExcel>  exportXls(@Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);
}
