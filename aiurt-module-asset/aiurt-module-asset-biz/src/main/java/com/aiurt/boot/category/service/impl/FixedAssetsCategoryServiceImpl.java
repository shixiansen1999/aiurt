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
import java.util.Comparator;
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
        //树形查询:输入pid,获取本身及底下所有子级的code
        if(ObjectUtil.isNotEmpty(fixedAssetsCategory.getPid())){
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getDelFlag,CommonConstant.DEL_FLAG_0);
            List<FixedAssetsCategory> list = categoryMapper.selectList(queryWrapper);
            FixedAssetsCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getCategoryCode()));
            List<FixedAssetsCategory> allChildren = treeMenuList(list, category, new ArrayList<FixedAssetsCategory>());
            List<String> allChildrenCode = allChildren.stream().map(FixedAssetsCategory::getCategoryCode).collect(Collectors.toList());
            fixedAssetsCategory.setTreeCode(allChildrenCode);
        }

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
        List<FixedAssetsCategoryDTO> list = categoryMapper.getList(new FixedAssetsCategoryDTO());
        List<FixedAssetsCategoryDTO> categoryTree = new ArrayList<>();
        //构建树形
        if (CollUtil.isNotEmpty(list)) {
            List<FixedAssetsCategoryDTO> parentList = list.stream().filter(c -> c.getPid().equals(CategoryConstant.PID)).collect(Collectors.toList());
            for (FixedAssetsCategoryDTO parentCategory : parentList) {
                Integer level = 1;
                parentCategory.setLevel(level);
                FixedAssetsCategoryDTO categoryDTO = buildChildTree(level,list, parentCategory);
                categoryTree.add(categoryDTO);
            }
        }
        return categoryTree;
    }

    /**
     * 递归构建子节点
     * @param level
     * @param list
     * @param parentCategory
     * @return
     */
    private FixedAssetsCategoryDTO buildChildTree(Integer level,List<FixedAssetsCategoryDTO> list, FixedAssetsCategoryDTO parentCategory) {
        List<FixedAssetsCategoryDTO> childList = new ArrayList<>();
        for (FixedAssetsCategoryDTO dto : list) {
            if (parentCategory.getId().equals(dto.getPid())) {
                dto.setPidName(parentCategory.getCategoryName());
                 ++level;
                dto.setLevel(level);
                childList.add(dto);
                buildChildTree(level,list, dto);
            }
        }
        parentCategory.setChildren(childList);
        return parentCategory;
    }
    /**
     * 获取某个父节点下面的所有子节点
     * @param list
     * @param assetsCategory
     * @param allChildren
     * @return
     */
    public static List<FixedAssetsCategory> treeMenuList(List<FixedAssetsCategory> list, FixedAssetsCategory assetsCategory,List<FixedAssetsCategory> allChildren) {

        for (FixedAssetsCategory category : list) {
            //遍历出父id等于参数的id，add进子节点集合
            if (category.getPid() == assetsCategory.getId()) {
                //递归遍历下一级
                treeMenuList(list, category,allChildren);
                allChildren.add(category);
            }
        }
        return allChildren;
    }
    @Override
    public List<FixedAssetsCategoryDTO> getCategoryList(FixedAssetsCategoryDTO categoryDTO) {
        List<FixedAssetsCategoryDTO> list = categoryMapper.getList(categoryDTO);
        list =  list.stream().sorted(Comparator.comparing(FixedAssetsCategoryDTO::getCreateTime).reversed()).collect(Collectors.toList());
        for (FixedAssetsCategoryDTO dto : list) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getId,dto.getPid());
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
            if(ObjectUtil.isNotEmpty(category)){
                dto.setPidName(category.getCategoryName());
            }
        }
       return  list;
    }
}
