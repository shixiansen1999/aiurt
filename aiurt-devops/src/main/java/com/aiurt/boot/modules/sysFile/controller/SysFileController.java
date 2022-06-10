package com.aiurt.boot.modules.sysFile.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.sysFile.entity.SysFile;
import com.aiurt.boot.modules.sysFile.entity.SysFileType;
import com.aiurt.boot.modules.sysFile.param.FileAppParam;
import com.aiurt.boot.modules.sysFile.param.SysFileWebParam;
import com.aiurt.boot.modules.sysFile.service.ISysFileRoleService;
import com.aiurt.boot.modules.sysFile.service.ISysFileService;
import com.aiurt.boot.modules.sysFile.service.ISysFileTypeService;
import com.aiurt.boot.modules.sysFile.vo.FIlePlanVO;
import com.aiurt.boot.modules.sysFile.vo.FileAppVO;
import com.aiurt.boot.modules.sysFile.vo.SysFileVO;
import com.aiurt.boot.modules.sysFile.vo.TypeNameVO;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 文档表
 * @Author: qian
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "文档表")
@RestController
@RequestMapping("/sysfile/sysFile")
public class SysFileController {
	@Autowired
	private ISysFileService sysFileService;
	@Autowired
	private ISysFileRoleService iSysFileRoleService;
	@Autowired
	private ISysFileTypeService sysFileTypeService;
	@Autowired
	private ISysUserService sysUserService;

	/**
	 * 分页列表查询
	 *
	 * @param sysFile
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "文档表-分页列表查询")
	@ApiOperation(value = "文档表-分页列表查询", notes = "文档表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SysFileVO>> queryPageList(SysFileWebParam sysFile,
	                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                              HttpServletRequest request) {
		//处理未选择类型数据
		if (sysFile.getTypeId()==null){
			return Result.ok(new Page<>());
		}

		Result<IPage<SysFileVO>> result = new Result<IPage<SysFileVO>>();

		String userId = ((LoginUser)SecurityUtils.getSubject().getPrincipal()).getId();

		//查询条件拼接
		LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<SysFile>()
				.orderByDesc(SysFile::getId)
				.eq(SysFile::getDelFlag, 0);
		if (sysFile.getTypeId() != null) {
			List<Long> list = iSysFileRoleService.queryRoleByUserId(userId, sysFile.getTypeId());
			queryWrapper.in(SysFile::getTypeId, list);
		}

		if (StringUtils.isNotBlank(sysFile.getName())) {
			queryWrapper.like(SysFile::getName, sysFile.getName());
		}

		if (sysFile.getStartTime() != null) {
			queryWrapper.ge(SysFile::getCreateTime,
					sysFile.getStartTime().atTime(0, 0, 0));
		}

		if (sysFile.getEndTime() != null) {
			queryWrapper.le(SysFile::getCreateTime,
					sysFile.getEndTime().atTime(23, 59, 59));
		}
		if (StringUtils.isNotBlank(sysFile.getType())){
			queryWrapper.eq(SysFile::getType,sysFile.getType());
		}

		if (StringUtils.isNotBlank(sysFile.getCreateByName())) {
			List<SysUser> list = sysUserService.list(new LambdaQueryWrapper<SysUser>()
					.like(SysUser::getRealname, sysFile.getCreateByName())
					.select(SysUser::getId)
					.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));
			if (CollectionUtils.isNotEmpty(list)) {
				List<String> collect = list.stream().map(SysUser::getId).collect(Collectors.toList());
				queryWrapper.in(SysFile::getCreateBy, collect);
			} else {
				queryWrapper.eq(SysFile::getId, -1);
			}
		}

		IPage<SysFile> pageList = sysFileService.page(new Page<>(pageNo, pageSize), queryWrapper);
		Map<Long, SysFileType> map = null;
		Map<String, SysUser> userMap = null;

		if (CollectionUtils.isNotEmpty(pageList.getRecords())) {
			//获取类型
			List<Long> list = pageList.getRecords().stream().map(SysFile::getTypeId).collect(Collectors.toList());
			Collection<SysFileType> types = sysFileTypeService.listByIds(list);
			map = types.stream().collect(Collectors.toMap(SysFileType::getId, s -> s));
			//获取用户
			List<String> userIds = pageList.getRecords().stream().map(SysFile::getCreateBy).collect(Collectors.toList());
			Collection<SysUser> users = sysUserService.listByIds(userIds);
			userMap = users.stream().collect(Collectors.toMap(SysUser::getId, s -> s));
		}
		IPage<SysFileVO> pages = new Page<>();

		BeanUtils.copyProperties(pageList, pages);
		List<SysFileVO> records = new ArrayList<>();
		//若为此用户文件,给予全部权限
		final Map<Long, SysFileType> finalMap = map;
		final Map<String, SysUser> finalUserMap = userMap;
		pageList.getRecords().forEach(e -> {
			SysFileVO vo = new SysFileVO();
			BeanUtils.copyProperties(e, vo);
			if (e.getCreateBy().equals(userId)) {
				vo.setAllFlag(1);
			} else {
				vo.setAllFlag(0);
			}
			if (finalMap != null) {
				SysFileType sysFileType = finalMap.get(vo.getTypeId());
				if (sysFileType != null) {
					vo.setTypeName(sysFileType.getName());
				}
			}
			if (finalUserMap != null) {
				SysUser user = finalUserMap.get(vo.getCreateBy());
				if (user != null) {
					vo.setCreateByName(user.getRealname());
				}
			}
			records.add(vo);
		});
		pages.setRecords(records);

		result.setSuccess(true);
		result.setResult(pages);
		return result;
	}

	/**
	 * app分页列表查询
	 *
	 * @return
	 */
	@AutoLog(value = "文档表-app分页列表查询")
	@ApiOperation(value = "文档表-app分页列表查询", notes = "文档表-分页列表查询")
	@GetMapping(value = "/queryAppPage")
	public Result<IPage<FileAppVO>> queryAppPage(FileAppParam param) {
		Result<IPage<FileAppVO>> result = new Result<IPage<FileAppVO>>();
		result.setSuccess(true);
		result.setResult(sysFileService.selectAppList(param));
		return result;
	}


	/**
	 * 培训文档分页列表查询
	 *
	 * @return
	 */
	@AutoLog(value = "文档表-培训文档列表查询")
	@ApiOperation(value = "文档表-培训文档列表查询", notes = "文档表-培训文档列表查询")
	@GetMapping(value = "/queryList")
	public Result<List<FIlePlanVO>> queryList() {
		Result<List<FIlePlanVO>> result = new Result<>();
		result.setSuccess(true);
		result.setResult(sysFileService.selectList());
		return result;
	}

	/**
	 * 增加文件下载次数
	 *
	 * @return
	 */
	@AutoLog(value = "增加文件下载次数")
	@ApiOperation(value = "增加文件下载次数", notes = "增加文件下载次数")
	@PostMapping(value = "/addCount")
	public Result<?> addCount(@RequestParam("id") Long id) {
		SysFile byId = this.sysFileService.getById(id);
		byId.setDownSize(byId.getDownSize() == null ? 1 : byId.getDownSize() + 1);
		boolean b = this.sysFileService.updateById(byId);
		if (!b) {
			return Result.error("更改次数失败");
		}
		return Result.ok();
	}


	/**
	 * 添加
	 *
	 * @param sysFile
	 * @return
	 */
	@AutoLog(value = "文档表-添加")
	@ApiOperation(value = "文档表-添加", notes = "文档表-添加")
	@PostMapping(value = "/add")
	public Result<SysFile> add(@RequestBody SysFile sysFile) {
		Result<SysFile> result = new Result<SysFile>();
		if (StringUtils.isNotBlank(sysFile.getName())) {
			String name = sysFile.getName();
			//处理文件名
			String prefix = name.substring(0, name.indexOf(PatrolConstant.NO_SPL));
			sysFile.setName(StringUtils.isNotBlank(prefix) ? prefix : "未知文件名");
			//处理后缀
			String substring = name.substring(name.lastIndexOf(PatrolConstant.NO_SPL));
			if (StringUtils.isNotBlank(substring)) {
				String suffix = substring.replaceFirst(PatrolConstant.NO_SPL, "");
				if (StringUtils.isNotBlank(suffix)) {
					sysFile.setType(suffix.toUpperCase());
				}
			} else {
				sysFile.setType("未知类型");
			}
		}

		//URL url = new URL(sysFile.getUrl());
		//URLConnection uc = url.openConnection();
		//String fileName = uc.getHeaderField(6);
		//fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
		//sysFile.setName(fileName);
		//System.out.println("文件名为：" + fileName);
		double length = Double.parseDouble(Optional.ofNullable(sysFile.getFileSize()).orElse("0"));
		String size = ((int) length) + "B";
		for (int i = 0; i < 2; i++) {
			if (length / CommonConstant.FILE_SIZE > CommonConstant.FILE_SIZE_MIN) {
				length = length / CommonConstant.FILE_SIZE;
				if (i == 0) {
					size = length + "";
					size = size.substring(0, size.indexOf(".") + 2).concat("KB");
				} else {
					size = length + "";
					size = size.substring(0, size.indexOf(".") + 2).concat("MB");
				}
			} else {
				break;
			}
		}
		sysFile.setFileSize(size);
		try {
			sysFileService.save(sysFile);
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
	 * @param sysFile
	 * @return
	 */
	@AutoLog(value = "文档表-编辑")
	@ApiOperation(value = "文档表-编辑", notes = "文档表-编辑")
	@PutMapping(value = "/edit")
	public Result<SysFile> edit(@RequestBody SysFile sysFile) {
		Result<SysFile> result = new Result<SysFile>();
		SysFile sysFileEntity = sysFileService.getById(sysFile.getId());
		if (sysFileEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			//处理文件名
			if (StringUtils.isNotBlank(sysFile.getName()) && !sysFileEntity.getName().equals(sysFile.getName())) {
				String name = sysFile.getName();
				if (name.contains(PatrolConstant.NO_SPL)) {
					//处理文件名
					String prefix = name.substring(0, name.indexOf(PatrolConstant.NO_SPL));
					sysFile.setName(StringUtils.isNotBlank(prefix) ? prefix : "未知文件名");
					//处理后缀
					String substring = name.substring(name.lastIndexOf(PatrolConstant.NO_SPL));
					if (StringUtils.isNotBlank(substring)) {
						String suffix = substring.replaceFirst(PatrolConstant.NO_SPL, "");
						if (StringUtils.isNotBlank(suffix)) {
							sysFile.setType(suffix.toUpperCase());
						}
					} else {
						sysFile.setType("未知类型");
					}
				}
			}
			//拼接
			if (sysFile.getFileSize() != null && !sysFileEntity.getFileSize().equals(sysFile.getFileSize())) {
				double length = Double.parseDouble(Optional.ofNullable(sysFile.getFileSize()).orElse("0"));
				String size = ((int) length) + "B";
				for (int i = 0; i < 2; i++) {
					if (length / CommonConstant.FILE_SIZE > CommonConstant.FILE_SIZE_MIN) {
						length = length / CommonConstant.FILE_SIZE;
						if (i == 0) {
							size = length + "";
							size = size.substring(0, size.indexOf(".") + 2).concat("KB");
						} else {
							size = length + "";
							size = size.substring(0, size.indexOf(".") + 2).concat("MB");
						}
					} else {
						break;
					}
				}
				sysFile.setFileSize(size);
			}
			try {
				sysFileService.updateById(sysFile);
				result.success("修改成功！");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				result.error500("修改失败");
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
	@AutoLog(value = "文档表-通过id删除")
	@ApiOperation(value = "文档表-通过id删除", notes = "文档表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			sysFileService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败,{}", e.getMessage());
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
	@AutoLog(value = "文档表-批量删除")
	@ApiOperation(value = "文档表-批量删除", notes = "文档表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<SysFile> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<SysFile> result = new Result<>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.sysFileService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "文档表-通过id查询")
	@ApiOperation(value = "文档表-通过id查询", notes = "文档表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysFile> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<SysFile> result = new Result<SysFile>();
		SysFile sysFile = sysFileService.getById(id);
		if (sysFile == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(sysFile);
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
		QueryWrapper<SysFile> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				SysFile sysFile = JSON.parseObject(deString, SysFile.class);
				queryWrapper = QueryGenerator.initQueryWrapper(sysFile, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysFile> pageList = sysFileService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "文档表列表");
		mv.addObject(NormalExcelConstants.CLASS, SysFile.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("文档表列表数据", "导出人:Jeecg", "导出信息"));
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
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<SysFile> listSysFiles = ExcelImportUtil.importExcel(file.getInputStream(), SysFile.class, params);
				sysFileService.saveBatch(listSysFiles);
				return Result.ok("文件导入成功！数据行数:" + listSysFiles.size());
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
	 * 查询id类型
	 * 通过id查询
	 *
	 * @param request 请求
	 * @param typeId  id类型
	 * @return {@code Result<List<TypeNameVO>>}
	 */
	@AutoLog(value = "文档表-通过文档类型id(typeId)查询文件类型")
	@ApiOperation(value = "文档表-通过文档类型id(typeId)查询文件类型", notes = "文档表-通过文档类型id(typeId)查询文件类型")
	@GetMapping(value = "/queryByTypeId")
	public Result<List<TypeNameVO>> queryByTypeId(HttpServletRequest request,
	                                              @RequestParam(value = "typeId",required = false) Long typeId) {
		List<TypeNameVO> voList = new ArrayList<>();

		List<SysFile> files = this.sysFileService.query()
				.select(" distinct ".concat(SysFile.TYPE))
				.eq(SysFile.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.eq(typeId != null,SysFile.TYPE_ID, typeId)
				.list();
		if (CollectionUtils.isNotEmpty(files)) {
			for (SysFile file : files) {
				voList.add(new TypeNameVO().setName(file.getType()));
			}
		}
		return Result.ok(voList);
	}
}
