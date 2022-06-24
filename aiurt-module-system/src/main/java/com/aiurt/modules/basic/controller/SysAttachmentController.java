package com.aiurt.modules.basic.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.basic.service.ISysAttachmentService;
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
 * @Description: sys_attachment
 * @Author: gaowei
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="sys_attachment")
@RestController
@RequestMapping("/modules.basic/sysAttachment")
@Slf4j
public class SysAttachmentController extends BaseController<SysAttachment, ISysAttachmentService> {


	@Autowired
	private ISysAttachmentService sysAttachmentService;

	/**
	 * 分页列表查询
	 *
	 * @param sysAttachment
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sys_attachment-分页列表查询")
	@ApiOperation(value="sys_attachment-分页列表查询", notes="sys_attachment-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SysAttachment>> queryPageList(SysAttachment sysAttachment,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysAttachment> queryWrapper = QueryGenerator.initQueryWrapper(sysAttachment, req.getParameterMap());
		Page<SysAttachment> page = new Page<SysAttachment>(pageNo, pageSize);
		IPage<SysAttachment> pageList = sysAttachmentService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sysAttachment
	 * @return
	 */
	@AutoLog(value = "sys_attachment-添加")
	@ApiOperation(value="sys_attachment-添加", notes="sys_attachment-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysAttachment sysAttachment) {
		sysAttachmentService.save(sysAttachment);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sysAttachment
	 * @return
	 */
	@AutoLog(value = "sys_attachment-编辑")
	@ApiOperation(value="sys_attachment-编辑", notes="sys_attachment-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysAttachment sysAttachment) {
		sysAttachmentService.updateById(sysAttachment);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sys_attachment-通过id删除")
	@ApiOperation(value="sys_attachment-通过id删除", notes="sys_attachment-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sysAttachmentService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sys_attachment-批量删除")
	@ApiOperation(value="sys_attachment-批量删除", notes="sys_attachment-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysAttachmentService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sys_attachment-通过id查询")
	@ApiOperation(value="sys_attachment-通过id查询", notes="sys_attachment-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysAttachment> queryById(@RequestParam(name="id",required=true) String id) {
		SysAttachment sysAttachment = sysAttachmentService.getById(id);
		if(sysAttachment==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysAttachment);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sysAttachment
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysAttachment sysAttachment) {
        return super.exportXls(request, sysAttachment, SysAttachment.class, "sys_attachment");
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
        return super.importExcel(request, response, SysAttachment.class);
    }

}
