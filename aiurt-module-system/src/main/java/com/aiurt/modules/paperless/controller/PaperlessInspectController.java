package com.aiurt.modules.paperless.controller;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.boot.team.model.CrewModel;
import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.util.XlsUtil;
import lombok.val;
import org.apache.poi.ss.usermodel.Workbook;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.aiurt.modules.paperless.entity.PaperlessInspect;
import com.aiurt.modules.paperless.vo.PaperlessInspectPage;
import com.aiurt.modules.paperless.service.IPaperlessInspectService;
import com.aiurt.modules.paperless.service.IPaperlessInspectEntryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 安全检查记录
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Api(tags="安全检查记录")
@RestController
@RequestMapping("/paperless/paperlessInspect")
@Slf4j
public class PaperlessInspectController {
	@Autowired
	private IPaperlessInspectService paperlessInspectService;
	@Autowired
	private IPaperlessInspectEntryService paperlessInspectEntryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param paperlessInspect
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "安全检查记录-分页列表查询")
	@ApiOperation(value="安全检查记录-分页列表查询", notes="安全检查记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PaperlessInspect>> queryPageList(PaperlessInspect paperlessInspect,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PaperlessInspect> queryWrapper = QueryGenerator.initQueryWrapper(paperlessInspect, req.getParameterMap());
		Page<PaperlessInspect> page = new Page<PaperlessInspect>(pageNo, pageSize);
		IPage<PaperlessInspect> pageList = paperlessInspectService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param paperlessInspectPage
	 * @return
	 */
	@AutoLog(value = "安全检查记录-添加")
	@ApiOperation(value="安全检查记录-添加", notes="安全检查记录-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PaperlessInspectPage paperlessInspectPage) {
		PaperlessInspect paperlessInspect = new PaperlessInspect();
		BeanUtils.copyProperties(paperlessInspectPage, paperlessInspect);
		paperlessInspectService.saveMain(paperlessInspect, paperlessInspectPage.getPaperlessInspectEntryList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param paperlessInspectPage
	 * @return
	 */
	@AutoLog(value = "安全检查记录-编辑")
	@ApiOperation(value="安全检查记录-编辑", notes="安全检查记录-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PaperlessInspectPage paperlessInspectPage) {
		PaperlessInspect paperlessInspect = new PaperlessInspect();
		BeanUtils.copyProperties(paperlessInspectPage, paperlessInspect);
		PaperlessInspect paperlessInspectEntity = paperlessInspectService.getById(paperlessInspect.getId());
		if(paperlessInspectEntity==null) {
			return Result.error("未找到对应数据");
		}
		paperlessInspectService.updateMain(paperlessInspect, paperlessInspectPage.getPaperlessInspectEntryList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "安全检查记录-通过id删除")
	@ApiOperation(value="安全检查记录-通过id删除", notes="安全检查记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		paperlessInspectService.delMain(id);
		log.info("删除了"+id+"的数据");
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "安全检查记录-批量删除")
	@ApiOperation(value="安全检查记录-批量删除", notes="安全检查记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.paperlessInspectService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "安全检查记录-通过id查询")
	@ApiOperation(value="安全检查记录-通过id查询", notes="安全检查记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PaperlessInspect> queryById(@RequestParam(name="id",required=true) String id) {
		PaperlessInspect paperlessInspect = paperlessInspectService.getById(id);
		if(paperlessInspect==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(paperlessInspect);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "安全检查记录从表通过主表ID查询")
	@ApiOperation(value="安全检查记录从表主表ID查询", notes="安全检查记录从表-通主表ID查询")
	@GetMapping(value = "/queryPaperlessInspectEntryByMainId")
	public Result<List<PaperlessInspectEntry>> queryPaperlessInspectEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		List<PaperlessInspectEntry> paperlessInspectEntryList = paperlessInspectEntryService.selectByMainId(id);
		return Result.OK(paperlessInspectEntryList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param id
    */
	@ApiOperation(value="安全检查记录-导出", notes="导出")
    @GetMapping(value = "/exportXlsx")
    public void exportXls(HttpServletRequest request, String id,HttpServletResponse response) throws Exception {
//		String path="templates/paperless.xlsx";
		String path="aiurt-module-system/src/main/resources/templates/paperless.xlsx";
		TemplateExportParams params = new TemplateExportParams(path,true);
		PaperlessInspect paperlessInspect = paperlessInspectService.getById(id);
//		System.out.println(params);
		Map<String, Object> map = new HashMap<String, Object>();
		String company="□";
		String department="□";
		String team="□";
		if (paperlessInspect!=null){
			if (paperlessInspect.getPaperlessTeam()!=null){
				switch (paperlessInspect.getPaperlessTeam()){
					case 0:company="√";
						break;
					case 1:department="√";
						break;
					case 2:team="√";
				}
			}
			map.put("paperlessName",paperlessInspect.getPaperlessName());
			map.put("paperlessTime",DateUtil.format(paperlessInspect.getPaperlessTime(), "YYYY-MM-dd HH:mm:ss"));
			map.put("paperlessInspect",paperlessInspect.getPaperlessInspect());
			map.put("paperlessRectification",paperlessInspect.getPaperlessRectification());
			map.put("paperlessOpinion",paperlessInspect.getPaperlessOpinion());
			map.put("paperlessInspector",paperlessInspect.getPaperlessInspector());
			map.put("paperlessLiable",paperlessInspect.getPaperlessLiable());
		}

//		 map.put("paperlessTeam",paperlessInspect.getPaperlessTeam());
		map.put("company",company);
		map.put("department",department);
		map.put("team",team);
		 map.put("paperlessTeam","√");

		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		List<PaperlessInspectEntry> paperlessInspectEntryList = paperlessInspectEntryService.selectByMainId(paperlessInspect.getId());

		Integer i=0;

		for (PaperlessInspectEntry paperlessInspectEntry : paperlessInspectEntryList) {
			i++;
			Map<String, String> entryMap = new HashMap<>();
			entryMap.put("id", i.toString());
			entryMap.put("content", paperlessInspectEntry.getPaperlessContent());
			entryMap.put("examine", paperlessInspectEntry.getPaperlessExamine());
			entryMap.put("liable", paperlessInspectEntry.getPaperlessLiable());
			entryMap.put("method", paperlessInspectEntry.getPaperlessMethod());
			entryMap.put("timeend",DateUtil.format(paperlessInspectEntry.getPaperlessTimeend(), "YYYY-MM-dd HH:mm:ss"));
			entryMap.put("time", DateUtil.format(paperlessInspectEntry.getPaperlessTime(), "YYYY-MM-dd HH:mm:ss"));
			listMap.add(entryMap);
		}

		map.put("paperlessInspectEntryList", listMap);
		String fileName = "安全检查记录";
		Workbook workbook = ExcelExportUtil.exportExcel(params, map);
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			//xlsx格式设置
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
//			workbook.write(bufferedOutPut);
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (workbook!=null){
				workbook.close();
			}
		}
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
              List<PaperlessInspectPage> list = ExcelImportUtil.importExcel(file.getInputStream(), PaperlessInspectPage.class, params);
              for (PaperlessInspectPage page : list) {
                  PaperlessInspect po = new PaperlessInspect();
                  BeanUtils.copyProperties(page, po);
                  paperlessInspectService.saveMain(po, page.getPaperlessInspectEntryList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
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
      return Result.OK("文件导入失败！");
    }




}
