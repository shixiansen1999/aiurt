package com.aiurt.boot.materials.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="物资分类")
@RestController
@RequestMapping("/emergency/emergencyMaterialsCategory")
@Slf4j
public class EmergencyMaterialsCategoryController extends BaseController<EmergencyMaterialsCategory, IEmergencyMaterialsCategoryService> {
	@Autowired
	private IEmergencyMaterialsCategoryService emergencyMaterialsCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterialsCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "物资分类-分页列表查询")
	@ApiOperation(value="物资分类-分页列表查询", notes="物资分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterialsCategory>> queryPageList(EmergencyMaterialsCategory emergencyMaterialsCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		emergencyMaterialsCategory.setDelFlag(0);
		QueryWrapper<EmergencyMaterialsCategory> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterialsCategory, req.getParameterMap());
		Page<EmergencyMaterialsCategory> page = new Page<EmergencyMaterialsCategory>(pageNo, pageSize);
		IPage<EmergencyMaterialsCategory> pageList = emergencyMaterialsCategoryService.page(page, queryWrapper);
		pageList.getRecords().forEach(e->{
			if (StrUtil.isNotBlank(e.getPid()) && e.getPid().equals("0")==false){
				EmergencyMaterialsCategory byId = emergencyMaterialsCategoryService.getById(e.getPid());
                e.setFatherName(byId.getCategoryName());
			}
		});
		return Result.OK(pageList);
	}

	 /**
	  *
	  * @return
	  */

	 @AutoLog(value = "物资分类-物资分类树")
	 @ApiOperation(value="物资分类-物资分类树", notes="物资分类-物资分类树")
	 @GetMapping(value = "/selectTreeList")
	public Result<List<EmergencyMaterialsCategory>> selectTreeList(){
		List<EmergencyMaterialsCategory> emergencyMaterialsCategories = emergencyMaterialsCategoryService.selectTreeList();
		return Result.OK(emergencyMaterialsCategories);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterialsCategory
	 * @return
	 */
	@AutoLog(value = "物资分类-添加")
	@ApiOperation(value="物资分类-添加", notes="物资分类-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterialsCategory emergencyMaterialsCategory) {
		emergencyMaterialsCategoryService.save(emergencyMaterialsCategory);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterialsCategory
	 * @return
	 */
	@AutoLog(value = "物资分类-编辑")
	@ApiOperation(value="物资分类-编辑", notes="物资分类-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyMaterialsCategory emergencyMaterialsCategory) {
		emergencyMaterialsCategoryService.updateById(emergencyMaterialsCategory);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资分类-通过id删除")
	@ApiOperation(value="物资分类-通过id删除", notes="物资分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsCategory emergencyMaterialsCategory = new EmergencyMaterialsCategory();
		if (StrUtil.isNotBlank(id)){
			emergencyMaterialsCategory.setId(id);
			emergencyMaterialsCategory.setDelFlag(1);
		}else {
			return Result.OK("删除失败，id为空或不存在!");
		}
		emergencyMaterialsCategoryService.updateById(emergencyMaterialsCategory);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物资分类-批量删除")
	@ApiOperation(value="物资分类-批量删除", notes="物资分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资分类-通过id查询")
	@ApiOperation(value="物资分类-通过id查询", notes="物资分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterialsCategory> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsCategory emergencyMaterialsCategory = emergencyMaterialsCategoryService.getById(id);
		if(emergencyMaterialsCategory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterialsCategory);
	}


	 /**
	  * 通过code查询
	  *
	  * @param code
	  * @return
	  */
	 @AutoLog(value = "物资分类-通过code查询")
	 @ApiOperation(value="物资分类-通过code查询", notes="物资分类-通过code查询")
	 @GetMapping(value = "/queryByCode")
	 public Result<?> getCode(@RequestParam(name="code",required=true) String code){
		LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,code);
		EmergencyMaterialsCategory one = emergencyMaterialsCategoryService.getOne(queryWrapper, false);
		if (StrUtil.isNotBlank(one.getCategoryCode())){
			return Result.OK("分类编码已存在！");
		}if (StrUtil.isNotBlank(one.getCategoryName())){
			 return Result.OK("分类名称已存在！");
		 }
		return Result.OK("校验成功，请继续！");
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterialsCategory
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsCategory emergencyMaterialsCategory) {
        return super.exportXls(request, emergencyMaterialsCategory, EmergencyMaterialsCategory.class, "emergency_materials_category");
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
        return super.importExcel(request, response, EmergencyMaterialsCategory.class);
    }

}
