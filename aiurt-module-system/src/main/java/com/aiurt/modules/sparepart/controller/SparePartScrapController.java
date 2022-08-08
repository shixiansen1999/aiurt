package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Api(tags="备件管理-备件报废管理")
@RestController
@RequestMapping("/sparepart/sparePartScrap")
@Slf4j
public class SparePartScrapController extends BaseController<SparePartScrap, ISparePartScrapService> {
	@Autowired
	private ISparePartScrapService sparePartScrapService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartScrap
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件报废分页列表查询",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-分页列表查询", notes="spare_part_scrap-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartScrap>> queryPageList(SparePartScrap sparePartScrap,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<SparePartScrap> queryWrapper = QueryGenerator.initQueryWrapper(sparePartScrap, req.getParameterMap());
		Page<SparePartScrap> page = new Page<SparePartScrap>(pageNo, pageSize);
		List<SparePartScrap> list = sparePartScrapService.selectList(page, sparePartScrap);
		page.setRecords(list);
		return Result.OK(page);
	}

	/**
	 *   添加
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-添加", notes="spare_part_scrap-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartScrap sparePartScrap) {
		sparePartScrap.setStatus(2);
		sparePartScrapService.save(sparePartScrap);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-编辑", notes="spare_part_scrap-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartScrap sparePartScrap) {
		return sparePartScrapService.update(sparePartScrap);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-通过id删除", notes="spare_part_scrap-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartScrap sparePartScrap = sparePartScrapService.getById(id);
		sparePartScrap.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartScrapService.updateById(sparePartScrap);
		return Result.OK("删除成功!");
	}



	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
	@ApiOperation(value="spare_part_scrap-通过id查询", notes="spare_part_scrap-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartScrap> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartScrap sparePartScrap = sparePartScrapService.getById(id);
		if(sparePartScrap==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartScrap);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param  ids
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件报废",permissionUrl = "/sparepart/sparePartScrap/list")
    @RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartScrap sparePartScrap = new SparePartScrap();
		sparePartScrap.setIds(Arrays.asList(ids.split(",")));
		List<SparePartScrap> list = sparePartScrapService.selectList(null, sparePartScrap);
		list = list.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<list.size();i++){
			SparePartScrap order = list.get(i);
			order.setNumber(i+1+"");
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件报废管理列表");
		mv.addObject(NormalExcelConstants.CLASS, SparePartScrap.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件报废管理列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}

}
