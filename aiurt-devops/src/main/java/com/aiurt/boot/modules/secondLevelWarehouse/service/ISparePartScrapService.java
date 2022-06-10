package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;

import java.util.List;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ISparePartScrapService extends IService<SparePartScrap> {

    IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery);

    List<SparePartScrapExcel> exportXls(SparePartScrapQuery sparePartScrapQuery);
}
