package com.aiurt.boot.category.mapper;

import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface FixedAssetsCategoryMapper extends BaseMapper<FixedAssetsCategory> {

    /**
     * 资产分类-列表分页查询
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<FixedAssetsCategoryDTO> pageList(@Param("pageList") Page<FixedAssetsCategoryDTO> pageList, @Param("condition") FixedAssetsCategoryDTO condition);

    /**
     * 资产分类-查询全部的数据
     * @return
     */
    List<FixedAssetsCategoryDTO> getList( @Param("condition") FixedAssetsCategoryDTO condition);
    /**
     * 通过名称和父id获取资产分类信息
     *
     * @param categoryName
     * @param pid
     * @return
     */
    FixedAssetsCategory getAssetsCategory(@Param("categoryName") String categoryName, @Param("pid") String pid);
}
