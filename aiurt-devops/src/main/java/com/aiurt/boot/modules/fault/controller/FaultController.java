package com.aiurt.boot.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.FaultDeviceResult;
import com.aiurt.boot.common.result.FaultNumResult;
import com.aiurt.boot.common.result.FaultResult;
import com.aiurt.boot.common.result.TimeOutFaultNum;
import com.aiurt.boot.modules.fault.dto.FaultDTO;
import com.aiurt.boot.modules.fault.entity.Fault;
import com.aiurt.boot.modules.fault.entity.FaultEnclosure;
import com.aiurt.boot.modules.fault.entity.OperationProcess;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.param.FaultParam;
import com.aiurt.boot.modules.fault.service.IFaultEnclosureService;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.fault.service.IOperationProcessService;
import com.aiurt.boot.modules.fault.utils.ExportUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "故障表")
@RestController
@RequestMapping("/fault/fault")
public class FaultController {
	@Resource
	private IFaultService faultService;

	@Resource
	private IFaultEnclosureService faultEnclosureService;

	@Resource
	private IOperationProcessService operationProcessService;

	/**
	 * 分页列表查询   故障列表
	 * @param pageNo
	 * @param pageSize
	 * @param param
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障表-分页列表查询")
	@ApiOperation(value = "故障表-分页列表查询", notes = "故障表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultResult>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                                FaultParam param, HttpServletRequest req) {
		Result<IPage<FaultResult>> result = new Result<IPage<FaultResult>>();
		Page<FaultResult> page = new Page<>(pageNo, pageSize);
		IPage<FaultResult> pageList = faultService.pageList(page, param, req);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加  故障登记
	 *
	 * @param fault
	 * @return
	 */
	@AutoLog(value = "故障表-添加")
	@ApiOperation(value = "故障表-添加", notes = "故障表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@Valid @RequestBody FaultDTO fault, HttpServletRequest req) {
		try {
			return faultService.add(fault, req);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("添加失败,原因:"+e.getMessage());
		}
	}

	/**
	 * 编辑
	 *
	 * @param fault
	 * @return
	 */
	@AutoLog(value = "故障表-编辑")
	@ApiOperation(value = "故障表-编辑", notes = "故障表-编辑")
	@PutMapping(value = "/edit")
	public Result<Fault> edit(@RequestBody Fault fault) {
		Result<Fault> result = new Result<Fault>();
		Fault faultEntity = faultService.getById(fault.getId());
		if (faultEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = faultService.updateById(fault);
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障表-通过id删除")
	@ApiOperation(value = "故障表-通过id删除", notes = "故障表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			Fault byId = faultService.getById(id);
			if (byId==null){
				return  Result.error("删除失败,此数据不存在或已被删除");
			}
			if (CommonConstant.STATUS_NORMAL.equals(byId.getStatus())
					&& CommonConstant.STATUS_NORMAL.equals(byId.getDelFlag())
					&& CommonConstant.STATUS_NORMAL.equals(byId.getAssignStatus())
			) {
				faultService.removeById(id);
				faultEnclosureService.lambdaUpdate().eq(FaultEnclosure::getCode,byId.getCode()).remove();
				operationProcessService.lambdaUpdate().eq(OperationProcess::getFaultCode,byId.getCode()).remove();
			}else {
				return Result.error("删除失败! 非新登记故障状态下不允许删除.");
			}
		} catch (Exception e) {
			log.error("删除失败", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障表-批量删除")
	@ApiOperation(value = "故障表-批量删除", notes = "故障表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Fault> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<Fault> result = new Result<Fault>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.faultService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障表-通过id查询")
	@ApiOperation(value = "故障表-通过id查询", notes = "故障表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Fault> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<Fault> result = new Result<Fault>();
		Fault fault = faultService.getById(id);
		if (fault == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(fault);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 * @param response
	 * @param param
	 */
	@AutoLog("导出excel")
	@RequestMapping(value = "/exportXls")
	@ApiOperation(value = "导出excel", notes = "导出excel")
	public void exportXls(HttpServletResponse response,FaultParam param) {
		List<FaultResult> pageList = faultService.exportXls(param);
		//导出文件名称
		XSSFWorkbook wb = ExportUtils.generateTaskBook(pageList, "http://116.62.143.85/#/fault/list");
		ExportUtils.exportResponse(response, "故障列表.xlsx");
		try {
			ServletOutputStream os = response.getOutputStream();
			wb.write(os);
			os.close();
		} catch (IOException e) {
			throw new SwscException("导出错误,请稍后重试");
		}

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
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<Fault> listFaults = ExcelImportUtil.importExcel(file.getInputStream(), Fault.class, params);
				faultService.saveBatch(listFaults);
				return Result.ok("文件导入成功！数据行数:" + listFaults.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.ok("文件导入失败！");
	}

	/**
	 * 挂起  挂起后不可以指派
	 *
	 * @param id
	 * @param remark
	 * @return
	 */
	@AutoLog(value = "挂起")
	@ApiOperation(value = "挂起", notes = "挂起")
	@GetMapping("/hang")
	public Result hang(@RequestParam(name = "id", required = true) Integer id, @RequestParam(name = "remark", required = true) String remark) {
		try {
			faultService.hangById(id, remark);
		}catch (Exception e) {
			return Result.error("挂起失败："+e.getMessage());
		}
		return Result.ok("挂起成功");
	}

	/**
	 * 取消挂起  取消挂起后可以指派
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "取消挂起")
	@ApiOperation(value = "取消挂起", notes = "取消挂起")
	@GetMapping("/cancelHang")
	public Result cancelHang(@RequestParam(name = "id", required = true) Integer id) {
		try {
			faultService.cancelHang(id);
		}catch (Exception e) {
			return Result.error("取消挂起失败："+e.getMessage());
		}
		return Result.ok("取消挂起成功");
	}

	/**
	 * 报表统计故障数量
	 * @return
	 */
	@AutoLog(value = "报表统计故障数量")
	@ApiOperation(value = "报表统计故障数量", notes = "报表统计故障数量")
	@GetMapping("/getFaultNum")
    public Result<FaultNumResult> getFaultNum(@RequestParam(name = "dayStart", required = false) String startTime, @RequestParam(name = "dayEnd", required = false) String endTime) {
		Result<FaultNumResult> faultNum = faultService.getFaultNum(startTime,endTime);
		return faultNum;
	}

	/**
	 * 报表统计超时故障数量
	 * @return
	 */
	@AutoLog(value = "报表统计超时故障数量")
	@ApiOperation(value = "报表统计超时故障数量", notes = "报表统计超时故障数量")
	@GetMapping("/getTimeOutFaultNum")
	public Result<TimeOutFaultNum> getTimeOutFaultNum(@RequestParam(name = "startTime", required = false) String startTime,@RequestParam(name = "endTime", required = false) String endTime) {
		Result<TimeOutFaultNum> faultNum = faultService.getTimeOutFaultNum(startTime,endTime);
		return faultNum;
	}

	/**
	 * pc首页报表统计超时挂起数量
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@AutoLog(value = "pc首页报表统计超时挂起数量")
	@ApiOperation(value = "pc首页报表统计超时挂起数量", notes = "pc首页报表统计超时挂起数量")
	@GetMapping("/getTimeOutHangNum")
	public Result<TimeOutFaultNum> getTimeOutHangNum (@RequestParam(name = "startTime", required = false) String startTime,@RequestParam(name = "endTime", required = false) String endTime) {
		Result<TimeOutFaultNum> timeOutHangNum = faultService.getTimeOutHangNum(startTime, endTime);
		return timeOutHangNum;
	}

	/**
	 * 根据设备编号查询故障信息
	 * @param code
	 * @param param
	 * @return
	 */
	@AutoLog(value = "根据设备编号查询故障信息")
	@ApiOperation(value = "根据设备编号查询故障信息", notes = "根据设备编号查询故障信息")
	@GetMapping("/getFaultDeviceDetail")
	public Result<IPage<FaultDeviceResult>> getFaultDeviceDetail(@RequestParam(name = "code", required = true) String code,
																 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
																 FaultDeviceParam param) {
		Page<FaultDeviceResult> page = new Page<>(pageNo, pageSize);
		IPage<FaultDeviceResult> faultDeviceDetail = faultService.getFaultDeviceDetail(page,code,param);
		return Result.ok(faultDeviceDetail);
	}

	/**
	 * 根据故障编号查询故障现象
	 * @param code
	 * @return
	 */
	@AutoLog(value = "根据故障编号查询故障现象")
	@ApiOperation(value = "根据故障编号查询故障现象", notes = "根据故障编号查询故障现象")
	@GetMapping("/getFaultPhenomenonByCode")
	public Result<String> getFaultPhenomenonByCode(@RequestParam(name = "code", required = false) String code) {
		Fault fault = faultService.getOne(new QueryWrapper<Fault>().eq(Fault.CODE, code), false);
		if (StringUtils.isBlank(fault.getFaultPhenomenon())) {
			throw new SwscException("该故障没有详情");
		} else {
			String faultPhenomenon = fault.getFaultPhenomenon();
			return Result.ok(faultPhenomenon);
		}
	}
}
