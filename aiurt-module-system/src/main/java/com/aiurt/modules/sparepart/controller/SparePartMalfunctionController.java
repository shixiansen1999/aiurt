package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.aiurt.modules.sparepart.service.ISparePartMalfunctionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

 /**
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="备件管理-备件履历")
@RestController
@RequestMapping("/sparepart/sparePartMalfunction")
@Slf4j
public class SparePartMalfunctionController extends BaseController<SparePartMalfunction, ISparePartMalfunctionService> {
	@Autowired
	private ISparePartMalfunctionService sparePartMalfunctionService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartMalfunction
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
	@ApiOperation(value="spare_part_malfunction-分页列表查询", notes="spare_part_malfunction-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartMalfunction>> queryPageList(SparePartMalfunction sparePartMalfunction,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		Page<SparePartMalfunction> page = new Page<SparePartMalfunction>(pageNo, pageSize);
		IPage<SparePartMalfunction> pageList = sparePartMalfunctionService.pageList(page, sparePartMalfunction);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartMalfunction
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
	@ApiOperation(value="spare_part_malfunction-添加", notes="spare_part_malfunction-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartMalfunction sparePartMalfunction) {
		sparePartMalfunctionService.save(sparePartMalfunction);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartMalfunction
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
	@ApiOperation(value="spare_part_malfunction-编辑", notes="spare_part_malfunction-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartMalfunction sparePartMalfunction) {
		sparePartMalfunctionService.updateById(sparePartMalfunction);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
	@ApiOperation(value="spare_part_malfunction-通过id删除", notes="spare_part_malfunction-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartMalfunctionService.removeById(id);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
	@ApiOperation(value="spare_part_malfunction-通过id查询", notes="spare_part_malfunction-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartMalfunction> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartMalfunction sparePartMalfunction = sparePartMalfunctionService.getById(id);
		if(sparePartMalfunction==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartMalfunction);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件履历",permissionUrl = "/sparepart/sparePartMalfunction/list")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartMalfunction partMalfunction = new SparePartMalfunction();
		partMalfunction.setIds(Arrays.asList(ids.split(",")));
		List<SparePartMalfunction> list = sparePartMalfunctionService.selectList(partMalfunction);
		list = list.stream().distinct().collect(Collectors.toList());
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件出库履历详情");
		mv.addObject(NormalExcelConstants.CLASS, SparePartMalfunction.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件出库履历详情", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);

		return mv;
    }



}
