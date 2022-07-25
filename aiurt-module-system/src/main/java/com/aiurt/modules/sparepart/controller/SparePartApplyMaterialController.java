package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_apply_material
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Api(tags="备件管理-备件申领-物资")
@RestController
@RequestMapping("/sparepart/sparePartApplyMaterial")
@Slf4j
public class SparePartApplyMaterialController extends BaseController<SparePartApplyMaterial, ISparePartApplyMaterialService> {
	@Autowired
	private ISparePartApplyMaterialService sparePartApplyMaterialService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartApplyMaterial
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_apply_material-分页列表查询")
	@ApiOperation(value="spare_part_apply_material-分页列表查询", notes="spare_part_apply_material-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartApplyMaterial>> queryPageList(SparePartApplyMaterial sparePartApplyMaterial,
															   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															   HttpServletRequest req) {
		QueryWrapper<SparePartApplyMaterial> queryWrapper = QueryGenerator.initQueryWrapper(sparePartApplyMaterial, req.getParameterMap());
		Page<SparePartApplyMaterial> page = new Page<SparePartApplyMaterial>(pageNo, pageSize);
		IPage<SparePartApplyMaterial> pageList = sparePartApplyMaterialService.page(page, queryWrapper.lambda().eq(SparePartApplyMaterial::getDelFlag, CommonConstant.DEL_FLAG_0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartApplyMaterial
	 * @return
	 */
	@AutoLog(value = "spare_part_apply_material-添加")
	@ApiOperation(value="spare_part_apply_material-添加", notes="spare_part_apply_material-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartApplyMaterial sparePartApplyMaterial) {
		sparePartApplyMaterialService.save(sparePartApplyMaterial);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartApplyMaterial
	 * @return
	 */
	@AutoLog(value = "spare_part_apply_material-编辑")
	@ApiOperation(value="spare_part_apply_material-编辑", notes="spare_part_apply_material-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartApplyMaterial sparePartApplyMaterial) {
		sparePartApplyMaterialService.updateById(sparePartApplyMaterial);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_apply_material-通过id删除")
	@ApiOperation(value="spare_part_apply_material-通过id删除", notes="spare_part_apply_material-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartApplyMaterialService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_apply_material-批量删除")
	@ApiOperation(value="spare_part_apply_material-批量删除", notes="spare_part_apply_material-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartApplyMaterialService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_apply_material-通过id查询")
	@ApiOperation(value="spare_part_apply_material-通过id查询", notes="spare_part_apply_material-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartApplyMaterial> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartApplyMaterial sparePartApplyMaterial = sparePartApplyMaterialService.getById(id);
		if(sparePartApplyMaterial==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartApplyMaterial);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartApplyMaterial
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartApplyMaterial sparePartApplyMaterial) {
        return super.exportXls(request, sparePartApplyMaterial, SparePartApplyMaterial.class, "spare_part_apply_material");
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
        return super.importExcel(request, response, SparePartApplyMaterial.class);
    }

}
