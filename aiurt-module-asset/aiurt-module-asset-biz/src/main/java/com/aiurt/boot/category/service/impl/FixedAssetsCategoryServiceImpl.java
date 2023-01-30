package com.aiurt.boot.category.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

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
            if (ObjectUtil.isEmpty(parentCategory) || CategoryConstant.PID.equals(parentCategory.getPid())) {
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

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        String tipMessage = null;
        String url = null;
        int errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                tipMessage = "导入失败，文件类型不对。";
                return imporReturnRes(errorLines, successLines, tipMessage, false, null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<FixedAssetsCategory> categoryList = new ArrayList<>();
                List<FixedAssetsCategoryImport> list = ExcelImportUtil.importExcel(file.getInputStream(), FixedAssetsCategoryImport.class, params);
                list = list.stream().filter(l -> l.getPidName() != null || l.getCategoryCode() != null || l.getCategoryName() != null || l.getRemark() != null).collect(Collectors.toList());
                if (CollUtil.isEmpty(list)) {
                    tipMessage = "导入失败，该文件为空。";
                    return imporReturnRes(errorLines, successLines, tipMessage, false, null);
                }
                //拼接编码
                splicingCode(list);
                for (FixedAssetsCategoryImport model : list) {
                    if (ObjectUtil.isNotEmpty(model)) {
                        FixedAssetsCategory category = new FixedAssetsCategory();
                        StringBuilder stringBuilder = new StringBuilder();
                        //校验信息
                        examine(model, category, stringBuilder, list);
                        if (stringBuilder.length() > 0) {
                            errorLines++;
                        }
                        categoryList.add(category);
                    }
                }
                if (errorLines > 0) {
                    //错误报告下载
                    return getErrorExcel(errorLines, list, errorMessage, successLines, type, url);
                } else {
                    return imporReturnRes(errorLines, successLines, tipMessage, true, null);
                }

            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return imporReturnRes(errorLines, successLines, tipMessage, true, null);
    }

    private void splicingCode(List<FixedAssetsCategoryImport> list) {
        for (FixedAssetsCategoryImport dto : list) {
            if (ObjectUtil.isNotEmpty(dto.getPidName()) && ObjectUtil.isNotEmpty(dto.getCategoryCode()) && ObjectUtil.isNotEmpty(dto.getCategoryName())) {
                if (!CategoryConstant.PARENT_NAME.equals(dto.getPidName())) {
                    String[] split = dto.getPidName().split("");
                    Integer count = 0;
                    for (String s : split) {
                        if (s.equals("/")) {
                            count++;
                        }
                    }
                    //二级编码拼接
                    if (count == 0) {
                        List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && dto.getPidName().equals(l.getCategoryName())).collect(Collectors.toList());
                        //文件里没有这个父类
                        if (imports.size() == 0) {
                            FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, CategoryConstant.PID).eq(FixedAssetsCategory::getCategoryName, dto.getPidName()));
                            dto.setCategoryCode(assetsCategory == null ? dto.getCategoryCode() : assetsCategory.getCategoryCode() + dto.getCategoryCode());
                        }
                        //文件里有这个父类
                        if (imports.size() == 1) {
                            FixedAssetsCategoryImport categoryImport = imports.get(0);
                            dto.setCategoryCode(categoryImport.getCategoryCode() == null ? dto.getCategoryCode() : categoryImport.getCategoryCode() + dto.getCategoryCode());
                        }
                    }
                    //三级编码拼接
                    if (count == 1) {
                        List<String> depositPositionName = StrUtil.splitTrim(dto.getPidName(), "/");
                        String firstName = depositPositionName.get(0);
                        String secondName = depositPositionName.get(1);
                        List<FixedAssetsCategoryImport> firstImports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && firstName.equals(l.getCategoryName())).collect(Collectors.toList());
                        List<FixedAssetsCategoryImport> secondImports = list.stream().filter(l -> firstName.equals(l.getPidName()) && secondName.equals(l.getCategoryName())).collect(Collectors.toList());

                        //文件里没有这个父类
                        if (firstImports.size() == 0 && secondImports.size() == 0) {
                            FixedAssetsCategory firstCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, CategoryConstant.PID).eq(FixedAssetsCategory::getCategoryName, firstName));
                            if (ObjectUtil.isNotEmpty(firstCategory)) {
                                FixedAssetsCategory secondCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getId, firstCategory.getPid()));
                                dto.setCategoryCode(secondCategory == null ? dto.getCategoryCode() : firstCategory.getCategoryCode() + secondCategory.getCategoryCode() + dto.getCategoryCode());
                            }
                        }
                        //文件里有这个父类
                        if (firstImports.size() == 1 && secondImports.size() == 1) {
                            FixedAssetsCategoryImport firstCategoryImport = firstImports.get(0);
                            FixedAssetsCategoryImport secondCategoryImport = secondImports.get(0);
                            String code = firstCategoryImport.getCategoryCode() == null ? "" : firstCategoryImport.getCategoryCode() + secondCategoryImport.getCategoryCode() == null ? "" : secondCategoryImport.getCategoryCode();
                            dto.setCategoryCode(code == null ? dto.getCategoryCode() : code + dto.getCategoryCode());
                        }
                    }
                }
            }
        }
    }

    private void examine(FixedAssetsCategoryImport model, FixedAssetsCategory category, StringBuilder stringBuilder, List<FixedAssetsCategoryImport> list) {
        //导入：1.导入未存在的一、二、三级 2.导入已经存在的数据的二、三级
        BeanUtils.copyProperties(model, category);
        if (ObjectUtil.isNotEmpty(model.getPidName())) {
            if (CategoryConstant.PARENT_NAME.equals(model.getPidName())) {
                if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
                    model.setIsNotImportParentNode(false);
                    model.setPid("0");
                    model.setLevel("1");
                }
            } else {
                String[] split = model.getPidName().split("");
                Integer count = 0;
                for (String s : split) {
                    if (s.equals("/")) {
                        count++;
                    }
                }
                if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
                    if (count == 1 || count == 0) {
                        //二级节点
                        if (count == 0) {
                            List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && model.getCategoryName().equals(l.getCategoryName())).collect(Collectors.toList());
                            FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, CategoryConstant.PID).eq(FixedAssetsCategory::getCategoryName, model.getCategoryName()));
                            if (CollUtil.isEmpty(imports) && ObjectUtil.isEmpty(assetsCategory)) {
                                stringBuilder.append("上级节点不存在，");
                            } else {
                                if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isEmpty(imports)) {
                                    model.setIsNotImportParentNode(false);
                                    model.setPid(assetsCategory.getId());
                                } else {
                                    model.setIsNotImportParentNode(true);
                                }
                                model.setLevel("2");
                            }//三级节点
                        } else {
                            List<String> depositPositionName = StrUtil.splitTrim(model.getPidName(), "/");
                            String firstName = depositPositionName.get(0);
                            String secondName = depositPositionName.get(1);
                            //todo
                            //一级节点、二级节点是否存在
                            List<FixedAssetsCategoryImport> firstList = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && firstName.equals(l.getCategoryName())).collect(Collectors.toList());
                            List<FixedAssetsCategoryImport> secondList = list.stream().filter(l -> firstName.equals(l.getPidName()) && secondName.equals(l.getCategoryName())).collect(Collectors.toList());
                            //文件不存在该父级
                            if (CollUtil.isEmpty(firstList)) {
                                FixedAssetsCategory firstCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, CategoryConstant.PID).eq(FixedAssetsCategory::getCategoryName, firstName));
                                //数据库不存在该父级
                                if (ObjectUtil.isEmpty(firstCategory)) {
                                    stringBuilder.append("上级节点填写有误，");
                                } else {
                                    FixedAssetsCategory secondCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, firstCategory.getId()).eq(FixedAssetsCategory::getCategoryName, secondName));
                                    if (ObjectUtil.isEmpty(secondCategory)) {
                                        stringBuilder.append("上级节点填写有误，");
                                    } else {
                                        model.setPid(secondCategory.getId());
                                        model.setIsNotImportParentNode(true);
                                        model.setLevel("3");
                                    }
                                }
                            } else {
                                if (CollUtil.isEmpty(secondList)) {
                                    stringBuilder.append("上级节点填写有误，");
                                } else {
                                    model.setIsNotImportParentNode(false);
                                    model.setLevel("3");
                                }
                            }
                        }
                    } else {
                        stringBuilder.append("上级节点填写不规范，");
                    }
                }

            }
        } else {
            stringBuilder.append("上级节点为必填，");
        }
        if (ObjectUtil.isNotEmpty(model.getCategoryCode())) {
            String regular = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(model.getCategoryCode());
            if (!matcher.find()) {
                stringBuilder.append("分类编码必须是数字，");
            } else {
                if (ObjectUtil.isNotEmpty(model.getPidName())) {
                    if (CategoryConstant.PARENT_NAME.equals(model.getPidName())) {
                        FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, CategoryConstant.PID).eq(FixedAssetsCategory::getCategoryCode, model.getCategoryCode()));
                        List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && model.getCategoryCode().equals(l.getCategoryCode()) && !model.equals(l)).collect(Collectors.toList());
                        if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                            stringBuilder.append("分类编码已存在，");
                        }
                        if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isEmpty(imports)) {
                            stringBuilder.append("分类编码已存在，");
                        }
                        if (ObjectUtil.isEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                            stringBuilder.append("文字中存在相同的分类编码，");
                        }
                    } else {

                        if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
                            String[] split = model.getPidName().split("");
                            Integer count = 0;
                            for (String s : split) {
                                if (s.equals("/")) {
                                    count++;
                                }
                            }
                            if (count == 0) {
                                if (model.getCategoryCode().length() == 3) {
                                    //文件里是否有重复
                                    List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> model.getCategoryCode().equals(l.getCategoryCode()) && !model.equals(l)).collect(Collectors.toList());
                                    //数据库是否存在
                                    FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, model.getCategoryCode()));
                                    if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                                        stringBuilder.append("分类编码已存在，");
                                    }
                                    if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isEmpty(imports)) {
                                        stringBuilder.append("分类编码已存在，");
                                    }
                                    if (ObjectUtil.isEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                                        stringBuilder.append("文字中存在相同的分类编码，");
                                    }
                                } else {
                                    stringBuilder.append("二级分类编码为后三位数字，");
                                }
                            }
                            if (count == 1) {
                                if (model.getCategoryCode().length() == 4) {
                                    //文件里是否有重复
                                    List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> model.getCategoryCode().equals(l.getCategoryCode()) && !model.equals(l)).collect(Collectors.toList());
                                    //数据库是否存在
                                    FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, model.getCategoryCode()));
                                    if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                                        stringBuilder.append("分类编码已存在，");
                                    }
                                    if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isEmpty(imports)) {
                                        stringBuilder.append("分类编码已存在，");
                                    }
                                    if (ObjectUtil.isEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                                        stringBuilder.append("文字中存在相同的分类编码，");
                                    }
                                } else {
                                    stringBuilder.append("三级分类编码为后四位数字，");
                                }
                            }

                        }
                    }
                }
            }
        } else {
            stringBuilder.append("分类编码不能为空，");
        }
        if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
            if (ObjectUtil.isNotEmpty(model.getPidName())) {
                //一级
                if (CategoryConstant.PARENT_NAME.equals(model.getPidName())) {
                    //判断文件、数据库是否有同名
                    if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
                        List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && !model.equals(l) && model.getCategoryName().equals(l.getCategoryName())).collect(Collectors.toList());
                        FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryName, model.getCategoryName()).eq(FixedAssetsCategory::getPid, CategoryConstant.PID));
                        if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                            stringBuilder.append("一级分类名称已存在，");
                        }
                        if (ObjectUtil.isNotEmpty(assetsCategory) && CollUtil.isEmpty(imports)) {
                            stringBuilder.append("一级分类名称已存在，");
                        }
                        if (ObjectUtil.isEmpty(assetsCategory) && CollUtil.isNotEmpty(imports)) {
                            stringBuilder.append("文件中已存在相同的一级分类名称，");
                        }
                    }
                } else {
                    if (ObjectUtil.isNotEmpty(model.getCategoryName())) {
                        String[] split = model.getPidName().split("");
                        Integer count = 0;
                        for (String s : split) {
                            if (s.equals("/")) {
                                count++;
                            }
                        }
                        //二级名称
                        if (count == 0) {
                            //先知道是导入的是文件中，还是数据库中
                            if (model.getPidName().equals(model.getCategoryName())) {
                                stringBuilder.append("同根同枝之间名称不能重复，");
                            }
                            List<FixedAssetsCategoryImport> imports = list.stream().filter(l -> CategoryConstant.PARENT_NAME.equals(l.getPidName()) && model.getPidName().equals(l.getCategoryName())).collect(Collectors.toList());
                            if (imports.size() == 0) {
                                FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryName, model.getPidName()).eq(FixedAssetsCategory::getPid, CategoryConstant.PID));
                                if (ObjectUtil.isNotEmpty(assetsCategory)) {
                                    List<FixedAssetsCategory> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, assetsCategory.getId()));
                                    if (CollUtil.isNotEmpty(categoryList)) {
                                        List<FixedAssetsCategory> collect = categoryList.stream().filter(c -> c.getCategoryName().equals(model.getCategoryName())).collect(Collectors.toList());
                                        if (ObjectUtil.isNotEmpty(collect)) {
                                            stringBuilder.append("同根下枝干之间名称不能重复，");
                                        }
                                    }
                                }
                            }
                            if (imports.size() == 1) {
                                List<FixedAssetsCategoryImport> collect = list.stream().filter(c -> c.getPidName().equals(model.getPidName()) && c.getCategoryName().equals(model.getCategoryName()) && !c.equals(model)).collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(collect)) {
                                    stringBuilder.append("同根下枝干之间名称不能重复，");
                                }
                            }
                        }
                        //三级名称
                        if (count == 1) {
                            List<String> depositPositionName = StrUtil.splitTrim(model.getPidName(), "/");
                            String firstName = depositPositionName.get(0);
                            String secondName = depositPositionName.get(1);
                            if (firstName.equals(model.getCategoryName()) || secondName.equals(model.getCategoryName())) {
                                stringBuilder.append("同根同枝同叶之间不能重复，");
                            }
                            //确定是否文件同名、是否数据库同名
                            List<FixedAssetsCategoryImport> collect = list.stream().filter(c -> c.getPidName().equals(model.getPidName()) && c.getCategoryName().equals(model.getCategoryName()) && !c.equals(model)).collect(Collectors.toList());
                            if (CollUtil.isNotEmpty(collect)) {
                                stringBuilder.append("同根同枝同叶之间不能重复，");
                            }
                            FixedAssetsCategory assetsCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryName, firstName).eq(FixedAssetsCategory::getPid, CategoryConstant.PID));
                            if (ObjectUtil.isNotEmpty(assetsCategory)) {
                                FixedAssetsCategory secondCategory = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, assetsCategory.getId()).eq(FixedAssetsCategory::getCategoryName, secondName));
                                if (ObjectUtil.isNotEmpty(secondCategory)) {
                                    List<FixedAssetsCategory> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, secondCategory.getId()));
                                    List<FixedAssetsCategory> thirdCategory = categoryList.stream().filter(c -> c.getCategoryName().equals(model.getCategoryName())).collect(Collectors.toList());
                                    if (ObjectUtil.isNotEmpty(thirdCategory)) {
                                        stringBuilder.append("同根同枝同叶之间不能重复，");
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } else {
            stringBuilder.append("分类名称不能为空，");
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            model.setWrongReason(stringBuilder.toString());
        }
    }

    public static Result<?> imporReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", failReportUrl);
                Result res = Result.ok(result);
                res.setMessage("文件失败，数据有错误。");
                res.setCode(200);
                return res;
            } else {
                //是否成功
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", true);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                Result res = Result.ok(result);
                res.setMessage("文件导入成功！");
                res.setCode(200);
                return res;
            }
        } else {
            JSONObject result = new JSONObject(5);
            result.put("isSucceed", false);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            Result res = Result.ok(result);
            res.setMessage(tipMessage);
            res.setCode(200);
            return res;
        }

    }

    private Result<?> getErrorExcel(int errorLines, List<FixedAssetsCategoryImport> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/fixedAssetsCategoryError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/fixedAssetsCategoryError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<String, Object>(16);
        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FixedAssetsCategoryImport categoryModel = list.get(i);
            Map<String, String> lm = new HashMap<>(16);
            //错误报告获取信息
            lm.put("pidName", categoryModel.getPidName());
            lm.put("categoryCode", categoryModel.getCategoryCode());
            lm.put("categoryName", categoryModel.getCategoryName());
            lm.put("remark", categoryModel.getRemark());
            lm.put("wrongReason", categoryModel.getWrongReason());
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "资产分类信息导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imporReturnRes(errorLines, successLines, null, true, url);
    }

}
