package com.aiurt.modules.system.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.system.entity.ClientLog;
import com.aiurt.modules.system.service.IClientLogService;
import com.aiurt.modules.system.util.CoordinateTransformUtil;
import com.alibaba.fastjson.JSON;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Description: client_log
 * @Author: aiurt
 * @Date:   2023-06-12
 * @Version: V1.0
 */
@Api(tags="client_log")
@RestController
@RequestMapping("/clientlog/clientLog")
@Slf4j
public class ClientLogController extends BaseController<ClientLog, IClientLogService> {
	@Autowired
	private IClientLogService clientLogService;

	/**
	 * 分页列表查询
	 *
	 * @param clientLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "client_log-分页列表查询")
	@ApiOperation(value="client_log-分页列表查询", notes="client_log-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ClientLog>> queryPageList(ClientLog clientLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ClientLog> queryWrapper = QueryGenerator.initQueryWrapper(clientLog, req.getParameterMap());
		Page<ClientLog> page = new Page<ClientLog>(pageNo, pageSize);
		//日志关键词
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}
		IPage<ClientLog> pageList = clientLogService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param clientLog
	 * @return
	 */
	@AutoLog(value = "client_log-添加")
	@ApiOperation(value="client_log-添加", notes="client_log-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ClientLog clientLog) {
		log.info("转换前的参数：{}", JSON.toJSONString(clientLog));
		// 坐标转换
		// 纬度
		BigDecimal latitude = clientLog.getLatitude();
		// 经度
		BigDecimal longitude = clientLog.getLongitude();
		if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
			double[] bd09 = CoordinateTransformUtil.wgs84tobd09(longitude.doubleValue(), latitude.doubleValue());
			if (Objects.nonNull(bd09)) {
				clientLog.setLongitude(BigDecimal.valueOf(bd09[0]));
				clientLog.setLatitude(BigDecimal.valueOf(bd09[1]));
			}
		}
		log.info("转换前的参数：{}", JSON.toJSONString(clientLog));
		clientLogService.save(clientLog);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param clientLog
	 * @return
	 */
	@AutoLog(value = "client_log-编辑")
	@ApiOperation(value="client_log-编辑", notes="client_log-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ClientLog clientLog) {
		log.info("转换前的参数：{}", JSON.toJSONString(clientLog));
		// 坐标转换
		// 纬度
		BigDecimal latitude = clientLog.getLatitude();
		// 经度
		BigDecimal longitude = clientLog.getLongitude();
		if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
			double[] bd09 = CoordinateTransformUtil.wgs84tobd09(longitude.doubleValue(), latitude.doubleValue());
			if (Objects.nonNull(bd09)) {
				clientLog.setLongitude(BigDecimal.valueOf(bd09[0]));
				clientLog.setLatitude(BigDecimal.valueOf(bd09[1]));
			}
		}
		log.info("转换前的参数：{}", JSON.toJSONString(clientLog));
		clientLogService.updateById(clientLog);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "client_log-通过id删除")
	@ApiOperation(value="client_log-通过id删除", notes="client_log-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		clientLogService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "client_log-批量删除")
	@ApiOperation(value="client_log-批量删除", notes="client_log-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.clientLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "client_log-通过id查询")
	@ApiOperation(value="client_log-通过id查询", notes="client_log-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ClientLog> queryById(@RequestParam(name="id",required=true) String id) {
		ClientLog clientLog = clientLogService.getById(id);
		if(clientLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(clientLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param clientLog
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ClientLog clientLog) {
        return super.exportXls(request, clientLog, ClientLog.class, "client_log");
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
        return super.importExcel(request, response, ClientLog.class);
    }

}
