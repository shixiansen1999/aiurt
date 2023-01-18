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
import org.jeecg.common.api.vo.Result;
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
        if (ObjectUtil.isNotEmpty(fixedAssetsCategory.getPid())) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<FixedAssetsCategory> list = categoryMapper.selectList(queryWrapper);
            FixedAssetsCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getTreeCategoryCode()));
            List<FixedAssetsCategory> categoryList = new ArrayList<>();
            List<FixedAssetsCategory> allChildren = treeMenuList(list, category, categoryList);
            allChildren.add(category);
            List<String> allChildrenCode = allChildren.stream().map(FixedAssetsCategory::getCategoryCode).collect(Collectors.toList());
            fixedAssetsCategory.setTreeCode(allChildrenCode);
        }

        List<FixedAssetsCategoryDTO> list = categoryMapper.pageList(pageList, fixedAssetsCategory);
        for (FixedAssetsCategoryDTO categoryDTO : list) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getId, categoryDTO.getPid());
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(category)) {
                categoryDTO.setPidName(category.getCategoryName());
                categoryDTO.setParentCode(category.getCategoryCode());
            } else {
                categoryDTO.setParentCode(categoryDTO.getCategoryCode());
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
            for (FixedAssetsCategoryDTO categoryDTO : list) {
                LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(FixedAssetsCategory::getId, categoryDTO.getPid());
                queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
                FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
                if (ObjectUtil.isNotEmpty(category)) {
                    categoryDTO.setPidName(category.getCategoryName());
                    categoryDTO.setParentCode(category.getCategoryCode());
                } else {
                    categoryDTO.setParentCode(categoryDTO.getCategoryCode());
                }
            }
            List<FixedAssetsCategoryDTO> parentList = list.stream().filter(c -> c.getPid().equals(CategoryConstant.PID)).collect(Collectors.toList());
            for (FixedAssetsCategoryDTO parentCategory : parentList) {
                FixedAssetsCategoryDTO categoryDTO = buildChildTree(list, parentCategory);
                categoryTree.add(categoryDTO);
            }
        }
        return categoryTree;
    }

    /**
     * 递归构建子节点
     *
     * @param list
     * @param parentCategory
     * @return
     */
    private FixedAssetsCategoryDTO buildChildTree(List<FixedAssetsCategoryDTO> list, FixedAssetsCategoryDTO parentCategory) {
        List<FixedAssetsCategoryDTO> childList = new ArrayList<>();
        for (FixedAssetsCategoryDTO dto : list) {
            if (parentCategory.getId().equals(dto.getPid())) {
                childList.add(dto);
                buildChildTree(list, dto);
            }
        }
        parentCategory.setChildren(childList);
        return parentCategory;
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param list
     * @param assetsCategory
     * @param allChildren
     * @return
     */
    public static List<FixedAssetsCategory> treeMenuList(List<FixedAssetsCategory> list, FixedAssetsCategory assetsCategory, List<FixedAssetsCategory> allChildren) {

        for (FixedAssetsCategory category : list) {
            //遍历出父id等于参数的id，add进子节点集合
            if (category.getPid().equals(assetsCategory.getId())) {
                //递归遍历下一级
                treeMenuList(list, category, allChildren);
                allChildren.add(category);
            }
        }
        return allChildren;
    }

    @Override
    public List<FixedAssetsCategoryDTO> getCategoryList(FixedAssetsCategoryDTO categoryDTO) {
        List<FixedAssetsCategoryDTO> list = categoryMapper.getList(categoryDTO);
        list = list.stream().sorted(Comparator.comparing(FixedAssetsCategoryDTO::getCreateTime).reversed()).collect(Collectors.toList());
        for (FixedAssetsCategoryDTO dto : list) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getId, dto.getPid());
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(category)) {
                dto.setPidName(category.getCategoryName());
            }
        }
        return list;
    }

    @Override
    public Result<String> checkCodeName(FixedAssetsCategoryDTO fixedAssetsCategory) {
        if (ObjectUtil.isNotEmpty(fixedAssetsCategory.getCategoryCode()) && ObjectUtil.isEmpty(fixedAssetsCategory.getId())) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getCategoryCode());
            FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(category)) {
                return Result.error("分类编码已存在");
            }
        }
        if (ObjectUtil.isNotEmpty(fixedAssetsCategory.getCategoryName())) {
            //第一级(parentCode:添加传0，编辑传自己)
            FixedAssetsCategory parentCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getParentCode()));
            if (ObjectUtil.isEmpty(parentCategory)||CategoryConstant.PID.equals(parentCategory.getPid())) {
                //1.根节点之间不能重复
                //添加-根节点之间，名称不能重复
                if (ObjectUtil.isEmpty(fixedAssetsCategory.getId())) {
                    LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(FixedAssetsCategory::getCategoryName, fixedAssetsCategory.getCategoryName());
                    queryWrapper.eq(FixedAssetsCategory::getPid, CategoryConstant.PID);
                    FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
                    if (ObjectUtil.isNotEmpty(category)) {
                        return Result.error("一级分类名称不允许重复");
                    }
                }
                //编辑-根节点之间，名称不能重复(自己排查外)
                else {
                    FixedAssetsCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryName, fixedAssetsCategory.getCategoryName()).
                            ne(FixedAssetsCategory::getId, fixedAssetsCategory.getId()).eq(FixedAssetsCategory::getPid, CategoryConstant.PID));
                    if (ObjectUtil.isNotEmpty(category)) {
                        return Result.error("一级分类名称不允许重复");
                    }
                    //编辑-根节点修改名称，不能与底下的所有子级相同
                    FixedAssetsCategory myCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getId, fixedAssetsCategory.getId()));
                    List<FixedAssetsCategory> list = categoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0));
                    List<FixedAssetsCategory> allChildren = treeMenuList(list, myCategory, new ArrayList<FixedAssetsCategory>());
                    if (CollUtil.isNotEmpty(allChildren)) {
                        List<FixedAssetsCategory> collect = allChildren.stream().filter(a -> a.getCategoryName().equals(fixedAssetsCategory.getCategoryName())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(collect)) {
                            return Result.error("同根同枝同叶之间不能重复");
                        }
                    }
                }
            }
            //不是第一级
            else {
                //同根下限制
                //查询上一级
                LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getParentCode());
                FixedAssetsCategory category = categoryMapper.selectOne(queryWrapper);
                //查询上一级的下一级所有的数据
                List<FixedAssetsCategory> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, category.getId()));
                //2.同根下枝干之间不能重复(二三级之间)
                //添加-校验是否有重名
                if (ObjectUtil.isEmpty(fixedAssetsCategory.getId())) {
                    categoryList = categoryList.stream().filter(c -> c.getCategoryName().equals(fixedAssetsCategory.getCategoryName())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(categoryList)) {
                        return Result.error("同根下枝干之间不能重复");
                    }
                }
                //编辑-校验是否有重名，自己排除外
                else {
                    categoryList = categoryList.stream().filter(c -> c.getCategoryName().equals(fixedAssetsCategory.getCategoryName()) && !c.getId().equals(fixedAssetsCategory.getId())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(categoryList)) {
                        return Result.error("同根下枝干之间不能重复");
                    }
                }
                //3.同根同枝同叶之间不能重复（A-A-A）
                //添加
                if (ObjectUtil.isEmpty(fixedAssetsCategory.getId())) {
                    //查询自己及上一级是否同名
                    if (fixedAssetsCategory.getCategoryName().equals(category.getCategoryName())) {
                        return Result.error("同根同枝同叶之间不能重复");
                    }
                    if (CategoryConstant.PID.equals(category.getPid())) {
                        //自己与上上级是否同名
                        FixedAssetsCategory firstCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getId, category.getPid()));
                        if (ObjectUtil.isNotEmpty(firstCategory)) {
                            return Result.error("同根同枝同叶之间不能重复");
                        }
                    }
                } else {
                    //编辑
                    //二三级-自己与上一级是否同名
                    if (fixedAssetsCategory.getCategoryName().equals(category.getCategoryName()) && !fixedAssetsCategory.getId().equals(category.getId())) {
                        return Result.error("同根同枝同叶之间不能重复");
                    }
                    if (CategoryConstant.PID.equals(category.getPid())) {
                        //二级编辑-自己与下级是否重名
                        List<FixedAssetsCategory> levelThreeList = categoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, fixedAssetsCategory.getId()));
                        if (CollUtil.isNotEmpty(levelThreeList)) {
                            List<FixedAssetsCategory> sonCategoryList = levelThreeList.stream().filter(l -> l.getCategoryName().equals(fixedAssetsCategory.getCategoryName())).collect(Collectors.toList());
                            if (CollUtil.isNotEmpty(sonCategoryList)) {
                                return Result.error("同根同枝同叶之间不能重复");
                            }
                        }
                    } else {
                        //三级编辑-自己与上上级是否同名
                        FixedAssetsCategory firstCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getId, category.getPid()).ne(FixedAssetsCategory::getId, fixedAssetsCategory.getId()));
                        if (ObjectUtil.isNotEmpty(firstCategory)) {
                            if (fixedAssetsCategory.getCategoryName().equals(firstCategory.getCategoryName())) {
                                return Result.error("同根同枝同叶之间不能重复");
                            }
                        }
                    }
                }
            }
        }
        return Result.OK();
    }
}
