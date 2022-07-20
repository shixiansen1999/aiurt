package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.dto.SparePartInExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartInQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartInVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;


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
