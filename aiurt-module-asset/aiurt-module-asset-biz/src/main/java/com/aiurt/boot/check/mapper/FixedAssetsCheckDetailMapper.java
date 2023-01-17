package com.aiurt.boot.check.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: fixed_assets_check_detail
 * @Author: aiurt
 * @Date: 2023-01-17
 * @Version: V1.0
 */
public interface FixedAssetsCheckDetailMapper extends BaseMapper<FixedAssetsCheckDetail> {
    /**
     * 固定资产盘点任务详情-变更明细分页列表查询
     *
     * @param categoryCodes
     * @param orgCodes
     * @return
     */
    Page<FixedAssetsCheckDetail> queryPageList(@Param("page") Page<FixedAssetsCheckDetail> page,
                                               @Param("categoryCodes") List<String> categoryCodes,
                                               @Param("orgCodes") List<String> orgCodes);
}
