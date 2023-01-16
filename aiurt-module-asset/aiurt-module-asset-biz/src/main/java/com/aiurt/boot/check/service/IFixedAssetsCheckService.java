package com.aiurt.boot.check.service;

import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.check.dto.AssetsResultDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
public interface IFixedAssetsCheckService extends IService<FixedAssetsCheck> {
    /**
     * 分页查询
     * @param page
     * @param fixedAssetsCheck
     * @return
     */
    IPage<FixedAssetsCheck> queryPageList(Page<FixedAssetsCheck> page, FixedAssetsCheck fixedAssetsCheck);

    /**
     * 查询盘点数据
     * @param orgCodes
     * @param categoryCodes
     * @return
     */
    List<FixedAssets> queryInventoryResults(String orgCodes, String categoryCodes,String code);

    /**
     * 查询分类下拉框
     * @param orgCodes
     * @return
     */
    List<FixedAssetsCategory> queryBySpinner(String orgCodes);

    /**
     * 点击修状态接口
     * @param id
     * @param status
     * @param num
     */
    void updateStatus(String id, Integer status, Integer num);

    /**
     * 保存接口
     * @param fixedAssetsCheck
     */
    void addInventoryResults(FixedAssetsCheck fixedAssetsCheck);

    /**
     * 提交接口
     * @param fixedAssetsCheck
     */
    void addInventoryResultsBySubmit(FixedAssetsCheck fixedAssetsCheck);

    /**
     * 盘点管理-下发接口
     *
     * @param id
     */
    void issued(String id);

    /**
     * 固定资产盘点管理-更新盘点结果数据记录(保存/提交)
     *
     * @param assetsResultDTO
     * @return
     */
    String startProcess(AssetsResultDTO assetsResultDTO);
}
