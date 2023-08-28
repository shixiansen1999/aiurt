package com.aiurt.modules.train.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.feedback.entity.*;
import com.aiurt.modules.train.feedback.mapper.*;
import com.aiurt.modules.train.quzrtz.QuartzServiceImpl;
import com.aiurt.modules.train.quzrtz.job.CronUtlit;
import com.aiurt.modules.train.task.dto.*;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.*;
import com.aiurt.modules.train.task.service.IBdTrainTaskService;
import com.aiurt.modules.train.task.service.IBdTrainTaskSignService;
import com.aiurt.modules.train.task.vo.BdTrainTaskPage;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.aiurt.modules.train.trainarchive.service.ITrainArchiveService;
import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.aiurt.modules.train.trainrecord.service.ITrainRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @Description: 培训任务
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdTrainTaskServiceImpl extends ServiceImpl<BdTrainTaskMapper, BdTrainTask> implements IBdTrainTaskService {

	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;
	@Autowired
	private BdTrainTaskSignMapper bdTrainTaskSignMapper;
	@Autowired
	private BdTrainTaskUserMapper bdTrainTaskUserMapper;
	@Autowired
	private IBdTrainTaskSignService bdTrainTaskSignService;
	@Autowired
	private BdExamPaperMapper bdExamPaperMapper;
	@Autowired
	private BdExamRecordMapper examRecordMapper;
	@Autowired
	private BdTrainQuestionFeedbackRecordMapper bdTrainQuestionFeedbackRecordMapper;
	@Autowired
	private BdTrainQuestionFeedbackOptionsRecordMapper bdTrainQuestionFeedbackOptionsRecordMapper;
	@Autowired
	private BdTrainQuestionFeedbackQuesRecordMapper bdTrainQuestionFeedbackQuesRecordMapper;
	@Autowired
	private BdTrainTeacherFeedbackRecordMapper bdTrainTeacherFeedbackRecordMapper;
	@Autowired
	private BdTrainQuestionFeedbackOptionsMapper bdTrainQuestionFeedbackOptionsMapper;
	@Autowired
	private BdTrainQuestionFeedbackQuesMapper bdTrainQuestionFeedbackQuesMapper;
	@Autowired
	private BdTrainStudentFeedbackRecordMapper bdTrainStudentFeedbackRecordMapper;

	@Autowired
	private QuartzServiceImpl quartzService;
	@Autowired
	private ISysBaseAPI iSysBaseAPI;
	@Autowired
	private ITrainArchiveService archiveService;
	@Autowired
	private ITrainRecordService recordService;
	@Autowired
	private ISysParamAPI iSysParamAPI;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(BdTrainTask bdTrainTask, List<BdTrainTaskSign> bdTrainTaskSignList) {
		String formatTaskCode = taskCode(bdTrainTask.getTrainLine());
		bdTrainTask.setTaskCode(formatTaskCode);
		bdTrainTaskMapper.insert(bdTrainTask);
		if(bdTrainTaskSignList!=null && bdTrainTaskSignList.size()>0) {
			for(BdTrainTaskSign entity:bdTrainTaskSignList) {
				//外键设置
				entity.setTrainTaskId(bdTrainTask.getId());
				bdTrainTaskSignMapper.insert(entity);
			}
		}
	}
public String taskCode(Integer trainLine){
	Integer year = DateUtil.year(new Date());
	Integer month = DateUtil.month(new Date())+1;
	SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.TRAIN_TASK_CODE);
	String value = sysParamModel.getValue();
	if("0".equals(value)){
		trainLine = month;
	}
	String taskCode = "YY-THXH-"+trainLine+"-"+year+"-";
	String formatTaskCode = "";
	List<BdTrainTask> bdTrainTasks = bdTrainTaskMapper.selectList(new LambdaQueryWrapper<BdTrainTask>());
	if(CollUtil.isNotEmpty(bdTrainTasks)){
		List<String> taskCodes = bdTrainTasks.stream().filter(e->ObjectUtil.isNotEmpty(e.getTaskCode())).map(BdTrainTask::getTaskCode).collect(Collectors.toList());
		if(CollUtil.isNotEmpty(taskCodes)){
			Integer number = 1;
			do{
				String format = String.format("%02d",number );
				formatTaskCode = taskCode+format;
				number++;
			}
			while (taskCodes.contains(formatTaskCode));
		}else {
			formatTaskCode = taskCode+"01";
		}
	} else {
		formatTaskCode = taskCode+"01";
	}
	return  formatTaskCode;
}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(BdTrainTask bdTrainTask,List<BdTrainTaskSign> bdTrainTaskSignList) {
		bdTrainTaskMapper.updateById(bdTrainTask);

		//1.先删除子表数据
		bdTrainTaskSignMapper.deleteByMainId(bdTrainTask.getId());

		//2.子表数据重新插入
		if(bdTrainTaskSignList!=null && bdTrainTaskSignList.size()>0) {
			for(BdTrainTaskSign entity:bdTrainTaskSignList) {
				//外键设置
				entity.setTrainTaskId(bdTrainTask.getId());
				bdTrainTaskSignMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> edit(BdTrainTaskPage bdTrainTaskPage) {
		BdTrainTask bdTrainTask = new BdTrainTask();
		BeanUtils.copyProperties(bdTrainTaskPage, bdTrainTask);
		BdTrainTask bdTrainTaskEntity = this.getById(bdTrainTask.getId());
		if(bdTrainTaskEntity==null) {
			return Result.error("未找到对应数据");
		}
		//待发布的修改
		if (bdTrainTask.getTaskState() == 0 ) {
			//人员有变化
			if (CollectionUtil.isNotEmpty(bdTrainTask.getUserIds())) {
				bdTrainTaskUserMapper.deleteByMainId(bdTrainTask.getId());
				List<String> userIds = bdTrainTask.getUserIds();
				this.addTrainTaskUser(bdTrainTask.getId(), bdTrainTask.getTaskTeamId(), userIds);
			}
			//是否考试有变化
			if (bdTrainTask.getExamStatus()!= null && bdTrainTask.getExamStatus()==0) {
				bdTrainTask.setMakeUpState(0);
				bdTrainTask.setStudyResourceState(0);
			}
			String formatTaskCode = taskCode(bdTrainTask.getTrainLine());
			bdTrainTask.setTaskCode(formatTaskCode);
		}
		//发布
		if (bdTrainTask.getTaskState() == 1) {
			//复制反馈表
			//获取正在启用的讲师反馈表和对应的问题反馈单选项和问题反馈问题
			BdTrainQuestionFeedback teacherFeedback = bdTrainTeacherFeedbackRecordMapper.getBdTrainQuestionFeedbackId();
			if (ObjectUtil.isEmpty(teacherFeedback)) {
				return Result.error("没有正在启用的讲师反馈表");
			}
			teacherFeedback.setTrainTaskId(bdTrainTask.getId());
			copyDetail(teacherFeedback);
			//获取正在启用的学员反馈表和对应的问题反馈单选项和问题反馈问题
			BdTrainQuestionFeedback studentFeedback = bdTrainStudentFeedbackRecordMapper.getBdTrainQuestionFeedbackId();
			if (ObjectUtil.isEmpty(studentFeedback)) {
				return Result.error("没有正在启用的学员反馈表");
			}
			studentFeedback.setTrainTaskId(bdTrainTask.getId());
			copyDetail(studentFeedback);
		}
		//开始考试
		if (bdTrainTask.getTaskState() == 4) {
			Date time1 = new Date();
			if (bdTrainTaskEntity.getExamValidityPeriod() != null && bdTrainTask.getStartExamTime() != null) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(bdTrainTask.getStartExamTime());
				calendar.add(Calendar.DATE, bdTrainTaskEntity.getExamValidityPeriod());
				time1 = calendar.getTime();
			}
			//创建定时任务对象
			QuartzJobDTO quartzJobDTO = new QuartzJobDTO();
			quartzJobDTO.setParameter(bdTrainTask.getId());
			// 计算触发时间
			quartzJobDTO.setCronExpression(CronUtlit.getCron(time1));
			quartzService.test(quartzJobDTO);
			//保存定时任务id
			bdTrainTask.setQuartzJobId(quartzJobDTO.getId());
		}
		this.updateById(bdTrainTask);
		return Result.OK("编辑成功!");
	}

	private void constructArchive(BdTrainTask trainTask) {
		List<TrainRecord> trainRecords = new ArrayList<>();
		List<BdTrainTaskUser> list = examRecordMapper.userList(trainTask.getId());
		List<TrainArchive> archiveList = archiveService.list(new LambdaQueryWrapper<TrainArchive>().eq(TrainArchive::getDelFlag, CommonConstant.DEL_FLAG_0));
		Map<String, TrainArchive> archiveMap = archiveList.stream().collect(Collectors.toMap(TrainArchive::getUserId, Function.identity()));
		if(CollUtil.isNotEmpty(list)){
			list.forEach(e -> {
				TrainArchive archive = archiveMap.get(e.getUserId());
				if(ObjectUtil.isNotEmpty(archive)){
					TrainRecord trainRecord = new TrainRecord();
					trainRecord.setTrainArchiveId(archive.getId());
					trainRecord.setTrainTime(trainTask.getStartDate());
					trainRecord.setTaskGrade(trainTask.getTaskGrade());
					trainRecord.setIsAnnualPlan(trainTask.getIsAnnualPlan());
					trainRecord.setTrainContent(trainTask.getPlanSubName());
					trainRecord.setHour(Integer.valueOf(String.valueOf(trainTask.getTaskHours())));
					trainRecord.setTaskCode(trainTask.getTaskCode());
					trainRecord.setTrainTaskId(trainTask.getId());
					if(0==trainTask.getExamStatus()){
						trainRecord.setCheckGrade("无考核");
					}
					trainRecords.add(trainRecord);
				}

			});
			if(CollUtil.isNotEmpty(trainRecords)){
				recordService.saveBatch(trainRecords);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		BdTrainTask bdTrainTask = this.getById(id);
		Integer taskState = bdTrainTask.getTaskState();
		if (taskState == 0) {
			bdTrainTaskUserMapper.deleteByMainId(id);
			bdTrainTaskSignMapper.deleteByMainId(id);
			bdTrainTaskMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			BdTrainTask bdTrainTask = this.getById(id);
			Integer taskState = bdTrainTask.getTaskState();
			if (taskState == 0) {
				bdTrainTaskUserMapper.deleteByMainId(id.toString());
				bdTrainTaskSignMapper.deleteByMainId(id.toString());
				bdTrainTaskMapper.deleteById(id);
			}
		}
	}
	@Override
	public Page<TrainQueryTableDTO> trainQueryTable(Page<TrainQueryTableDTO> pageList, String userId, Integer signState, String startTime, String taskName){
		Page<TrainQueryTableDTO>trainQueryTableDTOs=bdTrainTaskMapper.selectByUserId(pageList,userId,signState,startTime,taskName);
		return trainQueryTableDTOs;
	}
	@Override
	public BdTrainTaskDTO studentTrainingDetails(String id){
		BdTrainTaskDTO bdTrainTaskDTO =bdTrainTaskMapper.studentTrainingDetails(id);
		return bdTrainTaskDTO;
	}

	@Override
	public Page<SignPeopleDTO> querySignPeople(Page<SignPeopleDTO> pageList, String trainTaskId, Integer signState) {
		Page<SignPeopleDTO> list = bdTrainTaskMapper.querySignPeople(pageList,trainTaskId,signState);
		return list;
	}

	@Override
	public void repairSing(SignPeopleDTO signPeopleDTO) {
		signPeopleDTO.setSignState(1);
		signPeopleDTO.setStateSign(1);
		bdTrainTaskMapper.updateSign(signPeopleDTO);
	}

	@Override
	public Page<BdTrainTask> getTaskById(Page<BdTrainTask> pageList,String id, String taskName,String taskId) {
		List<BdTrainTask> trainTasks = bdTrainTaskMapper.getTaskById(pageList,id,taskName,taskId);
		return getBdTrainTaskPage(pageList, trainTasks);
	}

	@Override
	public Page<BdTrainTask> getTaskByIds(Page<BdTrainTask> pageList, BdTrainTask condition) {
		Date date = null;
		if (condition.getTrainingDateRange()!=null){
			String trainingDateRange = condition.getTrainingDateRange();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				date= simpleDateFormat.parse(trainingDateRange);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		List<BdTrainTask> trainTasks = bdTrainTaskMapper.getTaskByIds(pageList,condition,date);
		trainTasks.forEach(e -> {
			String name = bdTrainTaskMapper.getName(e.getTeacherId());
			e.setTeacherName(name);
		});
		return getBdTrainTaskPage(pageList, trainTasks);
	}

	private Page<BdTrainTask> getBdTrainTaskPage(Page<BdTrainTask> pageList, List<BdTrainTask> trainTasks) {
		if (CollectionUtil.isNotEmpty(trainTasks)) {
			for (BdTrainTask bdTrainTask : trainTasks) {
				String trainTaskId = bdTrainTask.getId();
				String teamName = bdTrainTaskMapper.getTeamName(bdTrainTask.getTaskTeamId());
				//获取状态
				getTrainingState(bdTrainTask);
				BdTrainTaskUser bdTrainTaskUser = bdTrainTaskMapper.getBdTrainTaskUser(bdTrainTask.getId());
				bdTrainTask.setSignedInNum(bdTrainTaskUser.getSignedInNum());
				bdTrainTask.setNotSignedInNum(bdTrainTaskUser.getNotSignedInNum());
				bdTrainTask.setNumberTrainees(bdTrainTaskUser.getSignedInNum()+bdTrainTaskUser.getNotSignedInNum());
				bdTrainTask.setTaskTeamName(teamName);
				bdTrainTask.setNumber(bdTrainTask.getNumber());
				//判断是否关联考试
				if (bdTrainTask.getExamStatus().equals(1)) {
					//计算及格和未及格人数
					List<BdExamRecord> bdExamRecords = examRecordMapper.examUserList( bdTrainTask.getId());
					List<BdExamRecord> isPassList = bdExamRecords.stream().filter(e -> e.getIsPass() != null && e.getIsPass()==1).collect(Collectors.toList());
					List<BdExamRecord> isNotPassList = bdExamRecords.stream().filter(e -> e.getIsPass() != null && e.getIsPass()==0).collect(Collectors.toList());
					bdTrainTask.setPassNumber(isPassList.size());
					bdTrainTask.setNotPassNumber(isNotPassList.size());
					bdTrainTask.setAbsentPassNumber(bdTrainTask.getNumberTrainees() - isPassList.size() - isNotPassList.size());
				}
			}
		}
		return pageList.setRecords(trainTasks);
	}

	@Override
	public void updateTaskState(BdTrainTask bdTrainTask) {
		BdTrainTask trainTask = bdTrainTaskMapper.selectById(bdTrainTask.getId());
		if (ObjectUtil.isNotNull(bdTrainTask.getStartTime())) {
			//开始培训
			trainTask.setStartTime(bdTrainTask.getStartTime());
			trainTask.setTaskState(2);
			trainTask.setStopState(0);
			trainTask.setNumber(trainTask.getNumber() + 1);
			constructArchive(trainTask);
		} else if (ObjectUtil.isNotNull(bdTrainTask.getStopState())) {
			//继续,暂停培训
			if (bdTrainTask.getStopState().equals(0)) {
				//继续培训轮数加一
				trainTask.setNumber(trainTask.getNumber() + 1);
			}
			trainTask.setStopState(bdTrainTask.getStopState());
		} else if (ObjectUtil.isNotNull(bdTrainTask.getEndTime())) {
			//关闭培训
			trainTask.setEndTime(bdTrainTask.getEndTime());
			if (trainTask.getExamStatus().equals(0)) {
				trainTask.setTaskState(6);
			}else{
				trainTask.setTaskState(3);
			}
		}
		bdTrainTaskMapper.updateById(trainTask);
	}
	@Override
	public Page<BdTrainTaskUser> getUserTasks(Page<BdTrainTaskUser> pageList,String id,String taskName,String taskId) {
		List<BdTrainTaskUser> userTasks = bdTrainTaskUserMapper.getUserTasks(pageList,id,taskName,taskId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		if (CollectionUtil.isNotEmpty(userTasks)) {
			for (BdTrainTaskUser bdTrainTaskUser : userTasks) {
				//获取任务信息
				BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(bdTrainTaskUser.getTrainTaskId());
				String teacherName = bdTrainTaskMapper.getName(bdTrainTask.getTeacherId());
				String teamName = bdTrainTaskMapper.getTeamName(bdTrainTask.getTaskTeamId());
				bdTrainTask.setTeacherName(teacherName);
				bdTrainTask.setTaskTeamName(teamName);
				bdTrainTask.setNumber(bdTrainTask.getNumber());
				String startDate = dateFormat.format(bdTrainTask.getStartDate());
				String endDate = dateFormat.format(bdTrainTask.getEndDate());
				String startDateR = (new StringBuilder()).append(startDate).append("-").append(endDate).toString();;
				bdTrainTask.setTrainingDateRange(startDateR);
				bdTrainTaskUser.setBdTrainTask(bdTrainTask);
				bdTrainTaskUser.setTrainingState(bdTrainTask.getTrainingState());
				bdTrainTaskUser.setClassify(bdTrainTask.getClassify());
				//获取状态
				getTrainingState(bdTrainTask);
				if (bdTrainTaskUser.getSignState().equals(1)) {
					Date signTime = bdTrainTaskMapper.getSignTime(id, bdTrainTask.getId());
					bdTrainTaskUser.setSignTime(signTime);
				}
				if (bdTrainTaskUser.getFeedState().equals(1)) {
					Date completeTime = bdTrainTaskMapper.getCompleteTime(id, bdTrainTask.getId());
					bdTrainTaskUser.setCompleteTime(completeTime);
				}
			}
			return pageList.setRecords(userTasks);
		}
		return pageList.setRecords(userTasks);
	}

	/**获取状态*/
	private void getTrainingState(BdTrainTask bdTrainTask) {
		Integer taskState = bdTrainTask.getTaskState();
		if (taskState.equals(ExamConstans.UNPUBLISHED) || taskState.equals(ExamConstans.PUBLISHED)) {
			bdTrainTask.setTrainingState(0);
		}
		else if (taskState.equals(ExamConstans.IN_TRAINING) && bdTrainTask.getStopState() == 0) {
			bdTrainTask.setTrainingState(1);
		}

		else if (taskState.equals(ExamConstans.IN_TRAINING) && bdTrainTask.getStopState() == 1) {
			bdTrainTask.setTrainingState(2);
		}
		else if (taskState > ExamConstans.IN_TRAINING && taskState < ExamConstans.COMPLETED) {
			bdTrainTask.setTrainingState(3);
		}
		else {
			bdTrainTask.setTrainingState(4);
		}
	}


	@Override
	public FeedBackDetailsDTO feedBackDetails(String id,String userId){
		FeedBackDetailsDTO feedBackDetailsDTO = bdTrainTaskMapper.feedBackDetails(id,userId);
		if (Objects.isNull(feedBackDetailsDTO)){
			return null;
		}
		List<QuestionDTO> questionDTOList = bdTrainTaskMapper.selectTrainList(id,userId);
		if(questionDTOList.size()!=0){
			feedBackDetailsDTO.setQuestionDTOs(questionDTOList);
		}else {feedBackDetailsDTO.setClassifyName(new ArrayList<>());}
		List<List<QuestionDTO>> stuDto = new ArrayList<>();
		questionDTOList.stream()
				.collect(Collectors.groupingBy(QuestionDTO::getClassifyName,Collectors.toList()))
				.forEach((classifyName,questionDTOListByClassifyName)->{
					stuDto.add(questionDTOListByClassifyName);
				});
		feedBackDetailsDTO.setStuQuestionDTOs(stuDto);
		return feedBackDetailsDTO;
	}

	@Override
	public BdTrainTaskDTO queryResults(String trainTaskId) {
		BdTrainTaskDTO bdTrainTaskDTO = bdTrainTaskMapper.selectTrainTaskId(trainTaskId);
		List<TranscriptDTO> transcriptDTOs =bdTrainTaskMapper.selectTranscripts(trainTaskId);
		if (transcriptDTOs.size()==0){
			bdTrainTaskDTO.setTranscriptDTOs(new ArrayList<>());
		}else { bdTrainTaskDTO.setTranscriptDTOs(transcriptDTOs);}
		return bdTrainTaskDTO;
	}

	@Override
	public TeacherFeedbackDTO instructorFeedback(String trainTaskId, String userId) {
		TeacherFeedbackDTO teacherFeedbackDTO =bdTrainTaskMapper.instructorFeedback(trainTaskId);
		List<QuestionDTO>questionDTOList =bdTrainTaskMapper.selectquestionList(trainTaskId,userId);
		teacherFeedbackDTO.setQuestionDTOs(questionDTOList);
		if (ObjectUtils.isNotEmpty(teacherFeedbackDTO)){
			teacherFeedbackDTO.setAbsenteesNumber(teacherFeedbackDTO.getReferenceNumber()-teacherFeedbackDTO.getActualReferenceNumber());
			teacherFeedbackDTO.setQuestionDTOs(questionDTOList);
		}
		return teacherFeedbackDTO;
	}
	@Override
	public Page<BdTrainTask> queryList(Page<BdTrainTask> pageList, String uid, BdTrainTask bdTrainTasks, String startTime) {
		//根据任务id,uid来查找已关闭的培训任务
		List<BdTrainTask> bdTrainTaskList = bdTrainTaskMapper.queryList(pageList,bdTrainTasks, uid,startTime);
		Integer numberTrainees = 0;
		//签到状态
		for (BdTrainTask bdTrainTask : bdTrainTaskList) {
			//根据任务id,获取参加培训任务的人员
			List<BdTrainTaskUser> taskUserList = bdTrainTaskUserMapper.taskUserList( bdTrainTask.getId());
			List<BdTrainTaskUser> signList= taskUserList.stream().filter(e -> e.getSignState()==1).collect(Collectors.toList());
			List<BdTrainTaskUser> notSignList = taskUserList.stream().filter(e -> e.getSignState()==0).collect(Collectors.toList());
			bdTrainTask.setNotSignedInNum(notSignList.size());
			bdTrainTask.setSignedInNum(signList.size());
			numberTrainees=notSignList.size() + signList.size();
			bdTrainTask.setNumberTrainees(numberTrainees);
			//判断是否关联考试
			if (bdTrainTask.getExamStatus()==1) {
				//计算及格和未及格人数
				List<BdExamRecord> bdExamRecords = examRecordMapper.examUserList( bdTrainTask.getId());
				List<BdExamRecord> isPassList = bdExamRecords.stream().filter(e -> e.getIsPass() != null && e.getIsPass()==1).collect(Collectors.toList());
				List<BdExamRecord> isNotPassList = bdExamRecords.stream().filter(e -> e.getIsPass() != null && e.getIsPass()==0).collect(Collectors.toList());
				bdTrainTask.setPassNumber(isPassList.size());
				bdTrainTask.setNotPassNumber(isNotPassList.size());
				bdTrainTask.setAbsentPassNumber(numberTrainees - isPassList.size() - isNotPassList.size());
			}
		}
		return pageList.setRecords(bdTrainTaskList);
	}
	@Override
	public Page<BdTrainTask> queryTrainingLedger(Page<BdTrainTask> pageList,BdTrainTask bdTrainTask) {
		List<BdTrainTask> taskList = bdTrainTaskMapper.queryTrainingLedger(pageList,bdTrainTask);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		// 禁用数据权限过滤-start
		boolean filter = GlobalThreadLocal.setDataFilter(false);
		for(BdTrainTask bdTrainTasks : taskList)
		{
			if (StrUtil.isNotEmpty(bdTrainTasks.getTaskTeamCode())) {
				SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(bdTrainTasks.getTaskTeamCode());
				bdTrainTasks.setTaskTeamName(departByOrgCode.getDepartName());
			}
			LoginUser userById = iSysBaseAPI.getUserById(bdTrainTasks.getTeacherId());
			if (ObjectUtil.isNotEmpty(userById)) {
				bdTrainTasks.setTeacherName(userById.getRealname());
			}

			Date startDate1 = bdTrainTasks.getStartDate();
			Date endDate1 = bdTrainTasks.getEndDate();
			if(ObjectUtil.isNotEmpty(startDate1) && ObjectUtil.isNotEmpty(endDate1))
			{
				String startDate = dateFormat.format(bdTrainTasks.getStartDate());
				String endDate = dateFormat.format(bdTrainTasks.getEndDate());
				bdTrainTasks.setTrainStartDate(startDate);
				bdTrainTasks.setTrainEndDate(endDate);
				dateFormat.format(bdTrainTasks.getEndDate());
				String startDateR = (new StringBuilder()).append(startDate).append("-").append(endDate).toString();;
				bdTrainTasks.setTrainingDateRange(startDateR);
			}
			String examPaperId = bdTrainTasks.getExamPaperId();
			if(ObjectUtil.isNotEmpty(examPaperId))
			{
				BdExamPaper examPaper = bdExamPaperMapper.selectById(examPaperId);
				bdTrainTasks.setExamNumber(examPaper.getDanumber()+examPaper.getScqnumber());
			}
		}
		// 禁用数据权限过滤-end
		GlobalThreadLocal.setDataFilter(filter);
		return pageList.setRecords(taskList);
	}
	@Override
	public void addTrainTaskUser(String trainTaskId, String taskTeamId, List<String> userIds) {
		for (String userId:userIds) {
			BdTrainTaskUser bdTrainTaskUser = new BdTrainTaskUser();
			bdTrainTaskUser.setTrainTaskId(trainTaskId);
			bdTrainTaskUser.setTeamId(taskTeamId);
			bdTrainTaskUser.setUserId(userId);
			bdTrainTaskUser.setUserName(bdTrainTaskMapper.getName(userId));
			bdTrainTaskUserMapper.insert(bdTrainTaskUser);
		}
	}
	@Override
	public Page<BdTrainTask> queryPageList(Page<BdTrainTask> pageList, BdTrainTask bdTrainTask) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		bdTrainTask.setTaskTeamCode(sysUser.getOrgCode());
		List<BdTrainTask> trainTasks = bdTrainTaskMapper.queryPageList(pageList, bdTrainTask);
		trainTasks.forEach(b->{
			List<BdTrainTaskUser> userListById = bdTrainTaskUserMapper.getUserListById(b.getId());
			List<String> userIds = userListById.stream().map(BdTrainTaskUser::getUserId).collect(Collectors.toList());
			b.setUserIds(userIds);
		});
		return pageList.setRecords(trainTasks);
	}

	@Override
	public Integer getSignNumber(String trainTaskId, String signTime) {
       BdTrainTaskSign bdTrainTaskSign =bdTrainTaskMapper.getSignNumber(trainTaskId,signTime);
		return bdTrainTaskSign.getNumber();
	}

	@Override
	public Page<BdTrainTaskUser> getUserTasksWeb(Page<BdTrainTaskUser> pageList, String uid, BdTrainTaskUser trainTaskUser) {
		List<BdTrainTaskUser> userTasks = bdTrainTaskUserMapper.getUserTasksWeb(pageList,uid,trainTaskUser);
		queryBdTrainTask(userTasks,uid);
		if(ObjectUtil.isNotEmpty(trainTaskUser.getBdTrainTask()))
		{
			BdTrainTask bdTrainTask = new BdTrainTask();
			if(ObjectUtil.isNotEmpty(trainTaskUser.getBdTrainTask().getTrainingState()))
			{

				if(trainTaskUser.getBdTrainTask().getTrainingState().equals(0))
				{
					bdTrainTask.setTrainingState(trainTaskUser.getBdTrainTask().getTrainingState());
					bdTrainTask.setTaskState(1);
				}
				if(trainTaskUser.getBdTrainTask().getTrainingState().equals(1))
				{
					bdTrainTask.setTrainingState(trainTaskUser.getBdTrainTask().getTrainingState());
					bdTrainTask.setTaskState(2);
					bdTrainTask.setStopState(0);
				}
				if(trainTaskUser.getBdTrainTask().getTrainingState().equals(2))
				{
					bdTrainTask.setTrainingState(trainTaskUser.getBdTrainTask().getTrainingState());
					bdTrainTask.setTaskState(2);
					bdTrainTask.setStopState(1);
				}
				if(trainTaskUser.getBdTrainTask().getTrainingState().equals(4))
				{
					bdTrainTask.setTrainingState(trainTaskUser.getBdTrainTask().getTrainingState());
					bdTrainTask.setTrainingState(4);
				}
			}
			if(StringUtils.isNotBlank(trainTaskUser.getBdTrainTask().getTeacherName()))
			{
				bdTrainTask.setTeacherName(trainTaskUser.getBdTrainTask().getTeacherName());
			}
			if(StringUtils.isNotBlank(trainTaskUser.getBdTrainTask().getTrainingDateRange()))
			{
				bdTrainTask.setTrainingDateRange(trainTaskUser.getBdTrainTask().getTrainingDateRange());
			}
			if(StringUtils.isNotBlank(trainTaskUser.getBdTrainTask().getTaskName()))
			{
				bdTrainTask.setTaskName(trainTaskUser.getBdTrainTask().getTaskName());
			}
			trainTaskUser.setBdTrainTask(bdTrainTask);
			List<BdTrainTaskUser> userTaskList = bdTrainTaskUserMapper.getUserTasksWeb(pageList,uid,trainTaskUser);
			queryBdTrainTask(userTaskList,uid);
			return pageList.setRecords(userTaskList);
		}
		return pageList.setRecords(userTasks);
	}
private void queryBdTrainTask(List<BdTrainTaskUser> userTasks,String uid){
	if (CollectionUtil.isNotEmpty(userTasks)) {
		for (BdTrainTaskUser bdTrainTaskUser : userTasks) {
			//获取任务信息
			BdTrainTask bdTrainTask = bdTrainTaskMapper.queryStudentList(bdTrainTaskUser.getTrainTaskId());
			getTrainingStateWeb(bdTrainTask);
			bdTrainTaskUser.setTaskState(bdTrainTask.getTaskState());
			bdTrainTaskUser.setBdTrainTask(bdTrainTask);
			bdTrainTaskUser.setTrainingState(bdTrainTask.getTrainingState());
			bdTrainTaskUser.setClassify(bdTrainTask.getClassify());
			bdTrainTaskUser.setExamClassify(bdTrainTask.getExamClassify());
			bdTrainTaskUser.setExamStatus(bdTrainTask.getExamStatus());
			bdTrainTaskUser.setExamTaskName(bdTrainTask.getExamTaskName());
			bdTrainTaskUser.setPlanSubName(bdTrainTask.getPlanSubName());
			bdTrainTaskUser.setTeacherName(bdTrainTask.getTeacherName());
			bdTrainTaskUser.setTrainingDateRange(bdTrainTask.getTrainingDateRange());
			bdTrainTaskUser.setStartTime(bdTrainTask.getStartTime());
			bdTrainTaskUser.setEndTime(bdTrainTask.getEndTime());
			bdTrainTaskUser.setExamPlanTime(bdTrainTask.getExamPlanTime());
			//获取状态
			if (bdTrainTaskUser.getSignState().equals(1)) {
				Date signTime = bdTrainTaskMapper.getSignTime(uid, bdTrainTask.getId());
				bdTrainTaskUser.setSignTime(signTime);
			}
			if (bdTrainTaskUser.getFeedState().equals(1)) {
				Date completeTime = bdTrainTaskMapper.getCompleteTime(uid, bdTrainTask.getId());
				bdTrainTaskUser.setCompleteTime(completeTime);
			}
		}
	}
}
	/**
	 * 获取状态
	 * */
	private void getTrainingStateWeb(BdTrainTask bdTrainTask) {
		Integer taskState = bdTrainTask.getTaskState();
		//0：未开始,培训任务状态：1（已发布）
		if (taskState == 1) {
			bdTrainTask.setTrainingState(0);
		}
		//1：培训中,培训任务状态：2（培训中），暂停标志：0（进行中）
		else if (taskState == 2 && bdTrainTask.getStopState() == 0) {
			bdTrainTask.setTrainingState(1);
		}
		//2：暂停中，培训任务状态：2（培训中），暂停标志：0（进行中）
		else if (taskState == 2 && bdTrainTask.getStopState() == 1) {
			bdTrainTask.setTrainingState(2);
		}
		//4：已关闭,培训任务状态：>2,taskState not in (0,1)
		else if(taskState > 2)
		{
			bdTrainTask.setTrainingState(4);
		}
	}


	public void copyDetail(BdTrainQuestionFeedback trainQuestionFeedback) {
		List<BdTrainQuestionFeedbackOptions> feedbackOptions = bdTrainQuestionFeedbackOptionsMapper.selectByMainId(trainQuestionFeedback.getId());
		List<BdTrainQuestionFeedbackQues> feedbackQues = bdTrainQuestionFeedbackQuesMapper.selectByMainId(trainQuestionFeedback.getId());
		BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord = new BdTrainQuestionFeedbackRecord();
		bdTrainQuestionFeedbackRecord.setName(trainQuestionFeedback.getName());
		bdTrainQuestionFeedbackRecord.setClassify(trainQuestionFeedback.getClassify());
		bdTrainQuestionFeedbackRecord.setState(trainQuestionFeedback.getState());
		bdTrainQuestionFeedbackRecord.setIdel(trainQuestionFeedback.getIdel());
		bdTrainQuestionFeedbackRecord.setTrainTaskId(trainQuestionFeedback.getTrainTaskId());
		bdTrainQuestionFeedbackRecordMapper.insert(bdTrainQuestionFeedbackRecord);
		for (BdTrainQuestionFeedbackQues feedbackQue:feedbackQues) {
			BdTrainQuestionFeedbackQuesRecord bdTrainQuestionFeedbackQuesRecord = new BdTrainQuestionFeedbackQuesRecord();
			bdTrainQuestionFeedbackQuesRecord.setTrainQuestionFeedbackId(bdTrainQuestionFeedbackRecord.getId());
			bdTrainQuestionFeedbackQuesRecord.setClassifyName(feedbackQue.getClassifyName());
			bdTrainQuestionFeedbackQuesRecord.setQuestionName(feedbackQue.getQuestionName());
			bdTrainQuestionFeedbackQuesRecord.setQuestionClassify(feedbackQue.getQuestionClassify());
			bdTrainQuestionFeedbackQuesRecord.setIdel(feedbackQue.getIdel());
			bdTrainQuestionFeedbackQuesRecordMapper.insert(bdTrainQuestionFeedbackQuesRecord);
		}
		for (BdTrainQuestionFeedbackOptions feedbackOption:feedbackOptions) {
			BdTrainQuestionFeedbackOptionsRecord bdTrainQuestionFeedbackOptionsRecord = new BdTrainQuestionFeedbackOptionsRecord();
			bdTrainQuestionFeedbackOptionsRecord.setTrainQuestionFeedbackId(bdTrainQuestionFeedbackRecord.getId());
			bdTrainQuestionFeedbackOptionsRecord.setName(feedbackOption.getName());
			bdTrainQuestionFeedbackOptionsRecord.setIdel(feedbackOption.getIdel());
			bdTrainQuestionFeedbackOptionsRecordMapper.insert(bdTrainQuestionFeedbackOptionsRecord);

		}
	}

	@Override
	public Page<UserDTO> getTrainees(Page<UserDTO> pageList,BdTrainTask bdTrainTask) {
		List<UserDTO> trainees = bdTrainTaskMapper.getTrainees(pageList, bdTrainTask);
		return pageList.setRecords(trainees);
	}

	@Override
	public  Page<UserDTO> getTrainTeacher(Page<UserDTO> pageList, UserDTO userDTO) {
		List<UserDTO> trainTeacher = bdTrainTaskMapper.getTrainTeacher(pageList, userDTO);
		return pageList.setRecords(trainTeacher);
	}

	@Override
	public void add(BdTrainTaskPage bdTrainTaskPage) {
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
		this.saveMain(bdTrainTask, bdTrainTaskPage.getBdTrainTaskSignList());
		List<String> userIds = bdTrainTask.getUserIds();
		// 看是不是所有人员都要培训档案，如果有人没有培训档案的，直接抛出错误
		LambdaQueryWrapper<TrainArchive> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(TrainArchive::getUserId);
		queryWrapper.eq(TrainArchive::getDelFlag, CommonConstant.DEL_FLAG_0);
		queryWrapper.in(TrainArchive::getUserId, userIds);
		List<TrainArchive> list = archiveService.list(queryWrapper);
		List<String> trainArchiveIdList = list.stream().map(TrainArchive::getUserId).collect(Collectors.toList());
		if (!trainArchiveIdList.containsAll(userIds)) {
			throw new AiurtBootException("培训档案中没有相关培训人员记录！");
		}

		this.addTrainTaskUser(bdTrainTask.getId(),bdTrainTask.getTaskTeamId(),userIds);
	}
}
