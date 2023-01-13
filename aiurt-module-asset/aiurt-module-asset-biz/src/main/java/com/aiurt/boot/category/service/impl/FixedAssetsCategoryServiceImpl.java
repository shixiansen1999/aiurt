package com.aiurt.boot.category.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.category.constant.CategoryConstant;
import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.dto.FixedAssetsCategoryImport;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.mapper.FixedAssetsCategoryMapper;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.common.constant.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
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
