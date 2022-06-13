package com.aiurt.boot.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.result.BelongUnitResult;
import com.aiurt.common.result.OutsourcingPersonnelResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.aiurt.boot.modules.fault.entity.OutsourcingPersonnel;
import com.aiurt.boot.modules.fault.param.OutsourcingPersonnelParam;
import com.aiurt.boot.modules.fault.service.IOutsourcingPersonnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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

   @Resource
   private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @param param
     * @return
     */
   @AutoLog(value = "委外人员-分页列表查询")
   @ApiOperation(value="委外人员-分页列表查询", notes="委外人员-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<OutsourcingPersonnelResult>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                                  OutsourcingPersonnelParam param) {
       Result<IPage<OutsourcingPersonnelResult>> result = new Result<IPage<OutsourcingPersonnelResult>>();
       Page<OutsourcingPersonnelResult> page = new Page<OutsourcingPersonnelResult>(pageNo, pageSize);
       IPage<OutsourcingPersonnelResult> pageList = outsourcingPersonnelService.pageList(page,param);
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
   public Result<OutsourcingPersonnel> add(@Valid @RequestBody OutsourcingPersonnel outsourcingPersonnel, HttpServletRequest req) {
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
           result.onnull("未找到对应实体");
       }else {
           boolean ok = outsourcingPersonnelService.updateById(outsourcingPersonnel);

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
           outsourcingPersonnelService.removeById(id);
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
           result.onnull("未找到对应实体");
       }else {
           result.setResult(outsourcingPersonnel);
           result.setSuccess(true);
       }
       return result;
   }

    /**
     * 导出excel
     * @param param
     * @return
     */
 @GetMapping(value = "/exportXls")
 @ApiOperation(value="导出excel", notes="")
 public ModelAndView exportXls(OutsourcingPersonnelParam param) {

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<OutsourcingPersonnelResult> list = outsourcingPersonnelService.exportXls(param);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "委外人员列表");
     mv.addObject(NormalExcelConstants.CLASS, OutsourcingPersonnelResult.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("委外人员列表数据", "导出信息", ExcelType.XSSF));
     mv.addObject(NormalExcelConstants.DATA_LIST, list);
     return mv;
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
     Result<?> result =  outsourcingPersonnelService.importExcel(request,response);
     return result;
 }

 /**
  * 查询委外人员单位
  * @return
  */
 @AutoLog(value = "查询委外人员单位")
 @ApiOperation(value="查询委外人员单位", notes="查询委外人员单位")
 @GetMapping(value = "/selectBelongUnit")
 public Result<List<BelongUnitResult>> selectBelongUnit() {
     Result<List<BelongUnitResult>> result = new Result<List<BelongUnitResult>>();
     //  todo
    // List<BelongUnitResult> results = sysDictMapper.selectBelongUnit();
     result.setResult(null);
     return result;
 }

    /**
     * 下载委外人员导入信息模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "委外人员导入信息模板")
    @ApiOperation(value = "下载委外人员导入信息模板", notes = "下载委外人员导入信息模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource =  new ClassPathResource("template/委外人员导入信息模板.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

}
