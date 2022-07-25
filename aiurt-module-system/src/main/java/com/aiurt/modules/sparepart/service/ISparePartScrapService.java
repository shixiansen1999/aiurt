package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapQuery;


import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ISparePartScrapService extends IService<SparePartScrap> {



    List<SparePartScrapExcel> exportXls(SparePartScrapQuery sparePartScrapQuery);
}
