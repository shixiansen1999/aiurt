package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartScrapVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;

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
