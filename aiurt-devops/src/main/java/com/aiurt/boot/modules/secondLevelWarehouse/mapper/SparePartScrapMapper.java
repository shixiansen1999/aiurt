package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface SparePartScrapMapper extends BaseMapper<SparePartScrap> {

    IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery);

    List<SparePartScrapExcel> exportXls(@Param("sparePartScrapQuery") SparePartScrapQuery sparePartScrapQuery);
}
