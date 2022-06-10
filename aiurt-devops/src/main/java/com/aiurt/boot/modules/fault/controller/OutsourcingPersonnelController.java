package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.entity.OutsourcingPersonnel;
import com.swsc.copsms.modules.fault.param.OutsourcingPersonnelParam;
import com.swsc.copsms.modules.fault.service.IOutsourcingPersonnelService;
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
* @Description: 委外人员
* @Author: qian
* @Date:   2021-09-18
* @Version: V1.0
*/
@Slf4j
@Api(tags="委外人员")
@RestController
@RequestMapping("/fault/outsourcingPersonnel")
public class OutsourcingPersonnelController {
   @Autowired
   private IOutsourcingPersonnelService outsourcingPersonnelService;

   /**
     * 分页列表查询
    * @param outsourcingPersonnel
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "委外人员-分页列表查询")
   @ApiOperation(value="委外人员-分页列表查询", notes="委外人员-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<OutsourcingPersonnel>> queryPageList(OutsourcingPersonnel outsourcingPersonnel,
                                                            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                            @Valid OutsourcingPersonnelParam param,
                                                            HttpServletRequest req) {
       Result<IPage<OutsourcingPersonnel>> result = new Result<IPage<OutsourcingPersonnel>>();
       QueryWrapper<OutsourcingPersonnel> queryWrapper = QueryGenerator.initQueryWrapper(outsourcingPersonnel, req.getParameterMap());
       Page<OutsourcingPersonnel> page = new Page<OutsourcingPersonnel>(pageNo, pageSize);
       IPage<OutsourcingPersonnel> pageList = outsourcingPersonnelService.pageList(page, queryWrapper,param);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   新增人员
    * @param outsourcingPersonnel
    * @return
    */
   @AutoLog(value = "委外人员-添加")
   @ApiOperation(value="委外人员-添加", notes="委外人员-添加")
   @PostMapping(value = "/add")
   public Result<OutsourcingPersonnel> add(@RequestBody OutsourcingPersonnel outsourcingPersonnel, HttpServletRequest req) {
       Result<OutsourcingPersonnel> result = new Result<OutsourcingPersonnel>();
       try {
           outsourcingPersonnelService.add(outsourcingPersonnel,req);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500(e.getMessage());
       }
       return result;
   }

   /**
     *  编辑
    * @param outsourcingPersonnel
    * @return
    */
   @AutoLog(value = "委外人员-编辑")
   @ApiOperation(value="委外人员-编辑", notes="委外人员-编辑")
   @PutMapping(value = "/edit")
   public Result<OutsourcingPersonnel> edit(@RequestBody OutsourcingPersonnel outsourcingPersonnel) {
       Result<OutsourcingPersonnel> result = new Result<OutsourcingPersonnel>();
       OutsourcingPersonnel outsourcingPersonnelEntity = outsourcingPersonnelService.getById(outsourcingPersonnel.getId());
       if(outsourcingPersonnelEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = outsourcingPersonnelService.updateById(outsourcingPersonnel);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id假删除
    * @param id
    * @return
    */
   @AutoLog(value = "委外人员-通过id删除")
   @ApiOperation(value="委外人员-通过id删除", notes="委外人员-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
       try {
           outsourcingPersonnelService.deleteById(id);
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
   @AutoLog(value = "委外人员-批量删除")
   @ApiOperation(value="委外人员-批量删除", notes="委外人员-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<OutsourcingPersonnel> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<OutsourcingPersonnel> result = new Result<OutsourcingPersonnel>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.outsourcingPersonnelService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "委外人员-通过id查询")
   @ApiOperation(value="委外人员-通过id查询", notes="委外人员-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<OutsourcingPersonnel> queryById(@RequestParam(name="id",required=true) String id) {
       Result<OutsourcingPersonnel> result = new Result<OutsourcingPersonnel>();
       OutsourcingPersonnel outsourcingPersonnel = outsourcingPersonnelService.getById(id);
       if(outsourcingPersonnel==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(outsourcingPersonnel);
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
     QueryWrapper<OutsourcingPersonnel> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             OutsourcingPersonnel outsourcingPersonnel = JSON.parseObject(deString, OutsourcingPersonnel.class);
             queryWrapper = QueryGenerator.initQueryWrapper(outsourcingPersonnel, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<OutsourcingPersonnel> pageList = outsourcingPersonnelService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "委外人员列表");
     mv.addObject(NormalExcelConstants.CLASS, OutsourcingPersonnel.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("委外人员列表数据", "导出人:Jeecg", "导出信息"));
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
             List<OutsourcingPersonnel> listOutsourcingPersonnels = ExcelImportUtil.importExcel(file.getInputStream(), OutsourcingPersonnel.class, params);
             outsourcingPersonnelService.saveBatch(listOutsourcingPersonnels);
             return Result.ok("文件导入成功！数据行数:" + listOutsourcingPersonnels.size());
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

 /**
  * 查询所有委外人员
  * @return
  */
 @AutoLog(value = "查询所有委外人员")
 @ApiOperation(value="查询所有委外人员", notes="查询所有委外人员")
 @GetMapping(value = "/queryAll")
 public Result<List<OutsourcingPersonnel>> queryAll() {
     Result<List<OutsourcingPersonnel>> result = new Result<List<OutsourcingPersonnel>>();
     List<OutsourcingPersonnel> outsourcingPersonnels = outsourcingPersonnelService.queryAll();
     result.setResult(outsourcingPersonnels);
     return result;
 }

}
