package com.aiurt.modules.faultattachments.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultattachments.entity.FaultAttachments;
import com.aiurt.modules.faultattachments.service.IFaultAttachmentsService;
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
 * @Description: fault_attachments
 * @Author: aiurt
 * @Date:   2023-10-09
 * @Version: V1.0
 */
@Api(tags="fault_attachments")
@RestController
@RequestMapping("/faultattachments/faultAttachments")
@Slf4j
public class FaultAttachmentsController extends BaseController<FaultAttachments, IFaultAttachmentsService> {
	@Autowired
	private IFaultAttachmentsService faultAttachmentsService;

	/**
	 * 分页列表查询
	 *
	 * @param faultAttachments
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fault_attachments-分页列表查询")
	@ApiOperation(value="fault_attachments-分页列表查询", notes="fault_attachments-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultAttachments>> queryPageList(FaultAttachments faultAttachments,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
		QueryWrapper<FaultAttachments> queryWrapper = QueryGenerator.initQueryWrapper(faultAttachments, req.getParameterMap());
		Page<FaultAttachments> page = new Page<FaultAttachments>(pageNo, pageSize);
		IPage<FaultAttachments> pageList = faultAttachmentsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultAttachments
	 * @return
	 */
	@AutoLog(value = "fault_attachments-添加")
	@ApiOperation(value="fault_attachments-添加", notes="fault_attachments-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultAttachments faultAttachments) {
		faultAttachmentsService.save(faultAttachments);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultAttachments
	 * @return
	 */
	@AutoLog(value = "fault_attachments-编辑")
	@ApiOperation(value="fault_attachments-编辑", notes="fault_attachments-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultAttachments faultAttachments) {
		faultAttachmentsService.updateById(faultAttachments);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "fault_attachments-通过id删除")
	@ApiOperation(value="fault_attachments-通过id删除", notes="fault_attachments-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultAttachmentsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "fault_attachments-批量删除")
	@ApiOperation(value="fault_attachments-批量删除", notes="fault_attachments-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultAttachmentsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fault_attachments-通过id查询")
	@ApiOperation(value="fault_attachments-通过id查询", notes="fault_attachments-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultAttachments> queryById(@RequestParam(name="id",required=true) String id) {
		FaultAttachments faultAttachments = faultAttachmentsService.getById(id);
		if(faultAttachments==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultAttachments);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultAttachments
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultAttachments faultAttachments) {
        return super.exportXls(request, faultAttachments, FaultAttachments.class, "fault_attachments");
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
        return super.importExcel(request, response, FaultAttachments.class);
    }

}
