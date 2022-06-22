package com.aiurt.boot.strategy.controller;

import com.aiurt.boot.strategy.entity.InspectionStrDeviceRel;
import com.aiurt.boot.strategy.service.IInspectionStrDeviceRelService;
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
 * @Description: inspection_str_device_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="inspection_str_device_rel")
@RestController
@RequestMapping("/strategy/inspectionStrDeviceRel")
@Slf4j
public class InspectionStrDeviceRelController extends BaseController<InspectionStrDeviceRel, IInspectionStrDeviceRelService> {
	@Autowired
	private IInspectionStrDeviceRelService inspectionStrDeviceRelService;

	/**
	 * 分页列表查询
	 *
	 * @param inspectionStrDeviceRel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "inspection_str_device_rel-分页列表查询")
	@ApiOperation(value="inspection_str_device_rel-分页列表查询", notes="inspection_str_device_rel-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InspectionStrDeviceRel>> queryPageList(InspectionStrDeviceRel inspectionStrDeviceRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<InspectionStrDeviceRel> queryWrapper = QueryGenerator.initQueryWrapper(inspectionStrDeviceRel, req.getParameterMap());
		Page<InspectionStrDeviceRel> page = new Page<InspectionStrDeviceRel>(pageNo, pageSize);
		IPage<InspectionStrDeviceRel> pageList = inspectionStrDeviceRelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param inspectionStrDeviceRel
	 * @return
	 */
	@AutoLog(value = "inspection_str_device_rel-添加")
	@ApiOperation(value="inspection_str_device_rel-添加", notes="inspection_str_device_rel-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InspectionStrDeviceRel inspectionStrDeviceRel) {
		inspectionStrDeviceRelService.save(inspectionStrDeviceRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param inspectionStrDeviceRel
	 * @return
	 */
	@AutoLog(value = "inspection_str_device_rel-编辑")
	@ApiOperation(value="inspection_str_device_rel-编辑", notes="inspection_str_device_rel-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InspectionStrDeviceRel inspectionStrDeviceRel) {
		inspectionStrDeviceRelService.updateById(inspectionStrDeviceRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_str_device_rel-通过id删除")
	@ApiOperation(value="inspection_str_device_rel-通过id删除", notes="inspection_str_device_rel-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		inspectionStrDeviceRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "inspection_str_device_rel-批量删除")
	@ApiOperation(value="inspection_str_device_rel-批量删除", notes="inspection_str_device_rel-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.inspectionStrDeviceRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "inspection_str_device_rel-通过id查询")
	@ApiOperation(value="inspection_str_device_rel-通过id查询", notes="inspection_str_device_rel-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InspectionStrDeviceRel> queryById(@RequestParam(name="id",required=true) String id) {
		InspectionStrDeviceRel inspectionStrDeviceRel = inspectionStrDeviceRelService.getById(id);
		if(inspectionStrDeviceRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inspectionStrDeviceRel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param inspectionStrDeviceRel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionStrDeviceRel inspectionStrDeviceRel) {
        return super.exportXls(request, inspectionStrDeviceRel, InspectionStrDeviceRel.class, "inspection_str_device_rel");
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
        return super.importExcel(request, response, InspectionStrDeviceRel.class);
    }

}
