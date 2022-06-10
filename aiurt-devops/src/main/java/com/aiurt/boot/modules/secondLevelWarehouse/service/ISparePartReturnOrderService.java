package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartReturnOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartReturnQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartReturnVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
public interface ISparePartReturnOrderService extends IService<SparePartReturnOrder> {

    /**
     * 分页查询
     * @param page
     * @param wrapper
     * @param sparePartReturnQuery
     * @return
     */
    IPage<SparePartReturnVO> pageList(IPage<SparePartReturnVO> page, Wrapper<SparePartReturnVO> wrapper, SparePartReturnQuery sparePartReturnQuery);

    /**
     * 导出excel
     * @param sparePartReturnQuery
     * @return
     */
    List<SparePartReturnVO> exportXls(SparePartReturnQuery sparePartReturnQuery);

    /**
     * 备件退库表-添加
     * @param result
     * @param sparePartReturnOrder
     * @param request
     * @return
     */
    Result<?> addReturnOrder(Result<?> result, SparePartReturnOrder sparePartReturnOrder, HttpServletRequest request);
}
