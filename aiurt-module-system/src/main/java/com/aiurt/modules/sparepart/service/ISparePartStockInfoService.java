package com.aiurt.modules.sparepart.service;


import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
public interface ISparePartStockInfoService extends IService<SparePartStockInfo> {
    /**
     * 添加
     *
     * @param sparePartStockInfo
     * @return
     */
    Result<?> add(SparePartStockInfo sparePartStockInfo);
    /**
     * 编辑
     *
     * @param sparePartStockInfo
     * @return
     */
    Result<?> update(SparePartStockInfo sparePartStockInfo);

    /**
     * 根据用户名查询管理的仓库
     * @param userName
     * @return
     */
    SparePartStockInfo getSparePartStockInfoByUserName(String userName);

    /**
     * 系统管理-基础数据-备件仓库-导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);
}
