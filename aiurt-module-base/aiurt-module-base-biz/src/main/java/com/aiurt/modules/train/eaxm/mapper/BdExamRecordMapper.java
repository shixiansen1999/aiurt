package com.aiurt.modules.train.eaxm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.exam.dto.BdAchievementDTO;
import com.aiurt.modules.train.exam.dto.ExamDetailsDTO;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;

import java.util.List;

/**
 * @Description: 考试记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdExamRecordMapper extends BaseMapper<BdExamRecord> {

    /**
     * 查询学员考试计划列表
     * @param pageList
     * @param condition
     * @return
     */
    List<BdExamRecord> list(@Param("pageList") Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);

    /**
     * 查询考试记录状态
     * @param userId
     * @param trainTaskId
     * @return
     */
    String selectOnes(@Param("userId") String userId,@Param("trainTaskId") String trainTaskId,@Param("examPaperId") String examPaperId);


    /**
     * 查询考试补考记录状态
     * @param userId
     * @param trainTaskId
     * @return
     */
    String selectOness(@Param("userId") String userId,@Param("trainTaskId") String trainTaskId,@Param("examPaperId") String examPaperId);

    /**
     * 查询补考记录
     * @param userId
     * @param trainTaskId
     * @return
     */
    List<BdTrainMakeupExamRecord> excretory(@Param("userId") String userId, @Param("trainTaskId") String trainTaskId ,@Param("examPaperId") String examPaperId);

    /**
     * 核对学员考试信息
     * @param examPaperId
     * @param trainTaskId
     * @return
     */
    BdExamRecord readOnes(@Param("examPaperId") String examPaperId,@Param("trainTaskId") String trainTaskId);


    /**
     * 考试详情
     * @param examPaperId
     * @param trainTaskId
     * @param userId
     * @param id
     * @return
     */
    BdExamRecord readOne(@Param("examPaperId") String examPaperId,@Param("trainTaskId") String trainTaskId,@Param("examClassify") Integer examClassify,@Param("userId") String userId,@Param("id") String id);

    BdExamRecord readOness(@Param("examPaperId") String examPaperId,@Param("trainTaskId") String trainTaskId,@Param("examClassify") Integer examClassify,@Param("userId") String userId,@Param("id") String id);

    List<String> listId(@Param("id") String  id);

    /**
     * 考试记录查询
     * @param pageList
     * @param condition
     * @return
     */
    List<BdExamRecord> lists(@Param("pageList") Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);
    /**
     * 考试记录查询-Pc
     * @param pageList
     * @param condition
     * @return
     */
    List<BdExamRecord> listss(@Param("pageList") Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);

    /**
     * 查询讲师列表
     * @param pageList
     * @param condition
     * @return
     */
    List<BdExamRecord> lecturerList(@Param("pageList") Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);


    /**
     * 核对讲师考试信息
     * @param id
     * @param exemplify
     * @param state
     * @param taskId
     * @return
     */
    BdExamPaper lecturerReadOne (@Param("id") String id,@Param("exemplify") Integer exemplify,@Param("state") Integer state,@Param("taskId") String taskId);


    /**
     * 查询任务的所有用户
     * @param id
     * @return
     */
    List<BdTrainTaskUser> userList (@Param("id") String id);


    /**
     * 根据用户id查询分数
     * @param id
     * @param traintaskid
     * @return
     */
    BdTrainTaskUser resultLists (@Param("id") String id,@Param("traintaskid") String traintaskid,@Param("examClassify") Integer examClassify,@Param("examPaperId") String examPaperId);

    BdTrainTaskUser resultListss (@Param("id") String id,@Param("traintaskid") String traintaskid,@Param("examClassify") Integer examClassify,@Param("examPaperId") String examPaperId);
    /**
     * 查询参考人数
     * @param id
     * @return
     */
    List <String> bdExamPaper (@Param("id") String id,@Param("takeId") String takeId);
    //实际参加考试人员姓名
    List <String> bdExamPaperName(@Param("trainTaskId")String trainTaskId);
    //应该参加
    List<String>  actualReferenceNames(@Param("trainTaskId")String trainTaskId);

    /**
     * 查询历史考试记录
     * @param condition
     * @param pageList
     * @return
     */
    List<BdExamRecord> recordList (@Param("pageList") Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);

    /**
     * 保存
     * @param condition
     */
    void insertedAchievementDOList(@Param ("condition") BdAchievementDTO condition);

    /**
     * 修改
     * @param condition
     */
    void updatedAchievementDOList(@Param ("condition") BdAchievementDTO condition);

    ExamDetailsDTO examDetails (@Param("examPaperId")String examPaperId,@Param("trainTaskId")String trainTaskId);


    /**
     * 根据培训任务id获取考试人员
     */
    List<BdExamRecord> examUserList(@Param("taskId") String taskId);

    /**
     * 根据培训任务id查询
     * @param taskId
     * @return
     */
    List<BdExamRecord> getlist(@Param("taskId") String taskId);


    /**
     * 获取考生记录
     * @param examPaperId
     * @param userId
     * @return
     */
    List<BdExamRecord> getRecordList(@Param("examPaperId")String examPaperId, @Param("userId")String userId,@Param("examClassify")String examClassify,@Param("taskId")String taskId);

    /**
     * 试卷及格分
     * @param id
     * @return
     */
    int getPassSorce(String id);

    /**
     * 根据考试记录查询培训任务。查询对应的班组id
     * @param id
     * @return
     */
    String getTeamIdByExamRecordId(String id);

    /**
     * 获取记录数量
     * @param taskId
     * @return
     */
    List<BdExamRecord> getNum(@Param("taskId") String taskId);
    /**
     * 跟新补考数据
     * @param bdExamRecord
     */
      void UpdateById(@Param ("bdExamRecord") BdExamRecord bdExamRecord);
}
