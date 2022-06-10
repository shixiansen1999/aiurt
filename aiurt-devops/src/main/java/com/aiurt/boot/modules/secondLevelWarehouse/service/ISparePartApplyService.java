package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApply;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.*;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartApplyVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartApplyService extends IService<SparePartApply> {

    /**
     * 添加申领单
     * @param addApplyDTO
     * @param req
     */
    boolean addApply(AddApplyDTO addApplyDTO,HttpServletRequest req);

    /**
     * 提交申领单
     * @param ids
     * @param req
     * @return
     */
    Result<?> submitFormByIds(String ids, HttpServletRequest req);

    /**
     * 备件申领-分页列表查询
     * @param page
     * @param sparePartQuery
     * @return
     */
    IPage<SparePartApplyVO> queryPageList(Page<SparePartApplyVO> page, SparePartQuery sparePartQuery) ;

    /**
     * 二级库出库列表-分页列表查询
     * @param page
     * @param sparePartQuery
     * @return
     */
    IPage<SparePartApplyVO> queryPageListLevel2(Page<SparePartApplyVO> page, SparePartQuery sparePartQuery) ;


    /**
     * 导出excel
     * @param sparePartQuery
     * @return
     */
    List<StockApplyExcel> exportXls(SparePartQuery sparePartQuery);


    /**
     * 出库确认
     * @param stockOutDTOList
     * @param req
     * @return
     */
    void stockOutConfirm(StockOutDTO stockOutDTOList,HttpServletRequest req);

    /**
     * 导出excel
     * @param selections 选中行的ids
     * @return
     */
    List<StockOutExcel> exportStock2Xls(List<Integer> selections);

    /**
     * 编辑申领单
     *
     * @param editApplyDTO
     * @param req
     */
    void editApply(EditApplyDTO editApplyDTO, HttpServletRequest req);


}
