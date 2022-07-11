package com.aiurt.boot.standard.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.standard.dto.PatrolStandardDto;
import org.jeecg.common.api.vo.Result;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.service.IPatrolStandardService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="巡检标准")
@RestController
@RequestMapping("/patrolStandard")
@Slf4j
public class PatrolStandardController extends BaseController<PatrolStandard, IPatrolStandardService> {
	@Autowired
	private IPatrolStandardService patrolStandardService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolStandard
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检标准表-分页列表查询")
	@ApiOperation(value="巡检标准表-分页列表查询", notes="巡检标准表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolStandardDto>> queryPageList(PatrolStandardDto patrolStandard,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														  HttpServletRequest req) {
		Page<PatrolStandardDto> page = new Page<PatrolStandardDto>(pageNo, pageSize);
		IPage<PatrolStandardDto> pageList = patrolStandardService.pageList(page, patrolStandard);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolStandard
	 * @return
	 */
	@AutoLog(value = "巡检标准表-添加")
	@ApiOperation(value="巡检标准表-添加", notes="巡检标准表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolStandard patrolStandard) {
		patrolStandard.setCode("XB"+System.currentTimeMillis());
		patrolStandardService.save(patrolStandard);
		return Result.OK("添加成功！");
	}
	 /**
	  *   修改关联设备字段
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "巡检标准表-修改关联设备字段")
	 @ApiOperation(value="巡检标准表-修改关联设备字段", notes="巡检标准表-修改关联设备字段")
	 @PostMapping(value = "/deviceAssociation")
	 public Result<String> deviceAssociation( @RequestParam(name="id") String id) {
	 	PatrolStandard patrolStandard =new PatrolStandard();
		 patrolStandard.setId(id);
		 patrolStandard.setSpecifyDevice(1);
		 patrolStandardService.updateById(patrolStandard);
		 return Result.OK("修改成功！");
	 }
	 /**
	  *  生成巡检Code
	  * @param
	  * @return
	  */
	 @AutoLog(value = "生成巡检Code")
	 @ApiOperation(value="生成巡检Code", notes="生成巡检Code")
	 @GetMapping(value = "/generateCode")
	 public Result<String> generateCode() {
		String code="XB"+System.currentTimeMillis();
		 return Result.OK(code);
	 }
	/**
	 *  编辑
	 *
	 * @param patrolStandard
	 * @return
	 */
	@AutoLog(value = "巡检标准表-编辑")
	@ApiOperation(value="巡检标准表-编辑", notes="巡检标准表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolStandard patrolStandard) {
		patrolStandardService.updateById(patrolStandard);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检标准表-通过id删除")
	@ApiOperation(value="巡检标准表-通过id删除", notes="巡检标准表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		PatrolStandard patrolStandard = new PatrolStandard();
		patrolStandard.setId(id); patrolStandard.setDelFlag(1);
		patrolStandardService.updateById(patrolStandard);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "巡检标准表-批量删除")
	@ApiOperation(value="巡检标准表-批量删除", notes="巡检标准表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> id = Arrays.asList(ids.split(","));
		for (String id1 :id){
			this.delete(id1);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检标准表-通过id查询")
	@ApiOperation(value="巡检标准表-通过id查询", notes="巡检标准表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolStandard> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolStandard patrolStandard = patrolStandardService.getById(id);
		if(patrolStandard==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolStandard);
	}

	 @AutoLog(value = "巡检标准表-获取适用专业")
	 @ApiOperation(value="获取适用专业", notes="获取适用专业")
	 @GetMapping(value = "/obtainApplicableDisciplines")
	 public List<?> obtainApplicableDisciplines(@RequestParam(name="professionCode",required=false) String professionCode,
												@RequestParam(name="subsystemCode",required=false) String subsystemCode) {
		List<?> list = patrolStandardService.lists(professionCode,subsystemCode);
		 return list;
	 }
    /**
    * 导出excel
    *
    * @param request
    * @param patrolStandard
    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, PatrolStandard patrolStandard) {
//        return super.exportXls(request, patrolStandard, PatrolStandard.class, "patrol_standard");
//    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, PatrolStandard.class);
//    }

}
