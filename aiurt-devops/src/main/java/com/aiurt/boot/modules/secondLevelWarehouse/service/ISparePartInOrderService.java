package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartInVO;

import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartInOrderService extends IService<SparePartInOrder> {

    IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, SparePartInQuery sparePartInQuery);

    List<SparePartInExcel> exportXls(SparePartInQuery sparePartInQuery);
}
