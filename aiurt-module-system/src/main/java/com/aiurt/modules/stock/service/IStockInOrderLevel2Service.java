package com.aiurt.modules.stock.service;

import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.modules.stock.dto.StockInOrderLevel2ExportDTO;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockInOrderLevel2Service extends IService<StockInOrderLevel2> {
    /**
     * 新增获取入库编号
     * @return
     * @throws ParseException
     */
    StockInOrderLevel2 getInOrderCode() throws ParseException;

    /**
     * 二级库入库管理-添加
     * @param stockInOrderLevel2
     */
    void add(StockInOrderLevel2 stockInOrderLevel2);

    /**
     * 二级库入库管理-编辑
     * @param stockInOrderLevel2
     * @return
     */
    boolean edit(StockInOrderLevel2 stockInOrderLevel2);

    /**
     * 提交
     * @param status
     * @param code
     * @param stockInOrderLevel2
     * @return
     * @throws ParseException
     */
    boolean submitInOrderStatus(String status,String code, StockInOrderLevel2 stockInOrderLevel2) throws ParseException;

    /**
     * 导出
     * @param ids
     * @param request
     * @param response
     */
    void eqExport(String ids, HttpServletRequest request, HttpServletResponse response);

    /**
     * 二级库导出
     * @param request
     * @param response
     * @param stockInOrderLevel2ExportDTO
     */
    void exportXls(HttpServletRequest request, HttpServletResponse response, StockInOrderLevel2ExportDTO stockInOrderLevel2ExportDTO);


    /**
     * 二级库入库导入模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXls(HttpServletResponse response) throws IOException;

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取分页数据
     * @param page
     * @param stockInOrderLevel2
     * @return
     */
    IPage<StockInOrderLevel2> pageList(Page<StockInOrderLevel2> page, StockInOrderLevel2 stockInOrderLevel2);
}
