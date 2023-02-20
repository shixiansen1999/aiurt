package com.aiurt.boot.category.service;

import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * @param name
     * @return
     */
    List<FixedAssetsCategoryDTO> getCategoryTree(String name);

    /**
     * 获取分类信息
     *
     * @param categoryDTO
     * @return
     */
    List<FixedAssetsCategoryDTO> getCategoryList(FixedAssetsCategoryDTO categoryDTO);

    /**
     * 资产分类-导入
     *
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 资产分类-校验分类编码
     *
     * @param fixedAssetsCategory
     * @return
     */
    Result<String> checkCode(FixedAssetsCategoryDTO fixedAssetsCategory);

    /**
     * 资产分类-校验分类名称
     *
     * @param fixedAssetsCategory
     * @return
     */
    Result<String> checkName(FixedAssetsCategoryDTO fixedAssetsCategory);
}
