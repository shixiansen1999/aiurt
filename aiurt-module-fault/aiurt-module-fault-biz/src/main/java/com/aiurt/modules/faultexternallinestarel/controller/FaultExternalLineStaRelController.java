package com.aiurt.modules.faultexternallinestarel.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultexternallinestarel.entity.FaultExternalLineStaRel;
import com.aiurt.modules.faultexternallinestarel.service.IFaultExternalLineStaRelService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: fault_external_line_sta_rel
 * @Author: aiurt
 * @Date:   2023-06-13
 * @Version: V1.0
 */
@Api(tags="调度子系统位置")
@RestController
@RequestMapping("/faultexternallinestarel/faultExternalLineStaRel")
@Slf4j
public class FaultExternalLineStaRelController extends BaseController<FaultExternalLineStaRel, IFaultExternalLineStaRelService> {
	@Autowired
	private IFaultExternalLineStaRelService faultExternalLineStaRelService;

	/**
	 * 分页列表查询
	 *
	 * @param faultExternalLineStaRel 查询参数
	 * @param pageNo 页码
	 * @param pageSize 页数
	 * @return 列表
	 */
	@AutoLog(value = "调度子系统位置-分页列表查询")
	@ApiOperation(value="调度子系统位置-分页列表查询", notes="调度子系统位置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultExternalLineStaRel>> queryPageList(FaultExternalLineStaRel faultExternalLineStaRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<FaultExternalLineStaRel> page = new Page<>(pageNo, pageSize);
		IPage<FaultExternalLineStaRel> pageList = faultExternalLineStaRelService.pageList(page, faultExternalLineStaRel);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultExternalLineStaRel 添加的参数
	 * @return 结果集
	 */
	@AutoLog(value = "调度子系统位置-添加")
	@ApiOperation(value="调度子系统位置-添加", notes="调度子系统位置-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultExternalLineStaRel faultExternalLineStaRel) {
		faultExternalLineStaRelService.add(faultExternalLineStaRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultExternalLineStaRel 编辑参数
	 * @return 结果集
	 */
	@AutoLog(value = "调度子系统位置-编辑")
	@ApiOperation(value="调度子系统位置-编辑", notes="调度子系统位置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultExternalLineStaRel faultExternalLineStaRel) {
		faultExternalLineStaRelService.edit(faultExternalLineStaRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id 删除的id
	 * @return 结果集
	 */
	@AutoLog(value = "调度子系统位置-通过id删除")
	@ApiOperation(value="调度子系统位置-通过id删除", notes="调度子系统位置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		faultExternalLineStaRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids 删除的ids
	 * @return 结果集
	 */
	@AutoLog(value = "调度子系统位置-批量删除")
	@ApiOperation(value="调度子系统位置-批量删除", notes="调度子系统位置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.faultExternalLineStaRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id 查询id
	 * @return 调度子系统信息
	 */
	@AutoLog(value = "调度子系统位置-通过id查询")
	@ApiOperation(value="调度子系统位置-通过id查询", notes="调度子系统位置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultExternalLineStaRel> queryById(@RequestParam(name="id") String id) {
		FaultExternalLineStaRel faultExternalLineStaRel = faultExternalLineStaRelService.getById(id);
		if(faultExternalLineStaRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultExternalLineStaRel);
	}
	/**
	 * 下载模板
	 *
	 * @param response 响应参数
	 */
	@AutoLog(value = "调度子系统位置-下载模板")
	@ApiOperation(value = "调度子系统位置-下载模板", notes = "调度子系统位置-下载模板")
	@RequestMapping(value = "/downloadTemple", method = RequestMethod.GET)
	public void downloadTemple(HttpServletResponse response) throws IOException {
		//获取输入流，原始模板位置
		Resource resource = new ClassPathResource("/templates/faultexternallinestarel.xlsx");
		InputStream resourceAsStream = resource.getInputStream();
		//2.获取临时文件
		File fileTemp = new File("/templates/faultexternallinestarel.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		String path = fileTemp.getAbsolutePath();
		TemplateExportParams exportParams = new TemplateExportParams(path);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
		Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
		String fileName = "调度子系统位置导入模板.xlsx";
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			response.setHeader("Content-Disposition", "attachment;filename=" + "调度子系统位置导入模板.xlsx");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			workbook.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
    /**
    * 导出excel
    *
    * @param faultExternalLineStaRel 查询参数
    */
	@AutoLog(value = "调度子系统位置-导出")
	@ApiOperation(value = "调度子系统位置-导出", notes = "调度子系统位置-导出")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
	public ModelAndView exportXls(FaultExternalLineStaRel faultExternalLineStaRel,HttpServletRequest request) {
		List<FaultExternalLineStaRel> list = faultExternalLineStaRelService.getList(faultExternalLineStaRel);
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.FILE_NAME, "调度子系统位置");
		mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.CLASS, FaultExternalLineStaRel.class);
		mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.PARAMS, new ExportParams("调度子系统位置", "调度子系统位置导出信息", ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return  mv;
    }

    /**
      * 通过excel导入数据
    *
    * @param request 请求参数
    * @param response 响应参数
    * @return 结果集
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return faultExternalLineStaRelService.importExcel(request, response);
    }

}
