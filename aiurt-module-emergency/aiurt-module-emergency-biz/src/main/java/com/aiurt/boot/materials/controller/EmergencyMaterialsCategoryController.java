package com.aiurt.boot.materials.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
		queryWrapper.eq(StrUtil.isNotBlank(emergencyMaterialsCategory.getCategoryCode()),"category_code",emergencyMaterialsCategory.getCategoryCode());
		Page<EmergencyMaterialsCategory> page = new Page<EmergencyMaterialsCategory>(pageNo, pageSize);
		IPage<EmergencyMaterialsCategory> pageList = emergencyMaterialsCategoryService.page(page, queryWrapper);
		pageList.getRecords().forEach(e->{
			if (StrUtil.isNotBlank(e.getPid()) && e.getPid().equals("0")==false){
				EmergencyMaterialsCategory byId = emergencyMaterialsCategoryService.getById(e.getPid());
                e.setFatherName(byId.getCategoryName());
			}
			//是否有存在子级，有就查询出子级一起返回
			if (StrUtil.isNotBlank(e.getPid()) && e.getPid().equals("0")){
				LambdaQueryWrapper<EmergencyMaterialsCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
				lambdaQueryWrapper.eq(EmergencyMaterialsCategory::getPid, e.getId());
				lambdaQueryWrapper.eq(EmergencyMaterialsCategory::getDelFlag, 0);
				List<EmergencyMaterialsCategory> list = emergencyMaterialsCategoryService.list(lambdaQueryWrapper);
				list.forEach(q->{
					if (StrUtil.isNotBlank(q.getPid()) && q.getPid().equals("0")==false){
						EmergencyMaterialsCategory byId = emergencyMaterialsCategoryService.getById(q.getPid());
						q.setFatherName(byId.getCategoryName());
						if (q.getStatus()!=null && q.getStatus()==1){
                          q.setStatusName("启用");
						}if (q.getStatus()!=null && q.getStatus()==0){
							q.setStatusName("停用");
						}
					}
				});
				e.setChildren(list);
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
	@Transactional(rollbackFor = Exception.class)
	public Result<String> add(@RequestBody EmergencyMaterialsCategory emergencyMaterialsCategory) {
		if (StrUtil.isBlank(emergencyMaterialsCategory.getPid())){
			emergencyMaterialsCategory.setPid("0");
		}
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(rollbackFor = Exception.class)
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
	  * 通过code检验查询
	  *
	  * @param code
	  * @return
	  */
	 @AutoLog(value = "物资分类-通过code检验查询")
	 @ApiOperation(value="物资分类-通过code检验查询", notes="物资分类-通过code检验查询")
	 @GetMapping(value = "/queryByCode")
	 public Result<?> getCode(@RequestParam(name="code",required=true) String code){
		LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,code);
		queryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0);
		EmergencyMaterialsCategory one = emergencyMaterialsCategoryService.getOne(queryWrapper, false);
		if (ObjectUtil.isNull(one)){
			return Result.OK("校验成功，请继续！");
		}if (ObjectUtil.isNotNull(one)){
			return Result.OK("分类编码已存在！");
		}
		return Result.OK("校验成功，请继续！");
	 }


	 /**
	  * 通过name检验查询
	  *
	  * @param name
	  * @return
	  */
	 @AutoLog(value = "物资分类-通过name检验查询")
	 @ApiOperation(value="物资分类-通过name检验查询", notes="物资分类-通过name检验查询")
	 @GetMapping(value = "/queryByName")
	 public Result<?> getName(@RequestParam(name="name",required=true) String name){
		 LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
		 queryWrapper.eq(EmergencyMaterialsCategory::getCategoryName,name);
		 queryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0);
		 EmergencyMaterialsCategory one = emergencyMaterialsCategoryService.getOne(queryWrapper, false);
		 if (ObjectUtil.isNull(one)){
			 return Result.OK("校验成功，请继续！");
		 }if (ObjectUtil.isNotNull(one)){
			 return Result.OK("分类名称已存在！");
		 }
		 return Result.OK("校验成功，请继续！");
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param category
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsCategory category) {
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(EmergencyMaterialsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
		if(CollUtil.isNotEmpty(category.getSelections()))
		{
			List<String> selections = category.getSelections();
			queryWrapper.in(EmergencyMaterialsCategory::getId,selections);
		}
		if(ObjectUtil.isNotEmpty(category.getCategoryCode()))
		{
			queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,category.getCategoryCode());
		}
		if(ObjectUtil.isNotEmpty(category.getCategoryName()))
		{
			queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,category.getCategoryName());
		}
		List<EmergencyMaterialsCategory> list = emergencyMaterialsCategoryService.list(queryWrapper);
		list.forEach(e->{
			if (StrUtil.isNotBlank(e.getPid()) && e.getPid().equals("0")==false){
				EmergencyMaterialsCategory byId = emergencyMaterialsCategoryService.getById(e.getPid());
				e.setFatherName(byId.getCategoryName());
			}
		});
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "应急物资分类");
		mv.addObject(NormalExcelConstants.CLASS, EmergencyMaterialsCategory.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应急物资分类",  "导出信息", ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return  mv;
    }


	 /**
	  * 下载模板
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value = "/downloadTemple", method = RequestMethod.GET)
	 public void downloadTemple(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 emergencyMaterialsCategoryService.getImportTemplate(response,request);
	 }

	 /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return emergencyMaterialsCategoryService.importExcel(request, response);
    }

}
