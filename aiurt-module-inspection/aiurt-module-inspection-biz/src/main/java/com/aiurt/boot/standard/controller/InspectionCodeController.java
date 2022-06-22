package com.aiurt.boot.standard.controller;

import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.service.IInspectionCodeService;
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
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="inspection_code")
@RestController
@RequestMapping("/standard/inspectionCode")
@Slf4j
public class InspectionCodeController extends BaseController<InspectionCode, IInspectionCodeService> {
	@Autowired
	private IInspectionCodeService inspectionCodeService;

	/**
	 * 分页列表查询
	 *
	 * @param inspectionCode
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "inspection_code-分页列表查询")
	@ApiOperation(value="inspection_code-分页列表查询", notes="inspection_code-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InspectionCode>> queryPageList(InspectionCode inspectionCode,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<InspectionCode> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCode, req.getParameterMap());
		Page<InspectionCode> page = new Page<InspectionCode>(pageNo, pageSize);
		IPage<InspectionCode> pageList = inspectionCodeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param inspectionCode
	 * @return
	 */
	@AutoLog(value = "inspection_code-添加")
	@ApiOperation(value="inspection_code-添加", notes="inspection_code-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InspectionCode inspectionCode) {
		inspectionCodeService.save(inspectionCode);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param inspectionCode
	 * @return
	 */
	@AutoLog(value = "inspection_code-编辑")
	@ApiOperation(value="inspection_code-编辑", notes="inspection_code-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InspectionCode inspectionCode) {
		inspectionCodeService.updateById(inspectionCode);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code-通过id删除")
	@ApiOperation(value="inspection_code-通过id删除", notes="inspection_code-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		inspectionCodeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "inspection_code-批量删除")
	@ApiOperation(value="inspection_code-批量删除", notes="inspection_code-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.inspectionCodeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code-通过id查询")
	@ApiOperation(value="inspection_code-通过id查询", notes="inspection_code-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InspectionCode> queryById(@RequestParam(name="id",required=true) String id) {
		InspectionCode inspectionCode = inspectionCodeService.getById(id);
		if(inspectionCode==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inspectionCode);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param inspectionCode
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionCode inspectionCode) {
        return super.exportXls(request, inspectionCode, InspectionCode.class, "inspection_code");
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
        return super.importExcel(request, response, InspectionCode.class);
    }

}
