package com.aiurt.modules.sm.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.LimitSubmit;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionTypeMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Api(tags="安全事项")
@RestController
@RequestMapping("/sm/csSafetyAttention")
@Slf4j
public class CsSafetyAttentionController extends BaseController<CsSafetyAttention, ICsSafetyAttentionService> {
	@Autowired
	private ICsSafetyAttentionService csSafetyAttentionService;
	 @Autowired
	 private CsSafetyAttentionTypeMapper csSafetyAttentionTypeMapper;
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	 @Autowired
	 private CsSafetyAttentionMapper csSafetyAttentionMapper;

	/**
	 * 分页列表查询
	 *
	 * @param csSafetyAttention
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "安全事项-分页列表查询")
	@ApiOperation(value="安全事项-分页列表查询", notes="安全事项-分页列表查询")
	@GetMapping(value = "/list")
	//@PermissionData(pageComponent = "/overhaul/SafetyAttentionList")
	public Result<IPage<CsSafetyAttention>> queryPageList(CsSafetyAttention csSafetyAttention,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<CsUserMajorModel> list = sysBaseAPI.getMajorByUserId(sysUser.getId());
		List<CsUserSubsystemModel> list1 = sysBaseAPI.getSubsystemByUserId(sysUser.getId());
		List <String> majorCode =  list.stream().map(s-> s.getMajorCode()).collect(Collectors.toList());
		List <String> majorCode1 = list1.stream().map(s -> s.getMajorCode()).distinct().collect(Collectors.toList());
		List<String> majorCode2 =new ArrayList<>();
		majorCode2 = majorCode.stream()
				.filter((String s) -> !majorCode1.contains(s))
				.collect(Collectors.toList());
		LambdaQueryWrapper<CsSafetyAttention> queryWrapper = new LambdaQueryWrapper();
		Set<String> userRoleSet = sysBaseAPI.getUserRoleSet(sysUser.getUsername());
		queryWrapper.eq(CsSafetyAttention::getDelFlag,0);
		List<String> systemList = list1.stream().map(s-> s.getSystemCode()).collect(Collectors.toList());
		if (StrUtil.isNotEmpty(csSafetyAttention.getMajorCode())){
			queryWrapper.eq(CsSafetyAttention::getMajorCode,csSafetyAttention.getMajorCode());
			if (CollectionUtil.isNotEmpty(userRoleSet)){
				if (!userRoleSet.contains("admin")){
					queryWrapper.in(CsSafetyAttention::getSystemCode,systemList);
				}
			}
		}else {
			if (CollectionUtil.isNotEmpty(majorCode2)){
				List<String> systemCodes = csSafetyAttentionMapper.selectSystemCodes(majorCode2);
				systemList.addAll(systemCodes);
			}
			if (CollectionUtil.isNotEmpty(userRoleSet)){
				if (!userRoleSet.contains("admin")){
					queryWrapper.in(CsSafetyAttention::getSystemCode,systemList);
				}
			}
		}
		if (csSafetyAttention.getState()!=null){
			queryWrapper.eq(CsSafetyAttention::getState,csSafetyAttention.getState());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionMeasures())){
			queryWrapper.like(CsSafetyAttention::getAttentionMeasures,csSafetyAttention.getAttentionMeasures());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionContent())){
			queryWrapper.like(CsSafetyAttention::getAttentionContent,csSafetyAttention.getAttentionContent());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getSystemCode())){
			queryWrapper.eq(CsSafetyAttention::getSystemCode,csSafetyAttention.getSystemCode());
		}
            queryWrapper.orderByDesc(CsSafetyAttention::getCreateTime);
		Page<CsSafetyAttention> page = new Page<CsSafetyAttention>(pageNo, pageSize);
		IPage<CsSafetyAttention> pageList = csSafetyAttentionService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  *  app巡视、检修-安全事项查询
	  *
	  * @param csSafetyAttention
	  * @return
	  */
	 @AutoLog(value = " app巡视、检修-安全事项查询")
	 @ApiOperation(value=" app巡视、检修-安全事项查询", notes=" app巡视、检修-安全事项查询")
	 @GetMapping (value = "/queryList")
	 public Result<IPage<CsSafetyAttention>> queryList(CsSafetyAttention csSafetyAttention,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		 QueryWrapper<CsSafetyAttention> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		 if(ObjectUtil.isNotEmpty(csSafetyAttention.getMajorCode())){
		 	queryWrapper.eq("major_code",csSafetyAttention.getMajorCode());
		 }
		 if(ObjectUtil.isNotEmpty(csSafetyAttention.getSystemCode())){
		 	queryWrapper.eq("system_code",csSafetyAttention.getSystemCode());
		 }
		 if(ObjectUtil.isNotEmpty(csSafetyAttention.getState())){
		 	queryWrapper.eq("state",csSafetyAttention.getState());
		 }
		 if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionMeasures())){
			 queryWrapper.lambda().like(CsSafetyAttention::getAttentionMeasures,csSafetyAttention.getAttentionMeasures());
		 }
		 if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionContent())){
			 queryWrapper.lambda().like(CsSafetyAttention::getAttentionContent,csSafetyAttention.getAttentionContent());
		 }
		 if (StrUtil.isNotEmpty(csSafetyAttention.getIds())){
			 queryWrapper.lambda().in(CsSafetyAttention::getId,Arrays.asList(csSafetyAttention.getIds().split(",")));
		 }
		 queryWrapper.orderByDesc("create_time");
		 Page<CsSafetyAttention> page = new Page<CsSafetyAttention>(pageNo, pageSize);
		 IPage<CsSafetyAttention> pageList = csSafetyAttentionService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }
	 /**
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "根据巡检或者检修code查询数据")
	 @ApiOperation(value="根据巡检或者检修code查询数据", notes="根据巡检或者检修code查询数据")
	 @GetMapping (value = "/isFirstByCode")
	 public Result<List<CsSafetyAttention>> isFirstByCode (@RequestParam(name="code",required=true) String code,
													       @RequestParam(name = "status",required = true) Integer status,
														   @RequestParam(name = "majorCode") String majorCode,
														   @RequestParam(name = "systemCode",required = false)String systemCode) {
		 List<CsSafetyAttention> pageList = csSafetyAttentionService.isFirstByCode(code,status,majorCode,systemCode);
		 return Result.OK(pageList);
	 }
	 /**
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "App分页根据巡检或者检修code查询数据")
	 @ApiOperation(value="根据巡检或者检修code查询数据", notes="根据巡检或者检修code查询数据")
	 @GetMapping (value = "/AppIsFirstByCode")
	 public Result<Page<CsSafetyAttention>> AppIsFirstByCode (@RequestParam(name="code",required=true) String code,
														   @RequestParam(name = "status",required = true) Integer status,
														   @RequestParam(name = "majorCode") String majorCode,
														   @RequestParam(name = "systemCode",required = false)String systemCode,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 List<CsSafetyAttention> pageList = csSafetyAttentionService.isFirstByCode(code,status,majorCode,systemCode);
		 Page<CsSafetyAttention> page = new Page<CsSafetyAttention>(pageNo, pageSize);
		 return Result.OK(page.setRecords(pageList));
	 }
	/**
	 *   添加
	 *
	 * @param csSafetyAttention
	 * @return
	 */
	@AutoLog(value = "安全事项-添加")
	@ApiOperation(value="安全事项-添加", notes="安全事项-添加")
	@PostMapping(value = "/add")
	@LimitSubmit(key = "addAnnualPlan:#id")
	public Result<String> add(@RequestBody CsSafetyAttention csSafetyAttention) {
		csSafetyAttentionService.save(csSafetyAttention);
		return Result.OK("添加成功");
	}

	/**
	 *  编辑
	 *
	 * @param csSafetyAttention
	 * @return
	 */
	@AutoLog(value = "安全事项-编辑")
	@ApiOperation(value="安全事项-编辑", notes="安全事项-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CsSafetyAttention csSafetyAttention) {
		csSafetyAttentionService.updateById(csSafetyAttention);
		return Result.OK("编辑成功!");
	}
	 /**
	  *  修改状态
	  *
	  * @param id
	  * @param state
	  * @return
	  */
	 @AutoLog(value = "安全事项-修改状态")
	 @ApiOperation(value="安全事项-修改状态", notes="安全事项-修改状态")
	 @RequestMapping(value = "/modify", method = {RequestMethod.POST})
	 public Result<String> modify(@RequestParam(name = "id") String id,
								  @RequestParam(name = "status") Integer state) {
	 	CsSafetyAttention csSafetyAttention = new CsSafetyAttention();
	 	csSafetyAttention.setId(id);
	 	if (state==0){
			csSafetyAttention.setState(1);
		}
		 if (state==1){
			 csSafetyAttention.setState(0);
		 }
		 csSafetyAttentionService.updateById(csSafetyAttention);
		 if (state==0) {
			 return Result.OK(" 事项已生效！");
		 } else if (state==1){
			 return Result.OK(" 事项已失效！");
		 }else  {
			 return Result.error("修改失败!");
		 }
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "安全事项-通过id删除")
	@ApiOperation(value="安全事项-通过id删除", notes="安全事项-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		CsSafetyAttention csSafetyAttention = new CsSafetyAttention();
		csSafetyAttention.setId(id);
		csSafetyAttention.setDelFlag(1);
		csSafetyAttentionService.updateById(csSafetyAttention);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "安全事项-批量删除")
	@ApiOperation(value="安全事项-批量删除", notes="安全事项-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> list =Arrays.asList(ids.split(","));
		list.forEach(id->{
			this.delete(id);
		});
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "安全事项-通过id查询")
	@ApiOperation(value="安全事项-通过id查询", notes="安全事项-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsSafetyAttention> queryById(@RequestParam(name="id",required=true) String id) {
		CsSafetyAttention csSafetyAttention = csSafetyAttentionService.getOne(new LambdaQueryWrapper<CsSafetyAttention>()
		                                                                      .eq(CsSafetyAttention::getId,id)
		                                                                      .eq(CsSafetyAttention::getDelFlag,0));
		if(csSafetyAttention==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csSafetyAttention);
	}

    /**
    * 导出excel
    * @param request
    */
	@AutoLog(value = "导出excel")
	@ApiOperation(value = "导出excel", notes = "导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request,
								  @RequestParam(name="ids",required=true) String ids) {
        return csSafetyAttentionService.exportXls(request, ids);
    }
	 /**
	  * 下载导入模板
	  *
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	 @AutoLog(value = "下载导入模板")
	 @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
	 @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	 public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 //获取输入流，原始模板位置
		 ClassPathResource classPathResource =  new ClassPathResource("templates/csSafetyAttention.xlsx");
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
	  * @param request
	  * @param response
	  * @return
	  */
	 @AutoLog(value = "导入")
	 @ApiOperation(value = "导入", notes = "导入")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result importExcel(HttpServletRequest request, HttpServletResponse response) {
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
				 return csSafetyAttentionService.importExcelMaterial(file, params);
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 log.error(e.getMessage(), e);
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
	 }
}
