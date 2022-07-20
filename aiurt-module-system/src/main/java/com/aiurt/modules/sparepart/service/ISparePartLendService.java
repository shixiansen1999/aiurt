package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartLendVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
