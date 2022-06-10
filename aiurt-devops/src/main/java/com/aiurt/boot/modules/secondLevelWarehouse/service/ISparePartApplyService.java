package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.swsc.copsms.common.util.PageLimitUtil;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.AddApplyDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockApplyExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockOutDTO;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartApplyService extends IService<SparePartApply> {

    void addApply(AddApplyDTO addApplyDTO);

    PageLimitUtil<SparePartApply> queryPageList(SparePartApply sparePartApply, Integer pageNo, Integer pageSize,
                                                String startTime, String endTime, HttpServletRequest req) throws ParseException;

    List<StockApplyExcel> exportXls(List<Integer> ids);

    Boolean stockOutConfirm(StockOutDTO stockOutDTOList);
}
