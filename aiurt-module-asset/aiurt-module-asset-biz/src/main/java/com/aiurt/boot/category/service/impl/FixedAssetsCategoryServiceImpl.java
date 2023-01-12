package com.aiurt.boot.category.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.category.constant.CategoryConstant;
import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.mapper.FixedAssetsCategoryMapper;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsCategoryServiceImpl extends ServiceImpl<FixedAssetsCategoryMapper, FixedAssetsCategory> implements IFixedAssetsCategoryService {
    @Autowired
    private FixedAssetsCategoryMapper categoryMapper;

    @Override
    public Page<FixedAssetsCategoryDTO> pageList(Page<FixedAssetsCategoryDTO> pageList, FixedAssetsCategoryDTO fixedAssetsCategory) {
        List<FixedAssetsCategoryDTO> list = categoryMapper.pageList(pageList, fixedAssetsCategory);
        for (FixedAssetsCategoryDTO categoryDTO : list) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getId,categoryDTO.getPid());
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
            if(ObjectUtil.isNotEmpty(category)){
                categoryDTO.setPidName(category.getCategoryName());
            }
        }
        return pageList.setRecords(list);
    }

    @Override
    public List<FixedAssetsCategoryDTO> getCategoryTree() {
        List<FixedAssetsCategoryDTO> list = categoryMapper.getList();
        List<FixedAssetsCategoryDTO> categoryTree = new ArrayList<>();
        //构建树形
        if (CollUtil.isNotEmpty(list)) {
            List<FixedAssetsCategoryDTO> parentList = list.stream().filter(c -> c.getPid().equals(CategoryConstant.PID)).collect(Collectors.toList());
            for (FixedAssetsCategoryDTO parentCategory : parentList) {
                FixedAssetsCategoryDTO categoryDTO = buildChildTree(list, parentCategory);
                categoryTree.add(categoryDTO);
            }
        }
        return categoryTree;
    }

    private FixedAssetsCategoryDTO buildChildTree(List<FixedAssetsCategoryDTO> list, FixedAssetsCategoryDTO parentCategory) {
        List<FixedAssetsCategoryDTO> childList = new ArrayList<>();
        for (FixedAssetsCategoryDTO dto : list) {
            if (parentCategory.getId().equals(dto.getPid())) {
                dto.setPidName(parentCategory.getCategoryName());
                childList.add(dto);
                buildChildTree(list, dto);
            }
        }
        parentCategory.setChildren(childList);
        return parentCategory;
    }
}
