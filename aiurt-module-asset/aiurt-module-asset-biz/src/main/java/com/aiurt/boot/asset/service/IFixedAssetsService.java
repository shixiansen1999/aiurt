package com.aiurt.boot.asset.service;

import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
}
