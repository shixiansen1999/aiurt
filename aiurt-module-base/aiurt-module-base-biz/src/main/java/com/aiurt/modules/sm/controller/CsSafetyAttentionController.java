package com.aiurt.modules.sm.controller;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.system.base.view.AiurtEntityExcelView;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

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
	public Result<IPage<CsSafetyAttention>> queryPageList(CsSafetyAttention csSafetyAttention,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LambdaQueryWrapper<CsSafetyAttention> queryWrapper = new LambdaQueryWrapper();
		if (StrUtil.isNotEmpty(csSafetyAttention.getMajorCode())){
		queryWrapper.eq(CsSafetyAttention::getMajorCode,csSafetyAttention.getMajorCode());
		}
		if (csSafetyAttention.getState()!=null){
			queryWrapper.eq(CsSafetyAttention::getMajorCode,csSafetyAttention.getState());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionMeasures())){
			queryWrapper.like(CsSafetyAttention::getAttentionMeasures,csSafetyAttention.getAttentionMeasures());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionContent())){
			queryWrapper.like(CsSafetyAttention::getAttentionContent,csSafetyAttention.getAttentionContent());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionType())){
			queryWrapper.eq(CsSafetyAttention::getAttentionType,csSafetyAttention.getAttentionType());
		}
		if (StrUtil.isNotEmpty(csSafetyAttention.getAttentionTypeCode())){
			queryWrapper.eq(CsSafetyAttention::getAttentionTypeCode,csSafetyAttention.getAttentionTypeCode());
		}
		queryWrapper.eq(CsSafetyAttention::getDelFlag,0);
		Page<CsSafetyAttention> page = new Page<CsSafetyAttention>(pageNo, pageSize);
		IPage<CsSafetyAttention> pageList = csSafetyAttentionService.page(page, queryWrapper);
		return Result.OK(pageList);
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
	public Result<String> add(@RequestBody CsSafetyAttention csSafetyAttention) {
		csSafetyAttentionService.save(csSafetyAttention);
		return Result.OK("添加成功！");
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
    *
    * @param request
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request,String ids) {
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
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CsSafetyAttention.class);
    }

}
