package com.aiurt.boot.category.service;

import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface IFixedAssetsCategoryService extends IService<FixedAssetsCategory> {

    /**
     * 资产分类-列表分页查询
     *
     * @param pageList
     * @param fixedAssetsCategory
     * @return
     */
    Page<FixedAssetsCategoryDTO> pageList(Page<FixedAssetsCategoryDTO> pageList, FixedAssetsCategoryDTO fixedAssetsCategory);

    /**
     * 资产分类-树形
     * @return
     */
    List<FixedAssetsCategoryDTO> getCategoryTree();
}
