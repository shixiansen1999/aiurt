package com.aiurt.modules.train.feedback.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackMapper;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackOptionsService;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackQuesService;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackService;
import com.aiurt.modules.train.feedback.vo.BdTrainQuestionFeedbackPage;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 问题反馈主表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="问题反馈主表")
@RestController
@RequestMapping("/bdtrainfeedback/bdTrainFeedback")
@Slf4j
public class BdTrainQuestionFeedbackController {
	@Autowired
	private IBdTrainQuestionFeedbackService bdTrainQuestionFeedbackService;
	@Autowired
	private IBdTrainQuestionFeedbackQuesService bdTrainQuestionFeedbackQuesService;
	@Autowired
	private IBdTrainQuestionFeedbackOptionsService bdTrainQuestionFeedbackOptionsService;
	@Autowired
	private BdTrainQuestionFeedbackMapper bdTrainQuestionFeedbackMapper;
	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;

	/**
	 * 分页列表查询
	 *
	 * @param bdTrainQuestionFeedback
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-分页列表查询")
	@ApiOperation(value="问题反馈主表-分页列表查询", notes="问题反馈主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainQuestionFeedback bdTrainQuestionFeedback,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BdTrainQuestionFeedback> page = new Page<BdTrainQuestionFeedback>(pageNo, pageSize);
		Page<BdTrainQuestionFeedback> pageList =bdTrainQuestionFeedbackService.queryPageList(page,bdTrainQuestionFeedback);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param bdTrainQuestionFeedbackPage
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-添加")
	@ApiOperation(value="问题反馈主表-添加", notes="问题反馈主表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage) {
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		bdTrainQuestionFeedbackPage.setSysOrgCode(sysUser.getOrgCode());
		BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
		BeanUtils.copyProperties(bdTrainQuestionFeedbackPage, bdTrainQuestionFeedback);
		bdTrainQuestionFeedbackService.add(bdTrainQuestionFeedback,bdTrainQuestionFeedbackPage);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdTrainQuestionFeedbackPage
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-修改")
	@ApiOperation(value="问题反馈主表-修改", notes="问题反馈主表-修改")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage) {
		BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
		BeanUtils.copyProperties(bdTrainQuestionFeedbackPage, bdTrainQuestionFeedback);
		BdTrainQuestionFeedback bdTrainQuestionFeedbackEntity = bdTrainQuestionFeedbackService.getById(bdTrainQuestionFeedback.getId());
		if(bdTrainQuestionFeedbackEntity==null) {
			return Result.error("未找到对应数据");
		}
		bdTrainQuestionFeedbackService.updateMain(bdTrainQuestionFeedback, bdTrainQuestionFeedbackPage.getBdTrainQuestionFeedbackQuesList(),bdTrainQuestionFeedbackPage.getBdTrainQuestionFeedbackOptionsList());
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-通过id删除")
	@ApiOperation(value="问题反馈主表-通过id删除", notes="问题反馈主表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id){
		bdTrainQuestionFeedbackService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-批量删除")
	@ApiOperation(value="问题反馈主表-批量删除", notes="问题反馈主表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainQuestionFeedbackService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 * @param
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-查看")
	@ApiOperation(value="问题反馈主表-查看", notes="问题反馈主表-查看")
	@GetMapping(value = "/queryFeedbackPage")
	public Result<?> queryFeedbackPage(@RequestParam(name="id",required=true) String id) {
		BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage=bdTrainQuestionFeedbackService.queryByFeedbackPage(id);
		return Result.OK(bdTrainQuestionFeedbackPage);
	}
	@AutoLog(value = "问题反馈主表-通过id查询")
	@ApiOperation(value="问题反馈主表-通过id查询", notes="问题反馈主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainQuestionFeedback bdTrainQuestionFeedback = bdTrainQuestionFeedbackService.getById(id);
		if(bdTrainQuestionFeedback==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainQuestionFeedback);
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问题反馈问题列表通过主表ID查询")
	@ApiOperation(value="问题反馈问题列表主表ID查询", notes="问题反馈问题列表-通主表ID查询")
	@GetMapping(value = "/queryBdTrainQuestionFeedbackQuesByMainId")
	public Result<?> queryBdTrainQuestionFeedbackQuesListByMainId(@RequestParam(name="id",required=true) String id) {
		List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList = bdTrainQuestionFeedbackQuesService.selectByMainId(id);
		return Result.OK(bdTrainQuestionFeedbackQuesList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问题反馈单选项通过主表ID查询")
	@ApiOperation(value="问题反馈单选项主表ID查询", notes="问题反馈单选项-通主表ID查询")
	@GetMapping(value = "/queryBdTrainQuestionFeedbackOptionsByMainId")
	public Result<?> queryBdTrainQuestionFeedbackOptionsListByMainId(@RequestParam(name="id",required=true) String id) {
		List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList = bdTrainQuestionFeedbackOptionsService.selectByMainId(id);
		return Result.OK(bdTrainQuestionFeedbackOptionsList);
	}
	/**
	 *   启用
	 * @param bdTrainQuestionFeedback
	 * @return
	 */
	@AutoLog(value = "问题反馈主表-启用")
	@ApiOperation(value = "问题反馈主表-启用",notes = "问题反馈主表-启用")
	@PostMapping(value = "/enable")
	public Result<?> enable(@RequestBody BdTrainQuestionFeedback bdTrainQuestionFeedback){
		bdTrainQuestionFeedbackService.enable(bdTrainQuestionFeedback);
		return Result.OK("启用成功!");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param bdTrainQuestionFeedback
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BdTrainQuestionFeedback bdTrainQuestionFeedback) {
		// Step.1 组装查询条件查询数据
		QueryWrapper<BdTrainQuestionFeedback> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainQuestionFeedback, request.getParameterMap());
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		//Step.2 获取导出数据
		List<BdTrainQuestionFeedback> queryList = bdTrainQuestionFeedbackService.list(queryWrapper);
		// 过滤选中数据
		String selections = request.getParameter("selections");
		List<BdTrainQuestionFeedback> bdTrainQuestionFeedbackList = new ArrayList<BdTrainQuestionFeedback>();
		if(oConvertUtils.isEmpty(selections)) {
			bdTrainQuestionFeedbackList = queryList;
		}else {
			List<String> selectionList = Arrays.asList(selections.split(","));
			bdTrainQuestionFeedbackList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		}

		// Step.3 组装pageList
		List<BdTrainQuestionFeedbackPage> pageList = new ArrayList<BdTrainQuestionFeedbackPage>();
		for (BdTrainQuestionFeedback main : bdTrainQuestionFeedbackList) {
			BdTrainQuestionFeedbackPage vo = new BdTrainQuestionFeedbackPage();
			BeanUtils.copyProperties(main, vo);
			List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList = bdTrainQuestionFeedbackQuesService.selectByMainId(main.getId());
			vo.setBdTrainQuestionFeedbackQuesList(bdTrainQuestionFeedbackQuesList);
			List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList = bdTrainQuestionFeedbackOptionsService.selectByMainId(main.getId());
			vo.setBdTrainQuestionFeedbackOptionsList(bdTrainQuestionFeedbackOptionsList);
			pageList.add(vo);
		}

		// Step.4 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, "问题反馈主表列表");
		mv.addObject(NormalExcelConstants.CLASS, BdTrainQuestionFeedbackPage.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("问题反馈主表数据", "导出人:"+sysUser.getRealname(), "问题反馈主表"));
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
			MultipartFile file = entity.getValue();
			// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<BdTrainQuestionFeedbackPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BdTrainQuestionFeedbackPage.class, params);
				for (BdTrainQuestionFeedbackPage page : list) {
					BdTrainQuestionFeedback po = new BdTrainQuestionFeedback();
					BeanUtils.copyProperties(page, po);
					bdTrainQuestionFeedbackService.saveMain(po, page.getBdTrainQuestionFeedbackQuesList(),page.getBdTrainQuestionFeedbackOptionsList());
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
