package com.aiurt.boot.strategy.controller;

import com.aiurt.boot.entity.inspection.strategy.InspectionStrOrgRel;
import com.aiurt.boot.strategy.service.IInspectionStrOrgRelService;
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
 * @Description: inspection_str_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="inspection_str_org_rel")
@RestController
@RequestMapping("/strategy/inspectionStrOrgRel")
@Slf4j
public class InspectionStrOrgRelController extends BaseController<InspectionStrOrgRel, IInspectionStrOrgRelService> {
	@Autowired
	private IInspectionStrOrgRelService inspectionStrOrgRelService;

	/**
	 * 分页列表查询
	 *
	 * @param inspectionStrOrgRel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "inspection_str_org_rel-分页列表查询")
	@ApiOperation(value="inspection_str_org_rel-分页列表查询", notes="inspection_str_org_rel-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InspectionStrOrgRel>> queryPageList(InspectionStrOrgRel inspectionStrOrgRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<InspectionStrOrgRel> queryWrapper = QueryGenerator.initQueryWrapper(inspectionStrOrgRel, req.getParameterMap());
		Page<InspectionStrOrgRel> page = new Page<InspectionStrOrgRel>(pageNo, pageSize);
		IPage<InspectionStrOrgRel> pageList = inspectionStrOrgRelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param inspectionStrOrgRel
	 * @return
	 */
	@AutoLog(value = "inspection_str_org_rel-添加")
	@ApiOperation(value="inspection_str_org_rel-添加", notes="inspection_str_org_rel-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InspectionStrOrgRel inspectionStrOrgRel) {
		inspectionStrOrgRelService.save(inspectionStrOrgRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param inspectionStrOrgRel
	 * @return
	 */
	@AutoLog(value = "inspection_str_org_rel-编辑")
	@ApiOperation(value="inspection_str_org_rel-编辑", notes="inspection_str_org_rel-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InspectionStrOrgRel inspectionStrOrgRel) {
		inspectionStrOrgRelService.updateById(inspectionStrOrgRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_str_org_rel-通过id删除")
	@ApiOperation(value="inspection_str_org_rel-通过id删除", notes="inspection_str_org_rel-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		inspectionStrOrgRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "inspection_str_org_rel-批量删除")
	@ApiOperation(value="inspection_str_org_rel-批量删除", notes="inspection_str_org_rel-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.inspectionStrOrgRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "inspection_str_org_rel-通过id查询")
	@ApiOperation(value="inspection_str_org_rel-通过id查询", notes="inspection_str_org_rel-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InspectionStrOrgRel> queryById(@RequestParam(name="id",required=true) String id) {
		InspectionStrOrgRel inspectionStrOrgRel = inspectionStrOrgRelService.getById(id);
		if(inspectionStrOrgRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inspectionStrOrgRel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param inspectionStrOrgRel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionStrOrgRel inspectionStrOrgRel) {
        return super.exportXls(request, inspectionStrOrgRel, InspectionStrOrgRel.class, "inspection_str_org_rel");
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
        return super.importExcel(request, response, InspectionStrOrgRel.class);
    }

}
