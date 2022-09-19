package com.aiurt.modules.dailyschedule.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: 日程安排
 * @Author: aiurt
 * @Date:   2022-09-08
 * @Version: V1.0
 */
@Api(tags="日程安排")
@RestController
@RequestMapping("/dailySchedule")
@Slf4j
public class DailyScheduleController extends BaseController<DailySchedule, IDailyScheduleService> {
	@Autowired
	private IDailyScheduleService dailyScheduleService;

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param dailySchedule
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "日程安排-分页列表查询")
	@ApiOperation(value="日程安排-分页列表查询", notes="日程安排-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DailySchedule>> queryPageList(DailySchedule dailySchedule,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DailySchedule> queryWrapper = QueryGenerator.initQueryWrapper(dailySchedule, req.getParameterMap());
		Page<DailySchedule> page = new Page<DailySchedule>(pageNo, pageSize);
		IPage<DailySchedule> pageList = dailyScheduleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	 @ApiOperation(value="日程安排-首页", notes="日程安排-首页")
	 @GetMapping(value = "/queryList")
	 public Result<List<DailySchedule>> queryList(@ApiParam(value = "年份") @RequestParam(name = "year") Integer year,
												  @ApiParam(value = "月份") @RequestParam(name = "month") Integer month,
												  @ApiParam(value = "日") @RequestParam(name = "day") Integer day) {
		 List<DailySchedule> resultList = dailyScheduleService.queryList(year, month, day);
		 return Result.OK(resultList);
	 }
	/**
	 *   添加
	 *
	 * @param dailySchedule
	 * @return
	 */
	@AutoLog(value = "日程安排-添加")
	@ApiOperation(value="日程安排-添加", notes="日程安排-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DailySchedule dailySchedule) {
		String notifyUserId = dailySchedule.getNotifyUserId();
		List<String> userNameList = StrUtil.split(notifyUserId, ',');
		List<String> realNameList = new ArrayList<>();
		userNameList.stream().forEach(userName->{
			LambdaQueryWrapper<DailySchedule> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(DailySchedule::getAddTime, dailySchedule.getAddTime()).apply("(FIND_IN_SET({0},notify_user_id)>0)", userName);
			long count = dailyScheduleService.count(queryWrapper);
			if (count>3) {
				LoginUser userByName = sysBaseAPI.getUserByName(userName);
				realNameList.add(userByName.getRealname());
			}
		});

		if (CollectionUtil.isNotEmpty(realNameList)) {
			throw new AiurtBootException(String.format("%s日程已经超过三次，请勿添加！", JSONObject.toJSONString(realNameList)));
		}

		dailyScheduleService.save(dailySchedule);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param dailySchedule
	 * @return
	 */
	@AutoLog(value = "日程安排-编辑")
	@ApiOperation(value="日程安排-编辑", notes="日程安排-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DailySchedule dailySchedule) {
		List<String> userNameList = StrUtil.split(dailySchedule.getNotifyUserId(), ',');
		List<String> realNameList = new ArrayList<>();
		userNameList.stream().forEach(userName->{
			LambdaQueryWrapper<DailySchedule> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(DailySchedule::getAddTime, dailySchedule.getAddTime()).ne(DailySchedule::getId, dailySchedule.getId())
					.apply("(FIND_IN_SET({0},notify_user_id)>0)", userName);
			long count = dailyScheduleService.count(queryWrapper);
			if (count>3) {
				LoginUser userByName = sysBaseAPI.getUserByName(userName);
				realNameList.add(userByName.getRealname());
			}
		});

		if (CollectionUtil.isNotEmpty(realNameList)) {
			throw new AiurtBootException(String.format("%s日程已经超过三次，请勿添加！", JSONObject.toJSONString(realNameList)));
		}
		dailyScheduleService.updateById(dailySchedule);

		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "日程安排-通过id删除")
	@ApiOperation(value="日程安排-通过id删除", notes="日程安排-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		dailyScheduleService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "日程安排-批量删除")
	@ApiOperation(value="日程安排-批量删除", notes="日程安排-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.dailyScheduleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "日程安排-通过id查询")
	@ApiOperation(value="日程安排-通过id查询", notes="日程安排-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DailySchedule> queryById(@RequestParam(name="id",required=true) String id) {
		DailySchedule dailySchedule = dailyScheduleService.getById(id);
		if(dailySchedule==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(dailySchedule);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param dailySchedule
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DailySchedule dailySchedule) {
        return super.exportXls(request, dailySchedule, DailySchedule.class, "日程安排");
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
        return super.importExcel(request, response, DailySchedule.class);
    }

}
