package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.entity.FaultChangeSparePart;
import com.swsc.copsms.modules.fault.service.IFaultChangeSparePartService;
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
* @Description: 故障更换备件表
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="故障更换备件表")
@RestController
@RequestMapping("/fault/faultChangeSparePart")
public class FaultChangeSparePartController {
   @Autowired
   private IFaultChangeSparePartService faultChangeSparePartService;

   /**
     * 分页列表查询
    * @param faultChangeSparePart
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "故障更换备件表-分页列表查询")
   @ApiOperation(value="故障更换备件表-分页列表查询", notes="故障更换备件表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<FaultChangeSparePart>> queryPageList(FaultChangeSparePart faultChangeSparePart,
                                                            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                            HttpServletRequest req) {
       Result<IPage<FaultChangeSparePart>> result = new Result<IPage<FaultChangeSparePart>>();
       QueryWrapper<FaultChangeSparePart> queryWrapper = QueryGenerator.initQueryWrapper(faultChangeSparePart, req.getParameterMap());
       Page<FaultChangeSparePart> page = new Page<FaultChangeSparePart>(pageNo, pageSize);
       IPage<FaultChangeSparePart> pageList = faultChangeSparePartService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param faultChangeSparePart
    * @return
    */
   @AutoLog(value = "故障更换备件表-添加")
   @ApiOperation(value="故障更换备件表-添加", notes="故障更换备件表-添加")
   @PostMapping(value = "/add")
   public Result<FaultChangeSparePart> add(@RequestBody FaultChangeSparePart faultChangeSparePart) {
       Result<FaultChangeSparePart> result = new Result<FaultChangeSparePart>();
       try {
           faultChangeSparePartService.save(faultChangeSparePart);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param faultChangeSparePart
    * @return
    */
   @AutoLog(value = "故障更换备件表-编辑")
   @ApiOperation(value="故障更换备件表-编辑", notes="故障更换备件表-编辑")
   @PutMapping(value = "/edit")
   public Result<FaultChangeSparePart> edit(@RequestBody FaultChangeSparePart faultChangeSparePart) {
       Result<FaultChangeSparePart> result = new Result<FaultChangeSparePart>();
       FaultChangeSparePart faultChangeSparePartEntity = faultChangeSparePartService.getById(faultChangeSparePart.getId());
       if(faultChangeSparePartEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = faultChangeSparePartService.updateById(faultChangeSparePart);
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
   @AutoLog(value = "故障更换备件表-通过id删除")
   @ApiOperation(value="故障更换备件表-通过id删除", notes="故障更换备件表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           faultChangeSparePartService.removeById(id);
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
   @AutoLog(value = "故障更换备件表-批量删除")
   @ApiOperation(value="故障更换备件表-批量删除", notes="故障更换备件表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<FaultChangeSparePart> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<FaultChangeSparePart> result = new Result<FaultChangeSparePart>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.faultChangeSparePartService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "故障更换备件表-通过id查询")
   @ApiOperation(value="故障更换备件表-通过id查询", notes="故障更换备件表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<FaultChangeSparePart> queryById(@RequestParam(name="id",required=true) String id) {
       Result<FaultChangeSparePart> result = new Result<FaultChangeSparePart>();
       FaultChangeSparePart faultChangeSparePart = faultChangeSparePartService.getById(id);
       if(faultChangeSparePart==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(faultChangeSparePart);
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
     QueryWrapper<FaultChangeSparePart> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             FaultChangeSparePart faultChangeSparePart = JSON.parseObject(deString, FaultChangeSparePart.class);
             queryWrapper = QueryGenerator.initQueryWrapper(faultChangeSparePart, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<FaultChangeSparePart> pageList = faultChangeSparePartService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "故障更换备件表列表");
     mv.addObject(NormalExcelConstants.CLASS, FaultChangeSparePart.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障更换备件表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<FaultChangeSparePart> listFaultChangeSpareParts = ExcelImportUtil.importExcel(file.getInputStream(), FaultChangeSparePart.class, params);
             faultChangeSparePartService.saveBatch(listFaultChangeSpareParts);
             return Result.ok("文件导入成功！数据行数:" + listFaultChangeSpareParts.size());
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
