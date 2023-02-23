package com.aiurt.modules.faultproducereportlinedetail.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
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
 * @Description: 专业故障清单
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
@Api(tags="专业故障清单")
@RestController
@RequestMapping("/faultproducereportlinedetail/faultProduceReportLineDetail")
@Slf4j
public class FaultProduceReportLineDetailController extends BaseController<FaultProduceReportLineDetail, IFaultProduceReportLineDetailService> {
	@Autowired
	private IFaultProduceReportLineDetailService faultProduceReportLineDetailService;

	/**
	 * 分页列表查询
	 *
	 * @param faultProduceReportLineDetail
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "专业故障清单-分页列表查询")
	@ApiOperation(value="专业故障清单-分页列表查询", notes="专业故障清单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultProduceReportLineDetail>> queryPageList(FaultProduceReportLineDetail faultProduceReportLineDetail,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FaultProduceReportLineDetail> queryWrapper = QueryGenerator.initQueryWrapper(faultProduceReportLineDetail, req.getParameterMap());
		Page<FaultProduceReportLineDetail> page = new Page<FaultProduceReportLineDetail>(pageNo, pageSize);
		IPage<FaultProduceReportLineDetail> pageList = faultProduceReportLineDetailService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultProduceReportLineDetail
	 * @return
	 */
	@AutoLog(value = "专业故障清单-添加")
	@ApiOperation(value="专业故障清单-添加", notes="专业故障清单-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultProduceReportLineDetail faultProduceReportLineDetail) {
		faultProduceReportLineDetailService.save(faultProduceReportLineDetail);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultProduceReportLineDetail
	 * @return
	 */
	@AutoLog(value = "专业故障清单-编辑")
	@ApiOperation(value="专业故障清单-编辑", notes="专业故障清单-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultProduceReportLineDetail faultProduceReportLineDetail) {
		faultProduceReportLineDetailService.updateById(faultProduceReportLineDetail);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "专业故障清单-通过id删除")
	@ApiOperation(value="专业故障清单-通过id删除", notes="专业故障清单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultProduceReportLineDetailService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "专业故障清单-批量删除")
	@ApiOperation(value="专业故障清单-批量删除", notes="专业故障清单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultProduceReportLineDetailService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "专业故障清单-通过id查询")
	@ApiOperation(value="专业故障清单-通过id查询", notes="专业故障清单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultProduceReportLineDetail> queryById(@RequestParam(name="id",required=true) String id) {
		FaultProduceReportLineDetail faultProduceReportLineDetail = faultProduceReportLineDetailService.getById(id);
		if(faultProduceReportLineDetail==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultProduceReportLineDetail);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultProduceReportLineDetail
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultProduceReportLineDetail faultProduceReportLineDetail) {
        return super.exportXls(request, faultProduceReportLineDetail, FaultProduceReportLineDetail.class, "专业故障清单");
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
        return super.importExcel(request, response, FaultProduceReportLineDetail.class);
    }

}
