package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_apply
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
public interface ISparePartApplyService extends IService<SparePartApply> {
    /**
     * 添加
     *
     * @param sparePartApply
     * @return
     */
    Result<?> add(SparePartApply sparePartApply);
    /**
     * 编辑
     *
     * @param sparePartApply
     * @return
     */
    Result<?> update(SparePartApply sparePartApply);
    /**
     * 提交
     *
     * @param sparePartApply
     * @return
     */
    Result<?> submit(SparePartApply sparePartApply);
    /**
     * 生成申领单号
     * @return
     */
    String getCode();
    /**
     * 生成出库单号
     * @return
     */
    String getStockOutCode();
    /**
     * 导出excel
     *
     * @param ids
     */
    List<StockApplyExcel> exportXls(List<Integer> ids);
}
