package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockSubmitPlanService extends IService<StockSubmitPlan> {
    /**
     * 新增获取提报计划编号
     * @return
     * @throws ParseException
     */
    StockSubmitPlan getSubmitPlanCode() throws ParseException;

    /**
     * 物资提报计划-添加
     * @param stockSubmitPlan
     */
    void add(StockSubmitPlan stockSubmitPlan);

    /**
     * 物资提报计划-编辑
     * @param stockSubmitPlan
     * @return
     */
    boolean edit(StockSubmitPlan stockSubmitPlan);

    /**
     * 导出
     * @param ids
     * @param request
     * @param response
     */
    void eqExport (String ids,StockSubmitPlan stockSubmitPlan, HttpServletRequest request, HttpServletResponse response);

    /**
     * 提报计划导入
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcel(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 获取已有数据的部门下拉
     * @return
     */
    List<StockSubmitPlan> getOrgSelect();
}
