package com.aiurt.boot.asset.mapper;

import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface FixedAssetsMapper extends BaseMapper<FixedAssets> {

    /**
     * 固定资产-分页列表查询
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<FixedAssetsDTO> pageList(@Param("pageList") Page<FixedAssetsDTO> pageList, @Param("condition") FixedAssetsDTO condition);
}
