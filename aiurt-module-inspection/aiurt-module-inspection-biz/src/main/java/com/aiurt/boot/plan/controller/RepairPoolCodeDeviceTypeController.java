package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.RepairPoolCodeDeviceType;
import com.aiurt.boot.plan.service.IRepairPoolCodeDeviceTypeService;
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
 * @Description: repair_pool_code_device_type
 * @Author: aiurt
 * @Date:   2023-08-24
 * @Version: V1.0
 */
@Api(tags="repair_pool_code_device_type")
@RestController
@RequestMapping("/repairpoolcodedevicetype/repairPoolCodeDeviceType")
@Slf4j
public class RepairPoolCodeDeviceTypeController extends BaseController<RepairPoolCodeDeviceType, IRepairPoolCodeDeviceTypeService> {
	@Autowired
	private IRepairPoolCodeDeviceTypeService repairPoolCodeDeviceTypeService;

	/**
	 * 分页列表查询
	 *
	 * @param repairPoolCodeDeviceType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_pool_code_device_type-分页列表查询")
	@ApiOperation(value="repair_pool_code_device_type-分页列表查询", notes="repair_pool_code_device_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairPoolCodeDeviceType>> queryPageList(RepairPoolCodeDeviceType repairPoolCodeDeviceType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairPoolCodeDeviceType> queryWrapper = QueryGenerator.initQueryWrapper(repairPoolCodeDeviceType, req.getParameterMap());
		Page<RepairPoolCodeDeviceType> page = new Page<RepairPoolCodeDeviceType>(pageNo, pageSize);
		IPage<RepairPoolCodeDeviceType> pageList = repairPoolCodeDeviceTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairPoolCodeDeviceType
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_device_type-添加")
	@ApiOperation(value="repair_pool_code_device_type-添加", notes="repair_pool_code_device_type-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairPoolCodeDeviceType repairPoolCodeDeviceType) {
		repairPoolCodeDeviceTypeService.save(repairPoolCodeDeviceType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairPoolCodeDeviceType
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_device_type-编辑")
	@ApiOperation(value="repair_pool_code_device_type-编辑", notes="repair_pool_code_device_type-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairPoolCodeDeviceType repairPoolCodeDeviceType) {
		repairPoolCodeDeviceTypeService.updateById(repairPoolCodeDeviceType);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_device_type-通过id删除")
	@ApiOperation(value="repair_pool_code_device_type-通过id删除", notes="repair_pool_code_device_type-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairPoolCodeDeviceTypeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_pool_code_device_type-批量删除")
	@ApiOperation(value="repair_pool_code_device_type-批量删除", notes="repair_pool_code_device_type-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairPoolCodeDeviceTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_pool_code_device_type-通过id查询")
	@ApiOperation(value="repair_pool_code_device_type-通过id查询", notes="repair_pool_code_device_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairPoolCodeDeviceType> queryById(@RequestParam(name="id",required=true) String id) {
		RepairPoolCodeDeviceType repairPoolCodeDeviceType = repairPoolCodeDeviceTypeService.getById(id);
		if(repairPoolCodeDeviceType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPoolCodeDeviceType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairPoolCodeDeviceType
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairPoolCodeDeviceType repairPoolCodeDeviceType) {
        return super.exportXls(request, repairPoolCodeDeviceType, RepairPoolCodeDeviceType.class, "repair_pool_code_device_type");
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
        return super.importExcel(request, response, RepairPoolCodeDeviceType.class);
    }

}
