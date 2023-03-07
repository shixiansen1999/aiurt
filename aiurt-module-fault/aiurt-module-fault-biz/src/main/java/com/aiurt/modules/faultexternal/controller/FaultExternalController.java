package com.aiurt.modules.faultexternal.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultexternal.dto.FaultExternalDTO;
import com.aiurt.modules.faultexternal.dto.FalutExternalReceiveDTO;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.aiurt.modules.faultexternal.service.IFaultExternalService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
@Api(tags="调度系统故障")
@RestController
@RequestMapping("/external/faultExternal")
@Slf4j
public class FaultExternalController extends BaseController<FaultExternal, IFaultExternalService> {
	@Autowired
	private IFaultExternalService faultExternalService;

	/**
	 * 分页列表查询
	 *
	 * @param faultExternal
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "调度系统故障-分页列表查询")
	@ApiOperation(value="调度系统故障-分页列表查询", notes="调度系统故障-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultExternal>> queryPageList(FaultExternal faultExternal,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<FaultExternal> queryWrapper = QueryGenerator.initQueryWrapper(faultExternal, req.getParameterMap());
		Page<FaultExternal> page = new Page<FaultExternal>(pageNo, pageSize);
		//IPage<FaultExternal> pageList = faultExternalService.page(page, queryWrapper);
		Page<FaultExternal> pageList = faultExternalService.selectPage(page, faultExternal);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param
	 * @return
	 */
	@AutoLog(value = "调度系统故障-添加")
	@ApiOperation(value="调度系统故障-添加", notes="调度系统故障-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FalutExternalReceiveDTO falutExternalReceiveDTO) {
		String code = "200";
		if (StrUtil.equals(falutExternalReceiveDTO.getCode(), code)) {
			faultExternalService.save(falutExternalReceiveDTO.getData());
			return Result.OK("添加成功！");
		} else {
			return Result.error(falutExternalReceiveDTO.getCode(), falutExternalReceiveDTO.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param faultExternal
	 * @return
	 */
	@AutoLog(value = "调度系统故障-编辑")
	@ApiOperation(value="调度系统故障-编辑", notes="调度系统故障-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultExternal faultExternal) {
		faultExternalService.updateById(faultExternal);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "调度系统故障-通过id删除")
	@ApiOperation(value="调度系统故障-通过id删除", notes="调度系统故障-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultExternalService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "调度系统故障-批量删除")
	@ApiOperation(value="调度系统故障-批量删除", notes="调度系统故障-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultExternalService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "调度系统故障-通过id查询")
	@ApiOperation(value="调度系统故障-通过id查询", notes="调度系统故障-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultExternal> queryById(@RequestParam(name="id",required=true) String id) {
		FaultExternal faultExternal = faultExternalService.getById(id);
		if(faultExternal==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultExternal);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultExternal
    */
	@ApiOperation(value = "调度系统故障-导出excel", notes = "调度系统故障-导出excel")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    public ModelAndView exportXls(HttpServletRequest request, FaultExternal faultExternal) {
        return super.exportXls(request, faultExternal, FaultExternal.class, "调度系统故障");
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
        return super.importExcel(request, response, FaultExternal.class);
    }

	 @AutoLog(value = "故障表-添加")
	 @ApiOperation(value = "故障表-添加", notes = "故障表-添加")
	 @PostMapping(value = "/addExternal")
	 public Result<?> addExternal(@RequestBody FaultExternalDTO dto, HttpServletRequest req) {
		 try {
			 return faultExternalService.addFaultExternal(dto, req);
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 return Result.error("添加失败,原因:"+e.getMessage());
		 }
	 }

}
