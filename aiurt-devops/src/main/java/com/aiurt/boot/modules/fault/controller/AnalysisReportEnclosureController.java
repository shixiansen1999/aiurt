package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.entity.AnalysisReportEnclosure;
import com.swsc.copsms.modules.fault.service.IAnalysisReportEnclosureService;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: 分析报告-附件表
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="分析报告-附件表")
@RestController
@RequestMapping("/fault/analysisReportEnclosure")
public class AnalysisReportEnclosureController {
   @Autowired
   private IAnalysisReportEnclosureService analysisReportEnclosureService;

   /**
     * 分页列表查询
    * @param analysisReportEnclosure
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "分析报告-附件表-分页列表查询")
   @ApiOperation(value="分析报告-附件表-分页列表查询", notes="分析报告-附件表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<AnalysisReportEnclosure>> queryPageList(AnalysisReportEnclosure analysisReportEnclosure,
                                                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                               HttpServletRequest req) {
       Result<IPage<AnalysisReportEnclosure>> result = new Result<IPage<AnalysisReportEnclosure>>();
       QueryWrapper<AnalysisReportEnclosure> queryWrapper = QueryGenerator.initQueryWrapper(analysisReportEnclosure, req.getParameterMap());
       Page<AnalysisReportEnclosure> page = new Page<AnalysisReportEnclosure>(pageNo, pageSize);
       IPage<AnalysisReportEnclosure> pageList = analysisReportEnclosureService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param analysisReportEnclosure
    * @return
    */
   @AutoLog(value = "分析报告-附件表-添加")
   @ApiOperation(value="分析报告-附件表-添加", notes="分析报告-附件表-添加")
   @PostMapping(value = "/add")
   public Result<AnalysisReportEnclosure> add(@RequestBody AnalysisReportEnclosure analysisReportEnclosure) {
       Result<AnalysisReportEnclosure> result = new Result<AnalysisReportEnclosure>();
       try {
           analysisReportEnclosureService.save(analysisReportEnclosure);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param analysisReportEnclosure
    * @return
    */
   @AutoLog(value = "分析报告-附件表-编辑")
   @ApiOperation(value="分析报告-附件表-编辑", notes="分析报告-附件表-编辑")
   @PutMapping(value = "/edit")
   public Result<AnalysisReportEnclosure> edit(@RequestBody AnalysisReportEnclosure analysisReportEnclosure) {
       Result<AnalysisReportEnclosure> result = new Result<AnalysisReportEnclosure>();
       AnalysisReportEnclosure analysisReportEnclosureEntity = analysisReportEnclosureService.getById(analysisReportEnclosure.getId());
       if(analysisReportEnclosureEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = analysisReportEnclosureService.updateById(analysisReportEnclosure);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
    * 通过id删除
    * @param id
    * @return
    */
   @AutoLog(value = "分析报告-附件表-通过id删除")
   @ApiOperation(value="分析报告-附件表-通过id删除", notes="分析报告-附件表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           analysisReportEnclosureService.removeById(id);
       } catch (Exception e) {
           log.error("删除失败",e.getMessage());
           return Result.error("删除失败!");
       }
       return Result.ok("删除成功!");
   }

   /**
     *  批量删除
    * @param ids
    * @return
    */
   @AutoLog(value = "分析报告-附件表-批量删除")
   @ApiOperation(value="分析报告-附件表-批量删除", notes="分析报告-附件表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<AnalysisReportEnclosure> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<AnalysisReportEnclosure> result = new Result<AnalysisReportEnclosure>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.analysisReportEnclosureService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "分析报告-附件表-通过id查询")
   @ApiOperation(value="分析报告-附件表-通过id查询", notes="分析报告-附件表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<AnalysisReportEnclosure> queryById(@RequestParam(name="id",required=true) String id) {
       Result<AnalysisReportEnclosure> result = new Result<AnalysisReportEnclosure>();
       AnalysisReportEnclosure analysisReportEnclosure = analysisReportEnclosureService.getById(id);
       if(analysisReportEnclosure==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(analysisReportEnclosure);
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
     QueryWrapper<AnalysisReportEnclosure> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             AnalysisReportEnclosure analysisReportEnclosure = JSON.parseObject(deString, AnalysisReportEnclosure.class);
             queryWrapper = QueryGenerator.initQueryWrapper(analysisReportEnclosure, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<AnalysisReportEnclosure> pageList = analysisReportEnclosureService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "分析报告-附件表列表");
     mv.addObject(NormalExcelConstants.CLASS, AnalysisReportEnclosure.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分析报告-附件表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<AnalysisReportEnclosure> listAnalysisReportEnclosures = ExcelImportUtil.importExcel(file.getInputStream(), AnalysisReportEnclosure.class, params);
             analysisReportEnclosureService.saveBatch(listAnalysisReportEnclosures);
             return Result.ok("文件导入成功！数据行数:" + listAnalysisReportEnclosures.size());
         } catch (Exception e) {
             log.error(e.getMessage(),e);
             return Result.error("文件导入失败:"+e.getMessage());
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

}
