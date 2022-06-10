package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartLend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;

import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface ISparePartLendService extends IService<SparePartLend> {

    IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page, SparePartLendQuery sparePartLendQuery);

    Result<?> addLend(Result<?> result, SparePartLend sparePartLend);

    boolean returnMaterial(SparePartLend sparePartLendEntity, Integer returnNum);

    List<SparePartLendExcel> exportXls(SparePartLendQuery sparePartLendQuery);
}
