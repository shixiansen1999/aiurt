package com.aiurt.boot.task.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.boot.task.entity.RepairTaskPeerRel;
import com.aiurt.boot.task.service.IRepairTaskPeerRelService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: repair_task_peer_rel
 * @Author: aiurt
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Api(tags="repair_task_peer_rel")
@RestController
@RequestMapping("/repairtaskpeerrel/repairTaskPeerRel")
@Slf4j
public class RepairTaskPeerRelController extends BaseController<RepairTaskPeerRel, IRepairTaskPeerRelService> {
	@Autowired
	private IRepairTaskPeerRelService repairTaskPeerRelService;

	/**
	 * 分页列表查询
	 *
	 * @param repairTaskPeerRel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_task_peer_rel-分页列表查询")
	@ApiOperation(value="repair_task_peer_rel-分页列表查询", notes="repair_task_peer_rel-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairTaskPeerRel>> queryPageList(RepairTaskPeerRel repairTaskPeerRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairTaskPeerRel> queryWrapper = QueryGenerator.initQueryWrapper(repairTaskPeerRel, req.getParameterMap());
		Page<RepairTaskPeerRel> page = new Page<RepairTaskPeerRel>(pageNo, pageSize);
		IPage<RepairTaskPeerRel> pageList = repairTaskPeerRelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairTaskPeerRel
	 * @return
	 */
	@AutoLog(value = "repair_task_peer_rel-添加")
	@ApiOperation(value="repair_task_peer_rel-添加", notes="repair_task_peer_rel-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairTaskPeerRel repairTaskPeerRel) {
		repairTaskPeerRelService.save(repairTaskPeerRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairTaskPeerRel
	 * @return
	 */
	@AutoLog(value = "repair_task_peer_rel-编辑")
	@ApiOperation(value="repair_task_peer_rel-编辑", notes="repair_task_peer_rel-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairTaskPeerRel repairTaskPeerRel) {
		repairTaskPeerRelService.updateById(repairTaskPeerRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_task_peer_rel-通过id删除")
	@ApiOperation(value="repair_task_peer_rel-通过id删除", notes="repair_task_peer_rel-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairTaskPeerRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_task_peer_rel-批量删除")
	@ApiOperation(value="repair_task_peer_rel-批量删除", notes="repair_task_peer_rel-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairTaskPeerRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_task_peer_rel-通过id查询")
	@ApiOperation(value="repair_task_peer_rel-通过id查询", notes="repair_task_peer_rel-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairTaskPeerRel> queryById(@RequestParam(name="id",required=true) String id) {
		RepairTaskPeerRel repairTaskPeerRel = repairTaskPeerRelService.getById(id);
		if(repairTaskPeerRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairTaskPeerRel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairTaskPeerRel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairTaskPeerRel repairTaskPeerRel) {
        return super.exportXls(request, repairTaskPeerRel, RepairTaskPeerRel.class, "repair_task_peer_rel");
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
        return super.importExcel(request, response, RepairTaskPeerRel.class);
    }

}
