package com.aiurt.modules.online.page.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.page.entity.ActCustomPageField;
import com.aiurt.modules.online.page.service.IActCustomPageFieldService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: act_custom_page_field
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
@Api(tags="act_custom_page_field")
@RestController
@RequestMapping("/pagefield/actCustomPageField")
@Slf4j
public class ActCustomPageFieldController extends BaseController<ActCustomPageField, IActCustomPageFieldService> {
	@Autowired
	private IActCustomPageFieldService actCustomPageFieldService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomPageField
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "act_custom_page_field-分页列表查询")
	@ApiOperation(value="act_custom_page_field-分页列表查询", notes="act_custom_page_field-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomPageField>> queryPageList(ActCustomPageField actCustomPageField,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<ActCustomPageField> queryWrapper = QueryGenerator.initQueryWrapper(actCustomPageField, req.getParameterMap());
		Page<ActCustomPageField> page = new Page<ActCustomPageField>(pageNo, pageSize);
		IPage<ActCustomPageField> pageList = actCustomPageFieldService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomPageField
	 * @return
	 */
	@AutoLog(value = "act_custom_page_field-添加")
	@ApiOperation(value="act_custom_page_field-添加", notes="act_custom_page_field-添加")
	@RequiresPermissions("pagefield:act_custom_page_field:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomPageField actCustomPageField) {
		actCustomPageFieldService.save(actCustomPageField);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomPageField
	 * @return
	 */
	@AutoLog(value = "act_custom_page_field-编辑")
	@ApiOperation(value="act_custom_page_field-编辑", notes="act_custom_page_field-编辑")
	@RequiresPermissions("pagefield:act_custom_page_field:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomPageField actCustomPageField) {
		actCustomPageFieldService.updateById(actCustomPageField);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "act_custom_page_field-通过id删除")
	@ApiOperation(value="act_custom_page_field-通过id删除", notes="act_custom_page_field-通过id删除")
	@RequiresPermissions("pagefield:act_custom_page_field:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomPageFieldService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "act_custom_page_field-批量删除")
	@ApiOperation(value="act_custom_page_field-批量删除", notes="act_custom_page_field-批量删除")
	@RequiresPermissions("pagefield:act_custom_page_field:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomPageFieldService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "act_custom_page_field-通过id查询")
	@ApiOperation(value="act_custom_page_field-通过id查询", notes="act_custom_page_field-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomPageField> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomPageField actCustomPageField = actCustomPageFieldService.getById(id);
		if(actCustomPageField==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomPageField);
	}


}
