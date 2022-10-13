package com.aiurt.modules.train.task.service;

import com.aiurt.modules.train.task.dto.*;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.vo.BdTrainTaskPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 培训任务
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdTrainTaskService extends IService<BdTrainTask> {

	/**
	 * 添加一对多
	 * @param bdTrainTask
	 * @param bdTrainTaskSignList
	 */
	public void saveMain(BdTrainTask bdTrainTask,List<BdTrainTaskSign> bdTrainTaskSignList) ;

	/**
	 * 修改一对多
	 * @param bdTrainTask
	 * @param bdTrainTaskSignList
	 */
	public void updateMain(BdTrainTask bdTrainTask,List<BdTrainTaskSign> bdTrainTaskSignList);

	/**
	 * 修改
	 * @param bdTrainTaskPage
	 */
	Result<?> edit(BdTrainTaskPage bdTrainTaskPage);
	/**
	 * 删除一对多
	 * @param id
	 */
	public void delMain (String id);

	/**
	 * 批量删除一对多
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	/**
	 * 培训记录查询
	 * @param userId
	 * @return
	 */
	Page<TrainQueryTableDTO> trainQueryTable(Page<TrainQueryTableDTO> pageList, String userId,Integer signState,String startDate,String taskName);
	/**
	 * 学生培训任务-详情
	 * @param id
	 * @return
	 */
	BdTrainTaskDTO studentTrainingDetails(String id);
	/**
	 * 查询签到人员
	 * @param trainTaskId
	 * @return
	 */
	Page<SignPeopleDTO> querySignPeople (Page<SignPeopleDTO> pageList,String trainTaskId,Integer signState);
	/**
	 * 补签
	 * @param signPeopleDTO
	 */
	void repairSing (SignPeopleDTO signPeopleDTO);
	/**
	 * 根据讲师id获取培训任务
	 * @param id
	 * @param taskName
	 * @param pageList
	 * @return List<BdTrainTask>
	 * @autor lkj
	 * */
	Page<BdTrainTask> getTaskById(Page<BdTrainTask> pageList,String id, String taskName,String taskId);


	/**
	 * 根据讲师id获取培训任务
	 * @param pageList
	 * @return List<BdTrainTask>
	 * @autor lkj
	 * */
	Page<BdTrainTask> getTaskByIds(Page<BdTrainTask> pageList,BdTrainTask condition);

	/**
	 * 修改计划任务状态
	 * @param bdTrainTask
	 * @return
	 * @autor lkj
	 * */
	void updateTaskState(BdTrainTask bdTrainTask);

	/**
	 * 获取学生培训任务
	 * @param id
	 * @param taskName
	 * @param pageList
	 * @return
	 * @autor lkj
	 */
	Page<BdTrainTaskUser> getUserTasks(Page<BdTrainTaskUser> pageList,String id,String taskName,String taskId);

	/**
	 * 授课记录（已关闭）-列表查询
	 * @param pageList
	 * @param id
	 * @param bdTrainTask
	 * @param actualTrainingTime
	 * @return
	 */
	Page<BdTrainTask> queryList(Page<BdTrainTask> pageList, String id, BdTrainTask bdTrainTask, String actualTrainingTime);

	/**
	 * 学生培训任务-反馈详情
	 * @return
	 */
	FeedBackDetailsDTO feedBackDetails(String id,String userId);
	/**
	 * 查询成绩汇总表
	 * @return
	 */
	BdTrainTaskDTO queryResults(String trainTaskId);
	/**
	 * 讲师反馈表
	 * @return
	 */
	TeacherFeedbackDTO instructorFeedback(String trainTaskId,String userId);

	/**
	 * 培训台账
	 * @param pageList
	 * @param bdTrainTask
	 * @return
	 */
	Page<BdTrainTask> queryTrainingLedger(Page<BdTrainTask> pageList,BdTrainTask bdTrainTask);

	/**
	 * 培训台账
	 * @param trainTaskId
	 * @param taskTeamId
	 * @param userIds
	 * @return
	 */
	void addTrainTaskUser(String trainTaskId, String taskTeamId, List<String> userIds);

	 /**
	  * 分页查询培训任务列表
	  * @param bdTrainTask
	  * @param pageList
	  * @return
	  * */
	Page<BdTrainTask> queryPageList(Page<BdTrainTask> pageList,BdTrainTask bdTrainTask);

	/**
	 * 获取培训轮数
	 * @param trainTaskId
	 * @param signTime
	 * @return
	 */
	Integer getSignNumber(String trainTaskId, String signTime);

	/**
	 * web -获取学生培训任务
	 * @param pageList
	 * @param uid
	 * @param bdTrainTaskUser
	 * @return
	 */
	Page<BdTrainTaskUser> getUserTasksWeb(Page<BdTrainTaskUser> pageList, String uid, BdTrainTaskUser bdTrainTaskUser);


	/**
	 * 获取参训人员
	 * @param pageList
	 * @param bdTrainTask
	 * @return
	 */
	Page<UserDTO> getTrainees(Page<UserDTO> pageList,BdTrainTask bdTrainTask);

	/**
	 * 获取讲师
	 * @param pageList
	 * @param userDTO
	 * @return
	 */
	Page<UserDTO> getTrainTeacher(Page<UserDTO> pageList, UserDTO userDTO);
}
