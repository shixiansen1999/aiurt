package com.aiurt.modules.manufactor.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.entity.vo.CsMajorImportVO;
import com.aiurt.modules.manufactor.entity.CsManuFactorImportVo;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


/**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-厂商信息")
@RestController
@RequestMapping("/manufactor")
@Slf4j
public class CsManufactorController extends BaseController<CsManufactor,ICsManufactorService> {
	@Value("${jeecg.path.upload}")
	private String upLoadPath;
	@Autowired
	private ICsManufactorService csManufactorService;
	@Autowired
	private IDeviceService deviceService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	/**
	 * 分页列表查询
	 *
	 * @param csManufactor
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "厂商分页列表查询",permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息分页列表查询", notes="厂商信息分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsManufactor csManufactor,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsManufactor> queryWrapper = QueryGenerator.initQueryWrapper(csManufactor, req.getParameterMap());
		Page<CsManufactor> page = new Page<CsManufactor>(pageNo, pageSize);
		IPage<CsManufactor> pageList = csManufactorService.page(page, queryWrapper.lambda().eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0));
		return Result.OK(pageList);
	}
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "厂商不分页列表查询",permissionUrl = "/manufactor/list")
	 @ApiOperation(value="厂商信息列表查询", notes="厂商信息列表查询")
	 @GetMapping(value = "/selectList")
	 public Result<?> selectList(CsManufactor csManufactor,
									HttpServletRequest req) {
		 QueryWrapper<CsManufactor> queryWrapper = QueryGenerator.initQueryWrapper(csManufactor, req.getParameterMap());
		 List<CsManufactor> pageList = csManufactorService.list(queryWrapper.lambda().eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0));
		 return Result.OK(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param csManufactor
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加厂商信息添加",permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息添加", notes="厂商信息添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsManufactor csManufactor) {
		return csManufactorService.add(csManufactor);
	}

	/**
	 *  编辑
	 *
	 * @param csManufactor
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑厂商信息",permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息编辑", notes="厂商信息编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsManufactor csManufactor) {
		return csManufactorService.update(csManufactor);
	}

	/**
	 *  通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "厂商信息通过id删除",permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息通过id删除", notes="厂商信息通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsManufactor csManufactor = csManufactorService.getById(id);
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getManufactorCode,csManufactor.getCode());
		deviceWrapper.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		//判断物资主数据是否使用
		LambdaQueryWrapper<MaterialBase> materWrapper =  new LambdaQueryWrapper<MaterialBase>();
		materWrapper.eq(MaterialBase::getManufactorCode,csManufactor.getCode());
		materWrapper.eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<MaterialBase> materList = materialBaseService.list(materWrapper);
		if(!materList.isEmpty()){
			return Result.error("该位置信息被物资主数据使用中，无法删除");
		}
		csManufactor.setDelFlag(CommonConstant.DEL_FLAG_1);
		csManufactorService.updateById(csManufactor);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询厂商",permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息通过id查询", notes="厂商信息通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsManufactor csManufactor = csManufactorService.getById(id);
		if(csManufactor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csManufactor);
	}

	/**
	 * 厂商信息导出
	 * @param request
	 * @param csManufactor
	 * @return
	 */
	@AutoLog(value = "厂商信息-厂商信息分页列表-导出excel", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息-导出excel", notes="厂商信息-导出excel")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, CsManufactor csManufactor) {
		return super.exportXls(request, csManufactor, CsManufactor.class, "厂商信息");
	}

	/**
	 * 厂商信息导入
	 * @param request
	 * @param response
	 * @return
	 */
	@AutoLog(value = "厂商信息-厂商信息分页列表-通过excel导入数据", operateType =  6, operateTypeAlias = "通过excel导入数据", permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息-通过excel导入数据", notes="厂商信息-通过excel导入数据")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		// 错误信息
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0, errorLines = 0;

		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<CsManuFactorImportVo> csList = ExcelImportUtil.importExcel(file.getInputStream(), CsManuFactorImportVo.class, params);
				List<CsManuFactorImportVo> csManuFactorList = csList.stream().filter(item -> item.getName() != null).filter(item -> item.getLevel() != null).collect(Collectors.toList());

				List<CsManufactor> list = new ArrayList<>();
				for (int i = 0; i < csManuFactorList.size(); i++) {
					CsManuFactorImportVo csManuFactorImportVo = csManuFactorList.get(i);
					if (ObjectUtil.isNull(csManuFactorImportVo.getName())) {
						errorMessage.add("厂商名称为必填项，忽略导入");
						errorLines++;
					}else {
						CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().lambda().eq(CsManufactor::getCode, csManuFactorImportVo.getCode()).eq(CsManufactor::getDelFlag, 0));
						if (csManufactor != null) {
							errorMessage.add(csManuFactorImportVo.getCode() + "厂商编码已经存在，忽略导入");
							errorLines++;
						}
					}
					if (ObjectUtil.isNull(csManuFactorImportVo.getLevel())) {
						errorMessage.add("厂商等级为必填项，忽略导入");
						errorLines++;
					}
					CsManufactor csManufactor = new CsManufactor();
					BeanUtils.copyProperties(csManuFactorImportVo, csManufactor);
					list.add(csManufactor);
					successLines++;
				}
				if(errorLines==0)
				{
					csManufactorService.saveBatch(list);
				}
				else
				{
					successLines =0;
				}
			} catch (Exception e) {
				errorMessage.add("发生异常：" + e.getMessage());
				log.error(e.getMessage(), e);
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorMessage);
	}


	/**
	 * 厂商信息导入模板下载
	 * @return
	 */
	@AutoLog(value = "厂商信息导入模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/manufactor/list")
	@ApiOperation(value="厂商信息导入模板下载", notes="厂商信息导入模板下载")
	@RequestMapping(value = "/exportTemplateXls")
	public ModelAndView exportTemplateXl() {
		return super.exportTemplateXls("", CsManufactor.class,"厂商信息","");
	}

}
