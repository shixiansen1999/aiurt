package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.EnumTypeVO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialBaseResult;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.MaterialBaseParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags="物资基础信息")
@RestController
@RequestMapping("/secondLevelWarehouse/materialBase")
public class MaterialBaseController {
	@Resource
	private IMaterialBaseService materialBaseService;

	@Resource
	private MaterialBaseMapper materialBaseMapper;

	 /**
	  * 物资类型下拉列表-查询
	  * @return
	  */
	 @AutoLog(value = "物资类型下拉列表-查询")
	 @ApiOperation(value="物资类型下拉列表-查询", notes="物资类型下拉列表-查询")
	 @GetMapping(value = "/materialTypeList")
	 public Result<List<EnumTypeVO>> materialTypeList() {
		 Result<List<EnumTypeVO>> result = new Result<>();
		 List<EnumTypeVO> enumTypeVOS = new ArrayList<>();
		 for (MaterialTypeEnum value : MaterialTypeEnum.values()) {
			 EnumTypeVO enumTypeVO = new EnumTypeVO();
			 enumTypeVO.setCode(value.getCode());
			 enumTypeVO.setName(value.getName());
			 enumTypeVOS.add(enumTypeVO);
		 }
		 result.setSuccess(true);
		 result.setResult(enumTypeVOS);
		 return result;
	 }

	/**
	 * 分页列表查询
	 * @param param
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "物资基础信息-分页列表查询")
	@ApiOperation(value="物资基础信息-分页列表查询", notes="物资基础信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MaterialBaseResult>> queryPageList(MaterialBaseParam param,
													 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<MaterialBaseResult>> result = new Result<>();
		IPage<MaterialBaseResult> page = new Page<>(pageNo,pageSize);
		IPage<MaterialBaseResult> materialBases = materialBaseService.pageList(page,param);
		result.setSuccess(true);
		result.setResult(materialBases);
		return result;
	}

	@ApiOperation("根据物料编号查询物料类型")
	@GetMapping("/getTypeByMaterialCode")
	public Integer getTypeByMaterialCode(@RequestParam("code") String code){
		Integer type = materialBaseService.getTypeByMaterialCode(code);
		return type;
	}

	/**
	  *   添加
	 * @param materialBase
	 * @return
	 */
	@AutoLog(value = "物资基础信息-添加")
	@ApiOperation(value="物资基础信息-添加", notes="物资基础信息-添加")
	@PostMapping(value = "/add")
	public Result<MaterialBase> add(@Valid @RequestBody MaterialBase materialBase,
									HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Result<MaterialBase> result = new Result<MaterialBase>();
		List<MaterialBase> code = materialBaseMapper.selectList(new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, materialBase.getCode()));
		if (ObjectUtil.isNotEmpty(code)) {
			throw new SwscException("操作失败,该物资编号已存在，请重新输入！");
		}
		try {
			materialBase.setCreateBy(user.getId());
			materialBaseService.save(materialBase);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败: "+e.getMessage());
		}
		return result;
	}




	 /**
	  * 下载物资基础信息模板
	  *
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	 @AutoLog(value = "下载物资基础信息模板")
	 @ApiOperation(value = "下载物资基础信息模板", notes = "下载物资基础信息模板")
	 @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	 public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {

		 ClassPathResource classPathResource =  new ClassPathResource("template/materialBaseExport.xlsx");
		 System.out.println(classPathResource.getClassLoader());
		 System.out.println(classPathResource.getPath());
		 System.out.println(classPathResource.getURL());
		 InputStream bis = classPathResource.getInputStream();
		 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		 int len = 0;
		 while ((len = bis.read()) != -1) {
			 out.write(len);
			 out.flush();
		 }
		 out.close();
	 }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @ApiOperation(value="通过excel导入数据", notes="")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		 Result<?> result = materialBaseService.importExcel(request, response);
		 return result;

	 }
	/**
	  *  编辑
	 * @param materialBase
	 * @return
	 */
	@AutoLog(value = "物资基础信息-编辑")
	@ApiOperation(value="物资基础信息-编辑", notes="物资基础信息-编辑")
	@PutMapping(value = "/edit")
	public Result<MaterialBase> edit(@RequestBody MaterialBase materialBase,
									 HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Result<MaterialBase> result = new Result<MaterialBase>();
		if(materialBase.getId()==null){
			return result.error500("id不能为空");
		}
		MaterialBase materialBaseEntity = materialBaseService.getById(materialBase.getId());
		if(materialBaseEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			UpdateWrapper<MaterialBase> wrapper = new UpdateWrapper<>();
			if(materialBase.getPrice()==null){
				wrapper.set("price",null);
			}
			wrapper.set("update_by",user.getId());
			wrapper.eq("id",materialBase.getId());
			boolean b = materialBaseService.update(materialBase, wrapper);
			if(b) {
				result.success("修改成功!");
			}else{
				result.error500("修改失败");
			}
		}

		return result;
	}

	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资基础信息-通过id删除")
	@ApiOperation(value="物资基础信息-通过id删除", notes="物资基础信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			materialBaseService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}



}
