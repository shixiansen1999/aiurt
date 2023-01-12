package com.aiurt.boot.category.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.constant.CategoryConstant;
import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Api(tags = "资产分类")
@RestController
@RequestMapping("/category/fixedAssetsCategory")
@Slf4j
public class FixedAssetsCategoryController extends BaseController<FixedAssetsCategory, IFixedAssetsCategoryService> {
    @Autowired
    private IFixedAssetsCategoryService fixedAssetsCategoryService;
    @Autowired
    private IFixedAssetsService fixedAssetsService;

    /**
     * 分页列表查询
     *
     * @param fixedAssetsCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "资产分类-分页列表查询")
    @ApiOperation(value = "资产分类-分页列表查询", notes = "资产分类-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FixedAssetsCategoryDTO>> queryPageList(FixedAssetsCategoryDTO fixedAssetsCategory,
                                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest req) {
        Page<FixedAssetsCategoryDTO> pageList = new Page<>(pageNo, pageSize);
        Page<FixedAssetsCategoryDTO> list = fixedAssetsCategoryService.pageList(pageList, fixedAssetsCategory);
        return Result.OK(list);
    }

    /**
     * 资产分类-树形
     *
     * @return
     */
    @AutoLog(value = "资产分类-树形")
    @ApiOperation(value = "资产分类-树形", notes = "资产分类-树形")
    @GetMapping(value = "/getCategoryTreeList")
    public Result<List<FixedAssetsCategoryDTO>> getCategoryTree() {
        List<FixedAssetsCategoryDTO> list = fixedAssetsCategoryService.getCategoryTree();
        return Result.OK(list);
    }

    /**
     * 添加
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-添加")
    @ApiOperation(value = "资产分类-添加", notes = "资产分类-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody FixedAssetsCategory fixedAssetsCategory) {
        fixedAssetsCategoryService.save(fixedAssetsCategory);
        return Result.OK("添加成功！");
    }

    /**
     * 校验分类编码、分类名称
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-校验")
    @ApiOperation(value = "资产分类-校验", notes = "资产分类-校验")
    @PostMapping(value = "/checkCodeName")
    public Result<String> checkCodeName(@RequestBody FixedAssetsCategory fixedAssetsCategory) {
        if (ObjectUtil.isNotEmpty(fixedAssetsCategory.getCategoryCode())) {
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getCategoryCode());
            FixedAssetsCategory category = fixedAssetsCategoryService.getOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(category)) {
                return Result.OK("分类编码已存在");
            }
        }
        if (ObjectUtil.isNotEmpty(fixedAssetsCategory.getCategoryName())) {
            //根节点之间，名称不能重复
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getCategoryName, fixedAssetsCategory.getCategoryName());
            if (fixedAssetsCategory.getPid() == CategoryConstant.PID) {
                queryWrapper.eq(FixedAssetsCategory::getPid, CategoryConstant.PID);
                FixedAssetsCategory category = fixedAssetsCategoryService.getOne(queryWrapper);
                if (ObjectUtil.isNotEmpty(category)) {
                    return Result.OK("一级分类名称不允许重复");
                }
            } else {
                //同根下枝干或同根同枝同叶之间不能重复
                queryWrapper.eq(FixedAssetsCategory::getPid, fixedAssetsCategory.getPid());
                List<FixedAssetsCategory> categoryList = fixedAssetsCategoryService.list(queryWrapper);
                if (CollUtil.isNotEmpty(categoryList)) {
                    categoryList = categoryList.stream().filter(c -> c.getCategoryName().equals(fixedAssetsCategory.getCategoryName())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(categoryList)) {
                        return Result.OK("同级下的分类名称不允许重复");
                    }
                }
            }
        }
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-编辑")
    @ApiOperation(value = "资产分类-编辑", notes = "资产分类-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FixedAssetsCategory fixedAssetsCategory) {
        fixedAssetsCategoryService.updateById(fixedAssetsCategory);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "资产分类-删除")
    @ApiOperation(value = "资产分类-删除", notes = "资产分类-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "code", required = true) String code) {
        List<FixedAssets> list = fixedAssetsService.list(new LambdaQueryWrapper<FixedAssets>().eq(FixedAssets::getCategoryCode, code));
        if (CollUtil.isNotEmpty(list)) {
            return Result.error("分类下有固资不允许删除!");
        }
        fixedAssetsCategoryService.removeById(id);
        return Result.OK("删除成功!");
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "资产分类-批量删除")
//	@ApiOperation(value="资产分类-批量删除", notes="资产分类-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.fixedAssetsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets_category-通过id查询")
//	@ApiOperation(value="fixed_assets_category-通过id查询", notes="fixed_assets_category-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<FixedAssetsCategory> queryById(@RequestParam(name="id",required=true) String id) {
//		FixedAssetsCategory fixedAssetsCategory = fixedAssetsCategoryService.getById(id);
//		if(fixedAssetsCategory==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(fixedAssetsCategory);
//	}

    /**
     * 导出excel
     *
     * @param request
     * @param fixedAssetsCategory
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCategory fixedAssetsCategory) {
        return super.exportXls(request, fixedAssetsCategory, FixedAssetsCategory.class, "fixed_assets_category");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FixedAssetsCategory.class);
    }

}
