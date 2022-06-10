package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.result.FaultSparePartResult;
import com.aiurt.boot.common.result.SparePartResult;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface ISparePartOutOrderService extends IService<SparePartOutOrder> {

    /**
     * 分页查询
     * @param page
     * @param sparePartLendQuery
     * @return
     */
    IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery);

    /**
     * 备件出库表-添加
     * @param result
     * @param sparePartOutOrder
     * @param req
     * @return
     */
    Result<?> addOutOrder(Result<?> result, SparePartOutOrder sparePartOutOrder, HttpServletRequest req);

    /**
     * 备件出库信息导出
     * @param sparePartLendQuery
     * @return
     */
    List<SparePartOutExcel> exportXls(SparePartLendQuery sparePartLendQuery);

    /**
     * 根据故障备件关联表device_change_spare_part的id，
     * 原备件报损，新备件出库
     * @param result
     * @param id device_change_spare_part的id
     * @return
     */
    Result<?> addByFault(Result<?> result, Long id);

//    Result<?> addByFault(Result<?> result, SpareByRepair spareByRepair);

    /**
     *履历-查询故障更换备件信息
     * @param id
     * @return
     */
    Result<List<SparePartResult>> selectFaultChangePart(Long id);

    /**
     * 履历-查询故障信息
     * @param id
     * @return
     */
    Result<List<FaultSparePartResult>> selectFaultDetail(Long id);
}
