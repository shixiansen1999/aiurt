package com.aiurt.modules.train.eaxm.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.exam.dto.BdAchievementDTO;
import com.aiurt.modules.train.exam.dto.ExamDetailsDTO;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.eaxm.service.IBdExamRecordService;
import com.aiurt.modules.train.quzrtz.QuartzServiceImpl;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 考试记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="考试记录")
@RestController
@RequestMapping("/examrecord/bdExamRecord")
@Slf4j
public class BdExamRecordController extends BaseController<BdExamRecord, IBdExamRecordService> {
	@Autowired
	private IBdExamRecordService bdExamRecordService;


	 @Autowired
	 private BdExamRecordMapper bdExamRecordMapper;

	@Autowired
	private QuartzServiceImpl quartzService;
	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;

	 /**
	  * 学员考试任务列表
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "考试记录-学员考试任务列表")
	 @ApiOperation(value="考试记录-学员考试任务列表", notes="考试记录-学员考试任务列表")
	 @GetMapping(value = "/queryExaminationList")
	 public Result<?> queryExaminationList( BdExamRecord condition,
										   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	                                      ){
		 Page<BdExamRecord> pageList = new Page<>(pageNo, pageSize);
		 Page<BdExamRecord> bdExamRecordPage = bdExamRecordService.queryPageList(pageList, condition);
		 return Result.OK(bdExamRecordPage);
	 }
	 /**
	  * 学员考试任务列表
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "考试记录-学员考试任务列表-PC")
	 @ApiOperation(value="考试记录-学员考试任务列表-PC", notes="考试记录-学员考试任务列表-PC")
	 @GetMapping(value = "/queryExaminationListPc")
	 public Result<?> queryExaminationListPc(BdExamRecord condition,
										   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	 ){
		 Page<BdExamRecord> pageList = new Page<>(pageNo, pageSize);
		 Page<BdExamRecord> lists = bdExamRecordService.queryPageListPc(pageList, condition);
		 return Result.OK(lists);
	 }

	 /**
	  * 讲师考试任务列表
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "考试记录-讲师考试任务列表")
	 @ApiOperation(value="考试记录-讲师考试任务列表", notes="考试记录-讲师考试任务列表")
	 @GetMapping(value = "/quadratureList")
	 public Result<?> quadratureList( BdExamRecord condition,
									 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	 ){
		 Page<BdExamRecord> pageList = new Page<>(pageNo, pageSize);
		 Page<BdExamRecord> bdExamRecordPage = bdExamRecordService.lecturerList(pageList, condition);
		 return Result.OK(bdExamRecordPage);
	 }

	 /**
	  * 考试记录查询
	  * @param condition
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "考试记录-考试记录查询")
	 @ApiOperation(value="考试记录-考试记录查询", notes="考试记录-考试记录查询")
	 @PostMapping(value = "/examDetailsList")
	 public Result<?> examDetailsList(@RequestBody BdExamRecord condition,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
									  ){
		 Page<BdExamRecord> pageList = new Page<>(pageNo, pageSize);
		 Page<BdExamRecord> lists = bdExamRecordService.lists(pageList, condition);
		 return Result.OK(lists);
	 }

	 /**
	  * 历史考试记录
	  * @param condition
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "考试记录-历史考试记录查询")
	 @ApiOperation(value="考试记录-历史考试记录查询", notes="考试记录-历史考试记录查询")
	 @GetMapping(value = "/recordList")
	 public Result<?> recordList( BdExamRecord condition,
								 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	 ){
		 Page<BdExamRecord> pageList = new Page<>(pageNo, pageSize);
		 Page<BdExamRecord> lists = bdExamRecordService.recordList(pageList,condition);
		 return Result.OK(lists);
	 }


	 /**
	  * 考试详情
	  * @param examPaperId
	  * @param trainTaskId
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "考试记录-考试详情")
	 @ApiOperation(value="考试记录-考试详情", notes="考试记录-考试详情")
	 @GetMapping(value = "/examinationInformationList")
	 public Result<?> examinationInformationList(@RequestParam(value = "examPaperId",required = false)String examPaperId,
												 @RequestParam("trainTaskId")String trainTaskId,
												 @RequestParam(value = "examClassify",required = false)Integer examClassify,
												 @RequestParam(value = "id",required = false) String id){
	 	return  Result.OK(bdExamRecordService.readOne(examPaperId,trainTaskId,examClassify,id));
	 }

	 /**
	  * 核对考试信息
	  * @param examPaperId
	  * @param trainTaskId
	  * @return
	  */
	 @AutoLog(value = "考试记录-核对学员考试信息")
	 @ApiOperation(value="考试记录-核对学员考试信息", notes="考试记录-核对学员考试信息")
	 @GetMapping(value = "/examineList")
	 public Result<?> examineList(@RequestParam("examPaperId")String examPaperId, @RequestParam(value = "trainTaskId") String trainTaskId){
		 return  Result.OK(bdExamRecordService.readOnes(examPaperId,trainTaskId));
	 }


	 /**
	  *
	  * @param id //试卷id
	  * @param exemplify // 考试类型
	  * @param state // 考试任务状态
	  * @param state // 考试任务id
	  * @return
	  */
	 @AutoLog(value = "考试记录-核对讲师考试信息")
	 @ApiOperation(value="考试记录-核对讲师考试信息", notes="考试记录-核对讲师考试信息")
	 @GetMapping(value = "/lecturerList")
	 public Result<?> lecturerList(@RequestParam("id")String id,
								   @RequestParam(value = "exemplify",required = false) Integer exemplify,
								   @RequestParam("state") Integer state,
								   @RequestParam("taskId") String taskId){
		 BdExamPaper bdExamPaper = bdExamRecordService.lecturerReadOne(id, exemplify, state,taskId);
		 if (ObjectUtil.isNotNull(bdExamPaper) && bdExamPaper.getExamTime()!=null && bdExamPaper.getExamValidityPeriod()!=null){
			 Date time1 = new Date();
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(bdExamPaper.getExamTime());
			 calendar.add(Calendar.DATE,bdExamPaper.getExamValidityPeriod());
			 time1=calendar.getTime();
			 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
			 String string1 = simpleDateFormat.format(time1);
			 bdExamPaper.setExaminationDeadline(string1);
		 }
		 return  Result.OK(bdExamPaper);
	 }

	 /**
	  * 开始考试
	  * @param id //试卷id
	  * @param exemplify // 考试类型
	  * @param state // 考试任务状态
	  * @return
	  */
	 @AutoLog(value = "考试记录-开始考试")
	 @ApiOperation(value="考试记录-开始考试", notes="考试记录-开始考试")
	 @GetMapping(value = "/startTheExamList")
	 public Result<?> startTheExam (@RequestParam("id")String id,
									@RequestParam(value = "exemplify",required = false) Integer exemplify,
									@RequestParam("state") Integer state,
									@RequestParam("taskId") String taskId){
		 BdExamPaper bdExamPaper = bdExamRecordService.lecturerReadOne(id, exemplify, state,taskId);
		 Date time1 = new Date();
		 if (ObjectUtil.isNotNull(bdExamPaper) && bdExamPaper.getExamTime()!=null && bdExamPaper.getExamValidityPeriod()!=null ){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(bdExamPaper.getExamTime());
			 calendar.add(Calendar.DATE,bdExamPaper.getExamValidityPeriod());
			 time1=calendar.getTime();
			 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
			 String string1 = simpleDateFormat.format(time1);
			 bdExamPaper.setExaminationDeadline(string1);
		 }
		 return  Result.OK(bdExamPaper);
	 }

	 /**
	  * 录入考试结果列表查询
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "考试记录-录入考试结果列表查询-考试名单查询")
	 @ApiOperation(value="考试记录-录入考试结果列表查询-考试名单查询", notes="考试记录-录入考试结果列表查询-考试名单查询")
	 @GetMapping(value = "/resultList")
	 public Result<?> resultList (@RequestParam("id")String id,
								  @RequestParam(value = "examPaperId",required = false)String examPaperId,
								  @RequestParam(value = "examClassify",required = false) Integer examClassify){
		 return  Result.OK(bdExamRecordService.resultList(id,examPaperId,examClassify));
	 }

	 /**
	  * 考试结果录入
	  * @param
	  * @return
	  */
	 @AutoLog(value = "考试记录-考试结果录入")
	 @ApiOperation(value="考试记录-考试结果录入", notes="考试记录-考试结果录入")
	 @PostMapping(value = "/addList")
	 public Result<?> addList (@RequestBody BdExamRecord bdExamRecord){
		 List<BdAchievementDTO> bdAchievementDTOList = bdExamRecord.getBdAchievementDTOList();
		 bdExamRecordService.addList(bdAchievementDTOList);
		 return Result.OK("录入成功！");
	 }

	 /**
	  * 提交考试结果
	  * @param bdExamRecord
	  * @return
	  */
	 @AutoLog(value = "考试记录-提交考试结果")
	 @ApiOperation(value="考试记录-提交考试结果", notes="考试记录-提交考试结果")
	 @PostMapping(value = "/addBdQuestionCategory")
	 public Result<?> addBdQuestionCategory (@RequestBody BdExamRecord bdExamRecord){
		 bdExamRecordService.addBdQuestionCategory(bdExamRecord);
		 return  Result.OK("提交成功!");
	 }


	 /**
	  * 讲师考试任务-考试详情
	  * @return
	  */
	 @AutoLog(value = "历史考试记录-详情")
	 @ApiOperation(value="考试记录-详情", notes="考试记录-详情")
	 @GetMapping(value = "/examDetails")
	 public Result<?> examDetails (@RequestParam("examPaperId") String examPaperId,
	                             @RequestParam("trainTask_id") String trainTaskId){
		 ExamDetailsDTO examDetailsDTO = bdExamRecordService.examDetails(examPaperId,trainTaskId);
		 if (Objects.isNull(examDetailsDTO)){
			 return  Result.error("考试没有关联,查无此考试");
		 }
		 return  Result.OK(examDetailsDTO);
	 }

	 /**
	  * 考试计划查询
	  *
	  * @param bdExamRecord
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	@AutoLog(value = "考试计划-分页列表查询")
	@ApiOperation(value="考试计划-分页列表查询", notes="考试计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdExamRecord bdExamRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdExamRecord> queryWrapper = QueryGenerator.initQueryWrapper(bdExamRecord, req.getParameterMap());
		Page<BdExamRecord> page = new Page<BdExamRecord>(pageNo, pageSize);
		IPage<BdExamRecord> pageList = bdExamRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param bdExamRecord
	 * @return
	 */
	@AutoLog(value = "考试记录-添加")
	@ApiOperation(value="考试记录-添加", notes="考试记录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdExamRecord bdExamRecord) {
		List<BdExamRecord>  f=bdExamRecordMapper.selectList(new LambdaQueryWrapper<BdExamRecord>()
				.eq(BdExamRecord::getTrainTaskId,bdExamRecord.getTrainTaskId())
				.eq(BdExamRecord::getUserId,bdExamRecord.getUserId())
				.eq(BdExamRecord::getExamPaperId,bdExamRecord.getExamPaperId())
				.eq(BdExamRecord::getExamClassify,bdExamRecord.getExamClassify()));
		if (f.size()==1){return Result.OK("添加成功！");}
		bdExamRecordService.save(bdExamRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdExamRecord
	 * @return
	 */
	@AutoLog(value = "考试记录-编辑")
	@ApiOperation(value="考试记录-编辑", notes="考试记录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdExamRecord bdExamRecord) {
		bdExamRecordService.updateById(bdExamRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考试记录-通过id删除")
	@ApiOperation(value="考试记录-通过id删除", notes="考试记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdExamRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "考试记录-批量删除")
	@ApiOperation(value="考试记录-批量删除", notes="考试记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdExamRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考试记录-通过id查询")
	@ApiOperation(value="考试记录-通过id查询", notes="考试记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdExamRecord bdExamRecord = bdExamRecordService.getById(id);
		if(bdExamRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdExamRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdExamRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdExamRecord bdExamRecord) {
        return super.exportXls(request, bdExamRecord, BdExamRecord.class, "考试记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@AutoLog(value = "考试记录-批量导入excel")
	@ApiOperation(value="考试记录-批量导入excel", notes="考试记录-批量导入excel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<BdTrainTaskUser> list = ExcelImportUtil.importExcel(file.getInputStream(),BdTrainTaskUser.class,params);
				long start = System.currentTimeMillis();
				log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				return Result.ok(list);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件导入失败！");
    }


	 /**
	  * 导出名单excel
	  */
	 @AutoLog(value = "考试记录-导出名单excel")
	 @ApiOperation(value="考试记录-导出名单excel", notes="考试记录-导出名单excel")
	 @RequestMapping(value = "/BdTrainTaskUserXls")
	 public ModelAndView BdTrainTaskUserXls(@RequestParam("id")String id,
										    @RequestParam(value = "examPaperId",required = false)String examPaperId) {
		 // 1、查出所有考试的数据
		 List<BdTrainTaskUser> bdTrainTaskUsers = bdExamRecordService.resultList(id,examPaperId,1);
		 // 2、设置导出的相关的数据
		 String xlsName = "导出名单excel" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		 // 3、导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 // 4、导出文件名称
		 mv.addObject(NormalExcelConstants.FILE_NAME, xlsName);
		 mv.addObject(NormalExcelConstants.CLASS,BdTrainTaskUser.class);
		 // 5、设置数据
		 mv.addObject(NormalExcelConstants.DATA_LIST, bdTrainTaskUsers);
		 // 6、设置 ExportParams
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(xlsName, "导出名单excel"));
		 return mv;
	 }
}
