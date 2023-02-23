package com.aiurt.modules.faultproducereport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
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
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
@Api(tags="生产日报")
@RestController
@RequestMapping("/faultproducereport/faultProduceReport")
@Slf4j
public class FaultProduceReportController extends BaseController<FaultProduceReport, IFaultProduceReportService> {
	@Autowired
	private IFaultProduceReportService faultProduceReportService;

	/**
	 * 分页列表查询
	 *
	 * @param faultProduceReport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产日报-分页列表查询")
	@ApiOperation(value="生产日报-分页列表查询", notes="生产日报-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultProduceReport>> queryPageList(FaultProduceReport faultProduceReport,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FaultProduceReport> queryWrapper = QueryGenerator.initQueryWrapper(faultProduceReport, req.getParameterMap());
		Page<FaultProduceReport> page = new Page<FaultProduceReport>(pageNo, pageSize);
		IPage<FaultProduceReport> pageList = faultProduceReportService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultProduceReport
	 * @return
	 */
	@AutoLog(value = "生产日报-添加")
	@ApiOperation(value="生产日报-添加", notes="生产日报-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultProduceReport faultProduceReport) {
		faultProduceReportService.save(faultProduceReport);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultProduceReport
	 * @return
	 */
	@AutoLog(value = "生产日报-编辑")
	@ApiOperation(value="生产日报-编辑", notes="生产日报-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultProduceReport faultProduceReport) {
		faultProduceReportService.updateById(faultProduceReport);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产日报-通过id删除")
	@ApiOperation(value="生产日报-通过id删除", notes="生产日报-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultProduceReportService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产日报-批量删除")
	@ApiOperation(value="生产日报-批量删除", notes="生产日报-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultProduceReportService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产日报-通过id查询")
	@ApiOperation(value="生产日报-通过id查询", notes="生产日报-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultProduceReport> queryById(@RequestParam(name="id",required=true) String id) {
		FaultProduceReport faultProduceReport = faultProduceReportService.getById(id);
		if(faultProduceReport==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultProduceReport);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultProduceReport
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultProduceReport faultProduceReport) {
        return super.exportXls(request, faultProduceReport, FaultProduceReport.class, "生产日报");
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
        return super.importExcel(request, response, FaultProduceReport.class);
    }

}
