package com.aiurt.boot.weeklyplan.controller;

import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
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
 * @Description: construction_command_assist
 * @Author: aiurt
 * @Date:   2022-11-22
 * @Version: V1.0
 */
@Api(tags="construction_command_assist")
@RestController
@RequestMapping("/weeklyplan/constructionCommandAssist")
@Slf4j
public class ConstructionCommandAssistController extends BaseController<ConstructionCommandAssist, IConstructionCommandAssistService> {
	@Autowired
	private IConstructionCommandAssistService constructionCommandAssistService;

	/**
	 * 施工周计划令分页列表查询
	 *
	 * @param constructionCommandAssist
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "施工周计划令分页列表查询")
	@ApiOperation(value="施工周计划令分页列表查询", notes="施工周计划令分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ConstructionCommandAssist>> queryPageList(ConstructionCommandAssist constructionCommandAssist,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ConstructionCommandAssist> queryWrapper = QueryGenerator.initQueryWrapper(constructionCommandAssist, req.getParameterMap());
		Page<ConstructionCommandAssist> page = new Page<ConstructionCommandAssist>(pageNo, pageSize);
		IPage<ConstructionCommandAssist> pageList = constructionCommandAssistService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param constructionCommandAssist
	 * @return
	 */
	@AutoLog(value = "construction_command_assist-添加")
	@ApiOperation(value="construction_command_assist-添加", notes="construction_command_assist-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ConstructionCommandAssist constructionCommandAssist) {
		constructionCommandAssistService.save(constructionCommandAssist);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param constructionCommandAssist
	 * @return
	 */
	@AutoLog(value = "construction_command_assist-编辑")
	@ApiOperation(value="construction_command_assist-编辑", notes="construction_command_assist-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ConstructionCommandAssist constructionCommandAssist) {
		constructionCommandAssistService.updateById(constructionCommandAssist);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "construction_command_assist-通过id删除")
	@ApiOperation(value="construction_command_assist-通过id删除", notes="construction_command_assist-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		constructionCommandAssistService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "construction_command_assist-批量删除")
	@ApiOperation(value="construction_command_assist-批量删除", notes="construction_command_assist-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.constructionCommandAssistService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "construction_command_assist-通过id查询")
	@ApiOperation(value="construction_command_assist-通过id查询", notes="construction_command_assist-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ConstructionCommandAssist> queryById(@RequestParam(name="id",required=true) String id) {
		ConstructionCommandAssist constructionCommandAssist = constructionCommandAssistService.getById(id);
		if(constructionCommandAssist==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(constructionCommandAssist);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param constructionCommandAssist
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ConstructionCommandAssist constructionCommandAssist) {
        return super.exportXls(request, constructionCommandAssist, ConstructionCommandAssist.class, "construction_command_assist");
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
        return super.importExcel(request, response, ConstructionCommandAssist.class);
    }

}
