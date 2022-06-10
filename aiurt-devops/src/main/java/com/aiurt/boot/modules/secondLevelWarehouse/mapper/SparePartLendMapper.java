package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartLend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartLendMapper extends BaseMapper<SparePartLend> {

    IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page,
                                         @Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);

    List<SparePartLendExcel> queryExportXls(@Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);
}
