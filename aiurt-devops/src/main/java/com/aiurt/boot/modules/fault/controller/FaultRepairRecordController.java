package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.result.FaultRepairRecordResult;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.fault.dto.FaultRepairDTO;
import com.aiurt.boot.modules.fault.dto.FaultRepairRecordDTO;
import com.aiurt.boot.modules.fault.entity.FaultRepairRecord;
import com.aiurt.boot.modules.fault.param.AssignParam;
import com.aiurt.boot.modules.fault.service.IFaultRepairRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "故障维修记录表")
@RestController
@RequestMapping("/fault/faultRepairRecord")
public class FaultRepairRecordController {
	@Autowired
	private IFaultRepairRecordService faultRepairRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param faultRepairRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障维修记录表-分页列表查询")
	@ApiOperation(value = "故障维修记录表-分页列表查询", notes = "故障维修记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultRepairRecord>> queryPageList(FaultRepairRecord faultRepairRecord,
	                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                                      HttpServletRequest req) {
		Result<IPage<FaultRepairRecord>> result = new Result<IPage<FaultRepairRecord>>();
		QueryWrapper<FaultRepairRecord> queryWrapper = QueryGenerator.initQueryWrapper(faultRepairRecord, req.getParameterMap());
		Page<FaultRepairRecord> page = new Page<FaultRepairRecord>(pageNo, pageSize);
		IPage<FaultRepairRecord> pageList = faultRepairRecordService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param faultRepairRecord
	 * @return
	 */
	@AutoLog(value = "故障维修记录表-添加")
	@ApiOperation(value = "故障维修记录表-添加", notes = "故障维修记录表-添加")
	@PostMapping(value = "/add")
	public Result<FaultRepairRecord> add(@RequestBody FaultRepairRecord faultRepairRecord) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		try {
			faultRepairRecordService.save(faultRepairRecord);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param faultRepairRecord
	 * @return
	 */
	@AutoLog(value = "故障维修记录表-编辑")
	@ApiOperation(value = "故障维修记录表-编辑", notes = "故障维修记录表-编辑")
	@PutMapping(value = "/edit")
	public Result<FaultRepairRecord> edit(@RequestBody FaultRepairRecord faultRepairRecord) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		FaultRepairRecord faultRepairRecordEntity = faultRepairRecordService.getById(faultRepairRecord.getId());
		if (faultRepairRecordEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = faultRepairRecordService.updateById(faultRepairRecord);

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
	@AutoLog(value = "故障维修记录表-通过id删除")
	@ApiOperation(value = "故障维修记录表-通过id删除", notes = "故障维修记录表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			faultRepairRecordService.removeById(id);
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
	@AutoLog(value = "故障维修记录表-批量删除")
	@ApiOperation(value = "故障维修记录表-批量删除", notes = "故障维修记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<FaultRepairRecord> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.faultRepairRecordService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "故障维修记录表-通过id查询")
	@ApiOperation(value = "故障维修记录表-通过id查询", notes = "故障维修记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultRepairRecord> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		FaultRepairRecord faultRepairRecord = faultRepairRecordService.getById(id);
		if (faultRepairRecord == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(faultRepairRecord);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<FaultRepairRecord> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				FaultRepairRecord faultRepairRecord = JSON.parseObject(deString, FaultRepairRecord.class);
				queryWrapper = QueryGenerator.initQueryWrapper(faultRepairRecord, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<FaultRepairRecord> pageList = faultRepairRecordService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "故障维修记录表列表");
		mv.addObject(NormalExcelConstants.CLASS, FaultRepairRecord.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障维修记录表列表数据", "导出人:Jeecg", "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
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
				List<FaultRepairRecord> listFaultRepairRecords = ExcelImportUtil.importExcel(file.getInputStream(), FaultRepairRecord.class, params);
				faultRepairRecordService.saveBatch(listFaultRepairRecords);
				return Result.ok("文件导入成功！数据行数:" + listFaultRepairRecords.size());
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
	 * 指派
	 * @param param
	 * @return
	 */
	@AutoLog(value = "指派")
	@ApiOperation(value = "指派", notes = "指派")
	@PostMapping(value = "/assign")
	public Result<FaultRepairRecord> assign(@RequestBody AssignParam param) {
		Result<FaultRepairRecord> result = new Result<>();
		try {
			faultRepairRecordService.assign(param);
			result.setMessage("指派成功");
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("指派失败："+e.getMessage());
		}
		return result;
	}

	/**
	 * 重新指派
	 * @param param
	 * @return
	 */
	@AutoLog(value = "重新指派")
	@ApiOperation(value = "重新指派", notes = "重新指派")
	@PostMapping(value = "/assignAgain")
	public Result<FaultRepairRecord> assignAgain(@RequestBody AssignParam param) {
		Result<FaultRepairRecord> result = new Result<>();
		try {
			faultRepairRecordService.assignAgain(param);
			result.setMessage("重新指派成功");
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("重新指派失败："+e.getMessage());
		}
		return result;
	}



	/**
	 * 指派
	 *
	 * @return
	 */
	@AutoLog(value = "指派")
	@ApiOperation(value = "指派", notes = "指派")
	@PostMapping(value = "/assignPost")
	public Result<FaultRepairRecord> assignPost(@RequestBody AssignParam param) {
		Result<FaultRepairRecord> result = new Result<>();
		faultRepairRecordService.assign(param);
		return result;
	}


	/**
	 * app填写维修记录
	 *
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "app填写维修记录")
	@ApiOperation(value = "app填写维修记录", notes = "app填写维修记录")
	@PostMapping(value = "/addRecord")
	public Result<FaultRepairRecord> addRecord(@Valid @RequestBody FaultRepairRecordDTO dto,HttpServletRequest req) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		try {
			faultRepairRecordService.addRecord(dto,req);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500(e.getMessage());
		}
		return result;
	}

	/**
	 * 查询当前登录人最后一条维修记录
	 * @param code
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询当前登录人最后一条维修记录")
	@ApiOperation(value = "查询当前登录人最后一条维修记录", notes = "查询当前登录人最后一条维修记录")
	@GetMapping(value = "/getDetail")
	public Result<?> getDetail(@RequestParam(name="code",required=true) String code,HttpServletRequest req) {
			FaultRepairRecordResult detail = faultRepairRecordService.getDetail(code, req);
			return Result.ok(detail);
	}
	/**
	 * 编辑维修记录
	 *
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "编辑维修记录")
	@ApiOperation(value = "编辑维修记录", notes = "编辑维修记录")
	@PostMapping(value = "/editRecord")
	public Result<FaultRepairRecord> editRecord(@Valid @RequestBody FaultRepairDTO dto) {
		Result<FaultRepairRecord> result = new Result<FaultRepairRecord>();
		try {
			faultRepairRecordService.editRecord(dto);
			result.success("修改成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500(e.getMessage());
		}
		return result;
	}
}
