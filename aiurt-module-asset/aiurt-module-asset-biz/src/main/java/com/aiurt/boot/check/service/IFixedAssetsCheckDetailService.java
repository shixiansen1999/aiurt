package com.aiurt.boot.check.service;

import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: fixed_assets_check_detail
 * @Author: aiurt
 * @Date: 2023-01-17
 * @Version: V1.0
 */
public interface IFixedAssetsCheckDetailService extends IService<FixedAssetsCheckDetail> {
    /**
     * 固定资产盘点任务详情-变更明细分页列表查询
     *
     * @param page
     * @param id
     * @return
     */
    IPage<FixedAssetsCheckDetail> queryPageList(Page<FixedAssetsCheckDetail> page, String id);
}
