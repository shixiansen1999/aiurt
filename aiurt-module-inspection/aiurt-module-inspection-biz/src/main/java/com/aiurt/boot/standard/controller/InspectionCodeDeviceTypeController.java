package com.aiurt.boot.standard.controller;

import com.aiurt.boot.standard.entity.InspectionCodeDeviceType;
import com.aiurt.boot.standard.service.IInspectionCodeDeviceTypeService;
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
import java.util.List;

/**
 * @Description: inspection_code_device_type
 * @Author: aiurt
 * @Date:   2023-08-24
 * @Version: V1.0
 */
@Api(tags="inspection_code_device_type")
@RestController
@RequestMapping("/inspectioncodedevicetype/inspectionCodeDeviceType")
@Slf4j
public class InspectionCodeDeviceTypeController extends BaseController<InspectionCodeDeviceType, IInspectionCodeDeviceTypeService> {
	@Autowired
	private IInspectionCodeDeviceTypeService inspectionCodeDeviceTypeService;

	/**
	 * 分页列表查询
	 *
	 * @param inspectionCodeDeviceType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "inspection_code_device_type-分页列表查询")
	@ApiOperation(value="inspection_code_device_type-分页列表查询", notes="inspection_code_device_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InspectionCodeDeviceType>> queryPageList(InspectionCodeDeviceType inspectionCodeDeviceType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<InspectionCodeDeviceType> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCodeDeviceType, req.getParameterMap());
		Page<InspectionCodeDeviceType> page = new Page<InspectionCodeDeviceType>(pageNo, pageSize);
		IPage<InspectionCodeDeviceType> pageList = inspectionCodeDeviceTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param inspectionCodeDeviceType
	 * @return
	 */
	@AutoLog(value = "inspection_code_device_type-添加")
	@ApiOperation(value="inspection_code_device_type-添加", notes="inspection_code_device_type-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InspectionCodeDeviceType inspectionCodeDeviceType) {
		inspectionCodeDeviceTypeService.save(inspectionCodeDeviceType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param inspectionCodeDeviceType
	 * @return
	 */
	@AutoLog(value = "inspection_code_device_type-编辑")
	@ApiOperation(value="inspection_code_device_type-编辑", notes="inspection_code_device_type-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InspectionCodeDeviceType inspectionCodeDeviceType) {
		inspectionCodeDeviceTypeService.updateById(inspectionCodeDeviceType);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code_device_type-通过id删除")
	@ApiOperation(value="inspection_code_device_type-通过id删除", notes="inspection_code_device_type-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		inspectionCodeDeviceTypeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "inspection_code_device_type-批量删除")
	@ApiOperation(value="inspection_code_device_type-批量删除", notes="inspection_code_device_type-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.inspectionCodeDeviceTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "inspection_code_device_type-通过id查询")
	@ApiOperation(value="inspection_code_device_type-通过id查询", notes="inspection_code_device_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InspectionCodeDeviceType> queryById(@RequestParam(name="id",required=true) String id) {
		InspectionCodeDeviceType inspectionCodeDeviceType = inspectionCodeDeviceTypeService.getById(id);
		if(inspectionCodeDeviceType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inspectionCodeDeviceType);
	}

	 /**
	  * 通过检修标准code查询
	  *
	  * @param code
	  * @return
	  */
	 @ApiOperation(value="通过检修标准code查询", notes="通过检修标准code查询")
	 @GetMapping(value = "/queryByInspectionCode")
	 public Result<List<InspectionCodeDeviceType>> queryByPatrolStandardCode(@RequestParam(name="code",required=true) String code) {
		 List<InspectionCodeDeviceType> patrolStandardDeviceTypes = inspectionCodeDeviceTypeService.queryByInspectionCode(code);

		 return Result.OK(patrolStandardDeviceTypes);
	 }
    /**
    * 导出excel
    *
    * @param request
    * @param inspectionCodeDeviceType
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionCodeDeviceType inspectionCodeDeviceType) {
        return super.exportXls(request, inspectionCodeDeviceType, InspectionCodeDeviceType.class, "inspection_code_device_type");
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
        return super.importExcel(request, response, InspectionCodeDeviceType.class);
    }

}
