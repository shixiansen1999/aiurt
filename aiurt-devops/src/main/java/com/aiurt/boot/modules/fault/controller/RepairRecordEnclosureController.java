package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.entity.RepairRecordEnclosure;
import com.swsc.copsms.modules.fault.service.IRepairRecordEnclosureService;
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
* @Description: 维修记录-附件表
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="维修记录-附件表")
@RestController
@RequestMapping("/fault/repairRecordEnclosure")
public class RepairRecordEnclosureController {
   @Autowired
   private IRepairRecordEnclosureService repairRecordEnclosureService;

   /**
     * 分页列表查询
    * @param repairRecordEnclosure
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "维修记录-附件表-分页列表查询")
   @ApiOperation(value="维修记录-附件表-分页列表查询", notes="维修记录-附件表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<RepairRecordEnclosure>> queryPageList(RepairRecordEnclosure repairRecordEnclosure,
                                                             @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                             HttpServletRequest req) {
       Result<IPage<RepairRecordEnclosure>> result = new Result<IPage<RepairRecordEnclosure>>();
       QueryWrapper<RepairRecordEnclosure> queryWrapper = QueryGenerator.initQueryWrapper(repairRecordEnclosure, req.getParameterMap());
       Page<RepairRecordEnclosure> page = new Page<RepairRecordEnclosure>(pageNo, pageSize);
       IPage<RepairRecordEnclosure> pageList = repairRecordEnclosureService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param repairRecordEnclosure
    * @return
    */
   @AutoLog(value = "维修记录-附件表-添加")
   @ApiOperation(value="维修记录-附件表-添加", notes="维修记录-附件表-添加")
   @PostMapping(value = "/add")
   public Result<RepairRecordEnclosure> add(@RequestBody RepairRecordEnclosure repairRecordEnclosure) {
       Result<RepairRecordEnclosure> result = new Result<RepairRecordEnclosure>();
       try {
           repairRecordEnclosureService.save(repairRecordEnclosure);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param repairRecordEnclosure
    * @return
    */
   @AutoLog(value = "维修记录-附件表-编辑")
   @ApiOperation(value="维修记录-附件表-编辑", notes="维修记录-附件表-编辑")
   @PutMapping(value = "/edit")
   public Result<RepairRecordEnclosure> edit(@RequestBody RepairRecordEnclosure repairRecordEnclosure) {
       Result<RepairRecordEnclosure> result = new Result<RepairRecordEnclosure>();
       RepairRecordEnclosure repairRecordEnclosureEntity = repairRecordEnclosureService.getById(repairRecordEnclosure.getId());
       if(repairRecordEnclosureEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = repairRecordEnclosureService.updateById(repairRecordEnclosure);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id删除
    * @param id
    * @return
    */
   @AutoLog(value = "维修记录-附件表-通过id删除")
   @ApiOperation(value="维修记录-附件表-通过id删除", notes="维修记录-附件表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           repairRecordEnclosureService.removeById(id);
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
   @AutoLog(value = "维修记录-附件表-批量删除")
   @ApiOperation(value="维修记录-附件表-批量删除", notes="维修记录-附件表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<RepairRecordEnclosure> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<RepairRecordEnclosure> result = new Result<RepairRecordEnclosure>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.repairRecordEnclosureService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "维修记录-附件表-通过id查询")
   @ApiOperation(value="维修记录-附件表-通过id查询", notes="维修记录-附件表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<RepairRecordEnclosure> queryById(@RequestParam(name="id",required=true) String id) {
       Result<RepairRecordEnclosure> result = new Result<RepairRecordEnclosure>();
       RepairRecordEnclosure repairRecordEnclosure = repairRecordEnclosureService.getById(id);
       if(repairRecordEnclosure==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(repairRecordEnclosure);
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
     QueryWrapper<RepairRecordEnclosure> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             RepairRecordEnclosure repairRecordEnclosure = JSON.parseObject(deString, RepairRecordEnclosure.class);
             queryWrapper = QueryGenerator.initQueryWrapper(repairRecordEnclosure, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<RepairRecordEnclosure> pageList = repairRecordEnclosureService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "维修记录-附件表列表");
     mv.addObject(NormalExcelConstants.CLASS, RepairRecordEnclosure.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("维修记录-附件表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<RepairRecordEnclosure> listRepairRecordEnclosures = ExcelImportUtil.importExcel(file.getInputStream(), RepairRecordEnclosure.class, params);
             repairRecordEnclosureService.saveBatch(listRepairRecordEnclosures);
             return Result.ok("文件导入成功！数据行数:" + listRepairRecordEnclosures.size());
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
