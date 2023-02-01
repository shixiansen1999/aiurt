package com.aiurt.boot.asset.service;

import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface IFixedAssetsService extends IService<FixedAssets> {
    /**
     * 固定资产-分页列表查询
     *
     * @param pageList
     * @param fixedAssetsDTO
     * @return
     */
    Page<FixedAssetsDTO> pageList(Page<FixedAssetsDTO> pageList, FixedAssetsDTO fixedAssetsDTO);

    /**
     * 固定资产-详情
     *
     * @param code
     * @return
     */
    Result<FixedAssetsDTO> detail(String code);

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出数据
     * @param request
     * @param fixedAssetsDTO
     * @return
     */
    void exportFixedAssetsXls(HttpServletRequest request, HttpServletResponse response,FixedAssetsDTO fixedAssetsDTO);
}
