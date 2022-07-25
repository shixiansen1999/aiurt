package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockLevel2CheckService extends IService<StockLevel2Check> {
    /**
     * 新增获取提报计划编号
     * @return
     */
    StockLevel2Check getStockCheckCode() throws ParseException;

    /**
     * 二级库盘点-添加
     * @param stockLevel2Check
     */
    void add(StockLevel2Check stockLevel2Check);

    /**
     * 二级库盘点-编辑
     * @param stockLevel2Check
     * @return
     */
    boolean edit(StockLevel2Check stockLevel2Check);

    /**
     * 导出
     * @param ids
     * @param request
     * @param response
     */
    void eqExport(String ids, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取仓库所属机构人员
     * @param warehouseCode
     * @return
     */
    Result getStockOrgUsers(String warehouseCode);

}
