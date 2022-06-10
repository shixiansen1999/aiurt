package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;

import java.util.List;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface ISparePartOutOrderService extends IService<SparePartOutOrder> {

    IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery);

    Result<?> addOutOrder(Result<?> result, SparePartOutOrder sparePartOutOrder);

    List<SparePartOutExcel> exportXls(SparePartLendQuery sparePartLendQuery);
}
