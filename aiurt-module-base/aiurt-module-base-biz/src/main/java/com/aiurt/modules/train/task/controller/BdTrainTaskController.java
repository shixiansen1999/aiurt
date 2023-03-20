package com.aiurt.modules.train.task.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.train.task.dto.*;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskSignMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskService;
import com.aiurt.modules.train.task.service.IBdTrainTaskSignService;
import com.aiurt.modules.train.task.vo.BdTrainTaskPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
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
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 培训任务
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="培训任务")
@RestController
@RequestMapping("/bdtraintask/bdTrainTask")
@Slf4j
public class BdTrainTaskController {
	@Autowired
	private IBdTrainTaskService bdTrainTaskService;
	@Autowired
	private IBdTrainTaskSignService bdTrainTaskSignService;
	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;
	@Autowired
	private BdTrainTaskSignMapper bdTrainTaskSignMapper;
	@Autowired
	private BdTrainTaskUserMapper bdTrainTaskUserMapper;
	 @Autowired
	 private ISysBaseAPI iSysBaseAPI;
	/**
	 * 分页列表查询
	 *
	 * @param bdTrainTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @autor lkj
	 * @return
	 */
	@AutoLog(value = "培训任务-分页列表查询")
	@ApiOperation(value="培训任务-分页列表查询", notes="培训任务-分页列表查询")
	@GetMapping(value = "/list")
	@ApiResponses({
			@ApiResponse(code = 200,message = "OK",response = BdTrainTask.class)
	})
	public Result<?> queryPageList(BdTrainTask bdTrainTask,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BdTrainTask> pageList = new Page<>(pageNo, pageSize);
		Page<BdTrainTask> bdTrainTaskPage = bdTrainTaskService.queryPageList(pageList, bdTrainTask);
		return Result.OK(bdTrainTaskPage);
	}

	/**
	 *   添加
	 *
	 * @param bdTrainTaskPage
	 * @autor lkj
	 * @return
	 */
	@AutoLog(value = "培训任务-添加")
	@ApiOperation(value="培训任务-添加", notes="培训任务-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainTaskPage bdTrainTaskPage) {
		BdTrainTask bdTrainTask = new BdTrainTask();
		BeanUtils.copyProperties(bdTrainTaskPage, bdTrainTask);
		bdTrainTask.setNumber(0);
		bdTrainTask.setTaskState(0);
		if (bdTrainTask.getExamStatus()==0) {
			bdTrainTask.setMakeUpState(0);
			bdTrainTask.setStudyResourceState(0);
		}
		SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(bdTrainTask.getTaskTeamId());
		bdTrainTask.setTaskTeamCode(sysDepartModel.getOrgCode());
		bdTrainTaskService.saveMain(bdTrainTask, bdTrainTaskPage.getBdTrainTaskSignList());
		List<String> userIds = bdTrainTask.getUserIds();
		bdTrainTaskService.addTrainTaskUser(bdTrainTask.getId(),bdTrainTask.getTaskTeamId(),userIds);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdTrainTaskPage
	 * @return
	 * @autor lkj
	 */
	@AutoLog(value = "培训任务-编辑")
	@ApiOperation(value="培训任务-编辑", notes="培训任务-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainTaskPage bdTrainTaskPage) {
		return bdTrainTaskService.edit(bdTrainTaskPage);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训任务-通过id删除")
	@ApiOperation(value="培训任务-通过id删除", notes="培训任务-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainTaskService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训任务-批量删除")
	@ApiOperation(value="培训任务-批量删除", notes="培训任务-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainTaskService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 * @autor lkj
	 */
	@AutoLog(value = "培训任务-通过id查询")
	@ApiOperation(value="培训任务-通过id查询", notes="培训任务-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainTask bdTrainTask = bdTrainTaskService.getById(id);
		if(bdTrainTask==null) {
			return Result.error("未找到对应数据");
		}
		bdTrainTask.setTeacherName(bdTrainTaskMapper.getName(bdTrainTask.getTeacherId()));
		bdTrainTask.setTaskTeamName(bdTrainTaskMapper.getTeamName(bdTrainTask.getTaskTeamId()));
		return Result.OK(bdTrainTask);
	}
	/**
	 * 通过id查询
	 *
	 * @param id 培训任务id
	 * @return
	 */
	@AutoLog(value = "培训签到记录通过主表ID查询")
	@ApiOperation(value="培训签到记录主表ID查询", notes="培训签到记录-通主表ID查询")
	@GetMapping(value = "/queryBdTrainTaskSignByMainId")
	public Result<?> queryBdTrainTaskSignListByMainId(@RequestParam(name="id",required=true) String id) {
		List<BdTrainTaskSign> bdTrainTaskSignList = bdTrainTaskSignService.selectByMainId(id);
		return Result.OK(bdTrainTaskSignList);
	}

	 /**
	  * 通过id查询
	  * @param id 培训任务id
	  * @autor lkj
	  * @return
	  */
	 @AutoLog(value = "培训签到记录查询")
	 @ApiOperation(value="培训签到记录查询", notes="培训签到记录查询")
	 @GetMapping(value = "/getBdTrainTaskSignListByMainId")
	 public Result<?> getBdTrainTaskSignListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<BdTrainSignDTO> trainSignDTOs = bdTrainTaskSignService.getById(id);
		 return Result.OK(trainSignDTOs);
	 }

	 /**
	  * 学员培训签到
	  *
	  * @param bdTrainTaskSign
	  * @return
	  * @autor lkj
	  */
	 @AutoLog(value = "学员培训签到")
	 @ApiOperation(value = "学员培训签到", notes = "学员培训签到")
	 @PostMapping(value = "/stuTrainTaskSign")
	 public Result<?> stuTrainTaskSign(@RequestBody BdTrainTaskSign bdTrainTaskSign) {
		 if (!bdTrainTaskSign.getTrainTaskId().equals(bdTrainTaskSign.getTaskId())) {
			 return Result.OK("签到失败，请核对培训任务");
		 }
		 BdTrainTaskSign sign = bdTrainTaskSignMapper.getSign(bdTrainTaskSign.getTrainTaskId(), bdTrainTaskSign.getUserId());
		 if (ObjectUtil.isNotNull(sign)) {
			 return Result.OK("已签到");
		 }
		bdTrainTaskSign.setNumber(bdTrainTaskSign.getNumber());
		bdTrainTaskSign.setStateSign(0);
		bdTrainTaskSignMapper.insert(bdTrainTaskSign);
	 	bdTrainTaskUserMapper.updateSignState(bdTrainTaskSign.getUserId(), bdTrainTaskSign.getTrainTaskId());
	 	return Result.OK("签到成功");
	 }


	 /**
	 * 讲师授课任务-任务查询
	 * @param taskName
	 * @return
	 * @autor lkj
	 * */
	@AutoLog(value = "讲师授课任务-任务查询")
	@ApiOperation(value="讲师授课任务-任务查询", notes="讲师授课任务-任务查询")
	@GetMapping(value = "/getTeacherTaskById")
	public Result<?> getTeacherTaskById(@RequestParam(name="taskName",required=false) String taskName,
										@RequestParam(name="taskId",required=false) String taskId,
										@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Page<BdTrainTask> pageList = new Page<>(pageNo, pageSize);
		Page<BdTrainTask> task = bdTrainTaskService.getTaskById(pageList, sysUser.getId(), taskName,taskId);
		return Result.OK(task);
	}

	 /**
	  * 讲师授课任务-任务查询
	  * @return
	  * @autor zwl
	  * */
	 @AutoLog(value = "讲师授课任务-任务查询-web")
	 @ApiOperation(value="讲师授课任务-任务查询-web", notes="讲师授课任务-任务查询-web")
	 @PostMapping(value = "/getTeacherTaskByIds")
	 public Result<?> getTeacherTaskByIds(@RequestBody BdTrainTask condition) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 condition.setTeacherId(sysUser.getId());
		 if (condition.getPageNo()==null||condition.getPageSize()==null){
			 condition.setPageNo(1);
			 condition.setPageSize(10);
		 }
		 Page<BdTrainTask> pageList = new Page<>(condition.getPageNo(), condition.getPageSize());
		 Page<BdTrainTask> task = bdTrainTaskService.getTaskByIds(pageList,condition);
		 return Result.OK(task);
	 }

	 /**
	  *  培训任务-状态更改
	  *
	  * @param bdTrainTaskPage
	  * @return
	  * @autor lkj
	  */
	 @AutoLog(value = "培训任务-状态更改")
	 @ApiOperation(value="培训任务-状态更改", notes="培训任务-状态更改")
	 @PutMapping(value = "/updateTaskState")
	 public Result<?> updateTaskState(@RequestBody  BdTrainTaskPage bdTrainTaskPage) {
		 BdTrainTask bdTrainTask = new BdTrainTask();
		 BeanUtils.copyProperties(bdTrainTaskPage, bdTrainTask);
		 bdTrainTaskService.updateTaskState(bdTrainTask);
		 return Result.OK("修改成功!");
	 }

	 /**
	  * 学生培训任务-任务查询
	  * @param
	  * @return
	  * @autor lkj
	  * */
	 @AutoLog(value = "学生培训任务-任务查询")
	 @ApiOperation(value="学生培训任务-任务查询", notes="学生培训任务-任务查询")
	 @GetMapping(value = "/getStudentTaskById")
	 public Result<?> getStudentTaskById(@RequestParam(name="taskName",required=false) String taskName,
										 @RequestParam(name="taskId",required=false) String taskId,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 Page<BdTrainTaskUser> pageList = new Page<>(pageNo, pageSize);
		 Page<BdTrainTaskUser> userTasks = bdTrainTaskService.getUserTasks(pageList, sysUser.getId(), taskName,taskId);
		 return Result.OK(userTasks);
	 }

	 /**
	  * web
	  * 学生培训任务-任务查询
	  * @param
	  * @return
	  * @autor hlq
	  * */
	 @AutoLog(value = "学生培训任务-任务查询-web")
	 @ApiOperation(value="学生培训任务-任务查询-web", notes="学生培训任务-任务查询-web")
	 @ApiResponses({
			 @ApiResponse(code = 200,message = "OK",response = BdTrainTask.class),
			 @ApiResponse(code = 200,message = "OK",response = BdTrainTaskUser.class)
	 })
	 @GetMapping(value = "/getUserTasksWeb")
	 public Result<?> getUserTasksWeb (BdTrainTaskUser bdTrainTaskUser,
									   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 Page<BdTrainTaskUser> pageList = new Page<>(pageNo, pageSize);
		 Page<BdTrainTaskUser> userTasks = bdTrainTaskService.getUserTasksWeb(pageList, sysUser.getId(), bdTrainTaskUser);
		 return Result.OK(userTasks);
	 }

	 /**
	  * 讲师授课任务-授课记录(已关闭)-列表查询
	  * @param
	  * @author hlq
	  */
	 @AutoLog(value = "讲师授课任务-授课记录(已关闭)-列表查询")
	 @ApiOperation(value="讲师授课任务-授课记录(已关闭)-列表查询", notes="讲师授课任务-授课记录(已关闭)-列表查询")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = BdTrainTask.class),
	 })
	 @GetMapping(value = "/getTeachTaskQuery")
	 public Result<?> getTeachTaskQuery(BdTrainTask bdTrainTask,
										@RequestParam(name="actualTrainingTime", required = false) String actualTrainingTime,
										@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 Page<BdTrainTask> pageList = new Page<>(pageNo, pageSize);
		 Page<BdTrainTask> bdTrainTaskList = bdTrainTaskService.queryList(pageList,sysUser.getId(),bdTrainTask,actualTrainingTime);
		 return Result.OK(pageList);
	 }

	 /**
	  * 培训台账-分页列表查询
	  *
	  * @param bdTrainTask
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @autor hlq
	  * @return
	  */
	 @AutoLog(value = "培训台账-分页列表查询")
	 @ApiOperation(value="培训台账-分页列表查询", notes="培训台账-分页列表查询")
	 @GetMapping(value = "/trainingLedger")
	 @PermissionData(pageComponent = "trainAss/trainAccount/BdTrainAccountList")
	 public Result<?> trainingLedger(BdTrainTask bdTrainTask,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 Page<BdTrainTask> pageList = new Page<>(pageNo, pageSize);
		 Page<BdTrainTask> bdTrainTasks = bdTrainTaskService.queryTrainingLedger(pageList,bdTrainTask);
		 return Result.OK(bdTrainTasks);
	 }

	 /**
	  * 查询成绩汇总表
	  * @param
	  * @return
	  */
	 @AutoLog(value = "/queryResults")
	 @ApiOperation(value="成绩汇总表", notes="成绩汇总表")
	 @GetMapping(value = "/queryResults")
	 public Result<?> queryResults(@RequestParam(name="trainTaskId") String trainTaskId) {
	 	BdTrainTaskDTO bdTrainTaskDTO = bdTrainTaskService.queryResults(trainTaskId);
		 return Result.OK(bdTrainTaskDTO);
	 }
	 /**
	  * 讲师反馈表
	  * @param
	  * @return
	  */
	 @AutoLog(value = "讲师反馈表")
	 @ApiOperation(value="讲师反馈表", notes="讲师反馈表")
	 @GetMapping(value = "/instructorFeedback")
	 public Result<?> instructorFeedback(@RequestParam(name="trainTaskId") String trainTaskId,
										 @RequestParam(name="userId") String userId) {
		 TeacherFeedbackDTO teacherFeedbackDTO = bdTrainTaskService.instructorFeedback(trainTaskId,userId);

		 return Result.OK(teacherFeedbackDTO);
	 }
	 /**
	  * 学生培训任务-培训查询列表
	  * @param
	  * @return
	  */
	 @AutoLog(value = "培训查询列表")
	 @ApiOperation(value="培训查询列表", notes="培训查询列表")
	 @GetMapping(value = "/trainQueryTable")
	 public Result<?> trainQueryTable(@RequestParam(name="userId",required=true) String userId,
									  @RequestParam(name ="signState",required = false) Integer signState,
									  @RequestParam(name = "startTime",required = false) String startTime,
									  @RequestParam(name = "taskName",required = false) String taskName,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
		 Page<TrainQueryTableDTO> pageList = new Page<>(pageNo, pageSize);
		Page<TrainQueryTableDTO>trainQueryTableDTOs=  bdTrainTaskService.trainQueryTable(pageList,userId,signState,startTime,taskName);
		 return Result.OK(trainQueryTableDTOs);
	 }
	 /**
	  * 学生培训任务-反馈详情
	  * @param
	  * @return
	  */
	 @AutoLog(value = "反馈详情")
	 @ApiOperation(value="反馈详情", notes="反馈详情")
	 @GetMapping(value = "/feedBackDetails")
	 public Result<?> feedBackDetails(@RequestParam(name="id",required=true) String id ,@RequestParam(name="userId",required=true) String userId) {
		 FeedBackDetailsDTO feedBackDetailsDTO = bdTrainTaskService.feedBackDetails(id,userId);
		 if (Objects.isNull(feedBackDetailsDTO)){
		 	return Result.OK("查无此数据");
		 }
		 return Result.OK(feedBackDetailsDTO);
	 }
	 /**
	  * 学生培训任务-详情
	  * @param
	  * @return
	  */
	 @AutoLog(value = "学生培训任务-详情")
	 @ApiOperation(value="学生培训任务-详情", notes="学生培训任务-详情")
	 @GetMapping(value = "/StudentTrainingDetails")
	 public Result<?> studentTrainingDetails(@RequestParam(name="id",required=true) String id) {
	 	BdTrainTaskDTO bdTrainTaskDTO = bdTrainTaskService.studentTrainingDetails(id);
		 return Result.OK(bdTrainTaskDTO);
	 }

	 /**
	  * 培训任务管理-查询签到人员
	  * @param
	  * @return
	  */
	 @AutoLog(value = "培训任务管理-查询签到人员")
	 @ApiOperation(value="培训任务管理-查询签到人员", notes="培训任务管理-查询签到人员")
	 @GetMapping(value = "/querySignPeople")
	 public Result<?> querySignPeople(@RequestParam(name="trainTaskId",required=true) String trainTaskId,
									  @RequestParam(name = "signState",required = false) Integer signState,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
		 Page<SignPeopleDTO> pageList = new Page<>(pageNo, pageSize);
		 Page<SignPeopleDTO> list=  bdTrainTaskService.querySignPeople(pageList,trainTaskId,signState);
		 return Result.OK(list);
	 }
	 /**
	  * 补签
	  * @param
	  * @return
	  */
	 @AutoLog(value = "培训任务管理-补签")
	 @ApiOperation(value="培训任务管理-补签", notes="培训任务管理-补签")
	 @PostMapping(value = "/repairSing")
	 public Result<?> repairSing(@RequestBody(required=true) SignPeopleDTO signPeopleDTO){
	 	BdTrainTaskSign bdTrainTaskSign = new BdTrainTaskSign();
		 bdTrainTaskSign.setTrainTaskId(signPeopleDTO.getTrainTaskId());
		 bdTrainTaskSign.setUserId(signPeopleDTO.getUserId());
		 bdTrainTaskSign.setUserName(bdTrainTaskMapper.getName(signPeopleDTO.getUserId()));
		 bdTrainTaskSignService.save(bdTrainTaskSign);
         bdTrainTaskService.repairSing(signPeopleDTO);
		 return Result.OK("补签成功");
	 }
	 /**
	  * 获取签到轮数
	  */
	 @AutoLog(value = "获取签到轮数")
	 @ApiOperation(value="获取签到轮数", notes="获取签到轮数")
	 @GetMapping(value = "/getSignNumber")
	 public Result<?> getSignNumber(@RequestParam(name="trainTaskId",required=true) String trainTaskId,
									@RequestParam(name = "signTime",required = true) String signTime){
	 	Integer number = bdTrainTaskService.getSignNumber(trainTaskId,signTime);
	 	return Result.OK(number);
	 }
	 /**
	  * 登录身份获取app
	  * @param
	  * @return
	  */
	 @AutoLog(value = "登录身份获取app")
	 @ApiOperation(value="登录身份获取app", notes="登录身份获取app")
	 @GetMapping(value = "/getRole")
	 public Result<?> getRole(@RequestParam(name="id",required=true) String id) {

		 String teacherRole = bdTrainTaskMapper.getTeacherRole(id);
		 String studentRole = bdTrainTaskMapper.getStudentRole(id);
		 List<Boolean> list = new ArrayList<Boolean>();
		 //如果有讲师身份，list第一个为true，如果有学员身份，第二个为true
		 if (ObjectUtil.isNull(teacherRole)) {
			 list.add(false);
		 } else {
			 list.add(true);
		 }
		 if (ObjectUtil.isNull(studentRole)) {
			 list.add(false);
		 } else {
			 list.add(true);
		 }
		 return Result.OK(list);
	 }

	 /**
	  * 查看参训人员
	  *
	  * @param bdTrainTask
	  * @param pageNo
	  * @param pageSize
	  * @autor lkj
	  * @return
	  */
	 @AutoLog(value = "培训任务-查看参训人员")
	 @ApiOperation(value="培训任务-查看参训人员", notes="培训任务-查看参训人员")
	 @GetMapping(value = "/getTrainees")
	 @ApiResponses({
			 @ApiResponse(code = 200,message = "OK",response = BdTrainTask.class)
	 })
	 public Result<?> getTrainees(BdTrainTask bdTrainTask,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 Page<UserDTO> pageList = new Page<>(pageNo, pageSize);
		 Page<UserDTO> users = bdTrainTaskService.getTrainees(pageList, bdTrainTask);
		 return Result.OK(users);
	 }


	 /**
	  * 获取角色为老师的用户
	  *
	  * @param userDTO
	  * @param pageNo
	  * @param pageSize
	  * @autor lkj
	  * @return
	  */
	 @AutoLog(value = "培训任务-获取角色为老师的用户")
	 @ApiOperation(value="培训任务-获取角色为老师的用户", notes="培训任务-获取角色为老师的用户")
	 @GetMapping(value = "/getTrainTeacher")
	 @ApiResponses({
			 @ApiResponse(code = 200,message = "OK",response = BdTrainTask.class)
	 })
	 public Result<?> getTrainTeacher(UserDTO userDTO,
								  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 Page<UserDTO> pageList = new Page<>(pageNo, pageSize);
		 Page<UserDTO> trainTeacher = bdTrainTaskService.getTrainTeacher(pageList, userDTO);
		 return Result.OK(trainTeacher);
	 }


	 /**
    * 导出excel
    *
    * @param request
    * @param bdTrainTask
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainTask bdTrainTask) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<BdTrainTask> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainTask, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<BdTrainTask> queryList = bdTrainTaskService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<BdTrainTask> bdTrainTaskList = new ArrayList<BdTrainTask>();
      if(oConvertUtils.isEmpty(selections)) {
          bdTrainTaskList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          bdTrainTaskList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<BdTrainTaskPage> pageList = new ArrayList<BdTrainTaskPage>();
      for (BdTrainTask main : bdTrainTaskList) {
          BdTrainTaskPage vo = new BdTrainTaskPage();
          BeanUtils.copyProperties(main, vo);
          List<BdTrainTaskSign> bdTrainTaskSignList = bdTrainTaskSignService.selectByMainId(main.getId());
          vo.setBdTrainTaskSignList(bdTrainTaskSignList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "培训任务列表");
      mv.addObject(NormalExcelConstants.CLASS, BdTrainTaskPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("培训任务数据", "导出人:"+sysUser.getRealname(), "培训任务"));
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
              List<BdTrainTaskPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BdTrainTaskPage.class, params);
              for (BdTrainTaskPage page : list) {
                  BdTrainTask po = new BdTrainTask();
                  BeanUtils.copyProperties(page, po);
                  bdTrainTaskService.saveMain(po, page.getBdTrainTaskSignList());
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
