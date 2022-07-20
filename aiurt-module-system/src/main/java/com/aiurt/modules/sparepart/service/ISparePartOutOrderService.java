package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.dto.SparePartOutExcel;
import com.aiurt.modules.sparepart.entity.vo.SparePartOutVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
