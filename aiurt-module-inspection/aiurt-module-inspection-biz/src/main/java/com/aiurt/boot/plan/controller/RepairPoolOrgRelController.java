package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.service.IRepairPoolOrgRelService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: repair_pool_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_pool_org_rel")
@RestController
@RequestMapping("/plan/repairPoolOrgRel")
@Slf4j
public class RepairPoolOrgRelController extends BaseController<RepairPoolOrgRel, IRepairPoolOrgRelService> {
	@Autowired
	private IRepairPoolOrgRelService repairPoolOrgRelService;

	/**
	 * 分页列表查询
	 *
	 * @param repairPoolOrgRel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_pool_org_rel-分页列表查询")
	@ApiOperation(value="repair_pool_org_rel-分页列表查询", notes="repair_pool_org_rel-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairPoolOrgRel>> queryPageList(RepairPoolOrgRel repairPoolOrgRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairPoolOrgRel> queryWrapper = QueryGenerator.initQueryWrapper(repairPoolOrgRel, req.getParameterMap());
		Page<RepairPoolOrgRel> page = new Page<RepairPoolOrgRel>(pageNo, pageSize);
		IPage<RepairPoolOrgRel> pageList = repairPoolOrgRelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairPoolOrgRel
	 * @return
	 */
	@AutoLog(value = "repair_pool_org_rel-添加")
	@ApiOperation(value="repair_pool_org_rel-添加", notes="repair_pool_org_rel-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairPoolOrgRel repairPoolOrgRel) {
		repairPoolOrgRelService.save(repairPoolOrgRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairPoolOrgRel
	 * @return
	 */
	@AutoLog(value = "repair_pool_org_rel-编辑")
	@ApiOperation(value="repair_pool_org_rel-编辑", notes="repair_pool_org_rel-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairPoolOrgRel repairPoolOrgRel) {
		repairPoolOrgRelService.updateById(repairPoolOrgRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_pool_org_rel-通过id删除")
	@ApiOperation(value="repair_pool_org_rel-通过id删除", notes="repair_pool_org_rel-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairPoolOrgRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_pool_org_rel-批量删除")
	@ApiOperation(value="repair_pool_org_rel-批量删除", notes="repair_pool_org_rel-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairPoolOrgRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_pool_org_rel-通过id查询")
	@ApiOperation(value="repair_pool_org_rel-通过id查询", notes="repair_pool_org_rel-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairPoolOrgRel> queryById(@RequestParam(name="id",required=true) String id) {
		RepairPoolOrgRel repairPoolOrgRel = repairPoolOrgRelService.getById(id);
		if(repairPoolOrgRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPoolOrgRel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairPoolOrgRel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairPoolOrgRel repairPoolOrgRel) {
        return super.exportXls(request, repairPoolOrgRel, RepairPoolOrgRel.class, "repair_pool_org_rel");
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
        return super.importExcel(request, response, RepairPoolOrgRel.class);
    }

}
