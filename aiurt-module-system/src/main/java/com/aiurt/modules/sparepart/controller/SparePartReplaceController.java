package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.sparepart.entity.SparePartReplace;
import com.aiurt.modules.sparepart.service.ISparePartReplaceService;

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
 * @Description: spare_part_replace
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="备件管理-备件更换记录")
@RestController
@RequestMapping("/sparepart/sparePartReplace")
@Slf4j
public class SparePartReplaceController extends BaseController<SparePartReplace, ISparePartReplaceService> {
	@Autowired
	private ISparePartReplaceService sparePartReplaceService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartReplace
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_replace-分页列表查询")
	@ApiOperation(value="spare_part_replace-分页列表查询", notes="spare_part_replace-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartReplace>> queryPageList(SparePartReplace sparePartReplace,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SparePartReplace> queryWrapper = QueryGenerator.initQueryWrapper(sparePartReplace, req.getParameterMap());
		Page<SparePartReplace> page = new Page<SparePartReplace>(pageNo, pageSize);
		IPage<SparePartReplace> pageList = sparePartReplaceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartReplace
	 * @return
	 */
	@AutoLog(value = "spare_part_replace-添加")
	@ApiOperation(value="spare_part_replace-添加", notes="spare_part_replace-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartReplace sparePartReplace) {
		sparePartReplaceService.save(sparePartReplace);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartReplace
	 * @return
	 */
	@AutoLog(value = "spare_part_replace-编辑")
	@ApiOperation(value="spare_part_replace-编辑", notes="spare_part_replace-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartReplace sparePartReplace) {
		sparePartReplaceService.updateById(sparePartReplace);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_replace-通过id删除")
	@ApiOperation(value="spare_part_replace-通过id删除", notes="spare_part_replace-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartReplaceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_replace-批量删除")
	@ApiOperation(value="spare_part_replace-批量删除", notes="spare_part_replace-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartReplaceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_replace-通过id查询")
	@ApiOperation(value="spare_part_replace-通过id查询", notes="spare_part_replace-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartReplace> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartReplace sparePartReplace = sparePartReplaceService.getById(id);
		if(sparePartReplace==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartReplace);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartReplace
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartReplace sparePartReplace) {
        return super.exportXls(request, sparePartReplace, SparePartReplace.class, "spare_part_replace");
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
        return super.importExcel(request, response, SparePartReplace.class);
    }

}
