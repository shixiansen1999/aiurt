package com.aiurt.modules.train.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.task.dto.*;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;

import java.util.Date;
import java.util.List;

/**
 * @Description: 培训任务
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdTrainTaskMapper extends BaseMapper<BdTrainTask> {

    /**
     * 获取用户姓名
     * @param userId
     * @return
     * */
    String getName(@Param("userId") String userId);

   /**
    * 获取部门名称
    * @param taskTeamId
    * @return
    * */
    String getTeamName(@Param("taskTeamId") Integer taskTeamId);

    /**
     * 获取该部门中的所有用户
     * @param teamId
     * @param teacherId
     * @return
     */
    List<String> getUserId(@Param("teamId") Integer teamId,@Param("teacherId") String teacherId);

    /**
     * 获取授课记录（已关闭）
     * @param pageList
     * @param bdTrainTask
     * @param uid
     * @param startTime
     * @return
     */
    List<BdTrainTask> queryList(@Param("pageList") Page<BdTrainTask> pageList,@Param("condition") BdTrainTask bdTrainTask,@Param("uid") String uid,@Param("startTime") String startTime);

    /**
     * 获取讲师培训任务
     * @param id
     * @param taskName
     * @param pageList
     * @return
     * */
    List<BdTrainTask> getTaskById(@Param("pageList") Page<BdTrainTask> pageList,@Param("id") String id, @Param("taskName") String taskName,@Param("taskId") String taskId);


    /**
     * 获取讲师培训任务
     * @param pageList
     * @return
     * */
    List<BdTrainTask> getTaskByIds(@Param("pageList") Page<BdTrainTask> pageList,@Param("condition") BdTrainTask condition,@Param("date")Date date);


    /**
     * 获取培训人员签到情况
     * @param id
     * @return
     */
    BdTrainTaskUser getBdTrainTaskUser(@Param("id") String id);

    /**
     * 获取签到时间
     * @param id
     * @param trainTaskId
     * @return
     */
    Date getSignTime(@Param("id") String id, @Param("trainTaskId") String trainTaskId);
    /**
     * 获取反馈时间
     * @param id
     * @param trainTaskId
     * @return
     */
    Date getCompleteTime(@Param("id") String id, @Param("trainTaskId") String trainTaskId);

    public Page<TrainQueryTableDTO> selectByUserId (@Param("pageList") Page<TrainQueryTableDTO> pageList, @Param("userId")String userId,@Param("signState") Integer signState,@Param("startTime") String startTime,@Param("taskName") String taskName);

    public BdTrainTaskDTO studentTrainingDetails(@Param("id")String id);

    public FeedBackDetailsDTO feedBackDetails (@Param("id")String id, @Param("userId")String userId);

    List<QuestionDTO> selectTrainList(@Param("id")String id,@Param("userId")String userId);

    public BdTrainTaskDTO selectTrainTaskId(@Param("trainTaskId")String trainTaskId);

    List<TranscriptDTO> selectTranscriptList (@Param("examPaperId")String examPaperId,@Param("trainTaskId")String trainTaskId);

    public TeacherFeedbackDTO instructorFeedback(@Param("trainTaskId")String trainTaskId);
    List<String> selectClassifyName(@Param("id")String id, @Param("userId")String userId);
    List<String> selectTeacherClassifyName(@Param("id")String id, @Param("userId")String userId);

    List<QuestionDTO>selectquestionList(@Param("trainTaskId")String trainTaskId,@Param("userId")String userId);
    /**
     * 查询签到人员
     * @param trainTaskId
     * @return
      */
    Page<SignPeopleDTO> querySignPeople(@Param("pageList") Page<SignPeopleDTO> pageList,@Param("trainTaskId")String trainTaskId,@Param("signState") Integer signState);
    /**
     * 补签
     * @param signPeopleDTO
     */
    void  updateSign(@Param("signPeopleDTO") SignPeopleDTO signPeopleDTO);
    BdTrainTaskSign getSignNumber(@Param("trainTaskId")String trainTaskId, @Param("signTime") String signTime);
    /**
     * 培训台账-获取培训任务列表
     * @param pageList
     * @param condition
     * @return
     */
    List<BdTrainTask> queryTrainingLedger(@Param("pageList") Page<BdTrainTask> pageList,@Param("condition") BdTrainTask condition);

    /**
     * pc-获取学员培训任务
     * @param id
     * @return
     */
    BdTrainTask queryStudentList(@Param("id") String id);
    List<TranscriptDTO> selectTranscripts(@Param("trainTaskId")String trainTaskId);

   /**
    * 获取讲师是否存在
    * @param teacherId
    * @return
    * */
    String getTeacherRole(@Param("teacherId")String teacherId);

    /**
     * 获取学员是否存在
     * @param studentId
     * @return
     * */
    String getStudentRole(@Param("studentId")String studentId);

    /**
     * 分页查询培训任务列表
     * @param pageList
     * @param bdTrainTask
     * @return
     * */
    List<BdTrainTask> queryPageList(@Param("pageList") Page<BdTrainTask> pageList, @Param("bdTrainTask") BdTrainTask bdTrainTask);
    /**
     * 获取参训人员
     * @param pageList
     * @param bdTrainTask
     * @return
     * */
    List<UserDTO> getTrainees(@Param("pageList") Page<UserDTO> pageList,@Param("bdTrainTask") BdTrainTask bdTrainTask);
    /**
     * 获取讲师
     * @param pageList
     * @param user
     * @return
     * */
    List<UserDTO> getTrainTeacher(@Param("page") Page<UserDTO> pageList, @Param("user") UserDTO user);


}
