package com.aiurt.modules.signmeeting.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.aiurt.modules.signmeeting.entity.Conferee;
import com.aiurt.modules.signmeeting.entity.SignMeeting;
import com.aiurt.modules.signmeeting.service.IConfereeService;
import com.aiurt.modules.signmeeting.service.ISignMeetingService;
import com.aiurt.modules.signmeeting.vo.SignMeetingPage;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.util.DateUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 会议签到
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Api(tags="会议签到")
@RestController
@RequestMapping("/meeting/signMeeting")
@Slf4j
public class SignMeetingController {
	@Autowired
	private ISignMeetingService signMeetingService;
	@Autowired
	private IConfereeService confereeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param signMeeting
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "会议签到-分页列表查询")
	@ApiOperation(value="会议签到-分页列表查询", notes="会议签到-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SignMeeting>> queryPageList(SignMeeting signMeeting,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                    HttpServletRequest req) {
		QueryWrapper<SignMeeting> queryWrapper = QueryGenerator.initQueryWrapper(signMeeting, req.getParameterMap());
		Page<SignMeeting> page = new Page<SignMeeting>(pageNo, pageSize);
		IPage<SignMeeting> pageList = signMeetingService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param signMeetingPage
	 * @return
	 */
	@AutoLog(value = "会议签到-添加")
	@ApiOperation(value="会议签到-添加", notes="会议签到-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SignMeetingPage signMeetingPage) {
		SignMeeting signMeeting = new SignMeeting();
		BeanUtils.copyProperties(signMeetingPage, signMeeting);
		signMeetingService.saveMain(signMeeting, signMeetingPage.getConfereeList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param signMeetingPage
	 * @return
	 */
	@AutoLog(value = "会议签到-编辑")
	@ApiOperation(value="会议签到-编辑", notes="会议签到-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SignMeetingPage signMeetingPage) {
		SignMeeting signMeeting = new SignMeeting();
		BeanUtils.copyProperties(signMeetingPage, signMeeting);
		SignMeeting signMeetingEntity = signMeetingService.getById(signMeeting.getId());
		if(signMeetingEntity==null) {
			return Result.error("未找到对应数据");
		}
		signMeetingService.updateMain(signMeeting, signMeetingPage.getConfereeList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会议签到-通过id删除")
	@ApiOperation(value="会议签到-通过id删除", notes="会议签到-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		signMeetingService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "会议签到-批量删除")
	@ApiOperation(value="会议签到-批量删除", notes="会议签到-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.signMeetingService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会议签到-通过id查询")
	@ApiOperation(value="会议签到-通过id查询", notes="会议签到-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SignMeeting> queryById(@RequestParam(name="id",required=true) String id) {
		SignMeeting signMeeting = signMeetingService.getById(id);
		if(signMeeting==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(signMeeting);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "参会人员-通过主表ID查询")
	@ApiOperation(value = "参会人员-通过主表ID查询", notes = "参会人员-通过主表ID查询")
	@GetMapping(value = "/queryConfereeByMainId")
	public Result<List<Conferee>> queryConfereeListByMainId(@RequestParam(name="id",required=true) String id) {
		List<Conferee> confereeList = confereeService.selectByMainId(id);
		return Result.OK(confereeList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param signMeeting
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SignMeeting signMeeting) {
		// Step.1 组装查询条件查询数据
      QueryWrapper<SignMeeting> queryWrapper = QueryGenerator.initQueryWrapper(signMeeting, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<SignMeeting> queryList = signMeetingService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<SignMeeting> signMeetingList = new ArrayList<SignMeeting>();
      if(oConvertUtils.isEmpty(selections)) {
          signMeetingList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          signMeetingList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<SignMeetingPage> pageList = new ArrayList<SignMeetingPage>();
      for (SignMeeting main : signMeetingList) {
          SignMeetingPage vo = new SignMeetingPage();
          BeanUtils.copyProperties(main, vo);
          List<Conferee> confereeList = confereeService.selectByMainId(main.getId());
          vo.setConfereeList(confereeList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "会议签到列表");
      mv.addObject(NormalExcelConstants.CLASS, SignMeetingPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("会议签到数据", "导出人:"+sysUser.getRealname(), "会议签到"));
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
              List<SignMeetingPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SignMeetingPage.class, params);
              for (SignMeetingPage page : list) {
                  SignMeeting po = new SignMeeting();
                  BeanUtils.copyProperties(page, po);
                  signMeetingService.saveMain(po, page.getConfereeList());
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

	@ApiOperation(value = "会议签到-通过id导出", notes = "会议签到-通过id导出")
	@GetMapping(value = "/exportOneXls")
	public void exportOneXls(HttpServletResponse response, String id) {
		signMeetingService.exportOneXls(id, response);
	}

}
