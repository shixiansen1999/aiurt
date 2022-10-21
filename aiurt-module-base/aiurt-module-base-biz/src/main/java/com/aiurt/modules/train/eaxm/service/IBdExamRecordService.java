package com.aiurt.modules.train.eaxm.service;

import com.aiurt.modules.train.exam.dto.BdAchievementDTO;
import com.aiurt.modules.train.exam.dto.ExamDetailsDTO;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 考试记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdExamRecordService extends IService<BdExamRecord> {

    /**
     * 查询学员列表
     * @param pageList
     * @param condition
     * @return
     */
    Page<BdExamRecord> queryPageList(Page<BdExamRecord> pageList,BdExamRecord condition);

    /**
     * 查询学员列表-pc
     * @param pageList
     * @param condition
     * @return
     */
    Page<BdExamRecord> queryPageListPc(Page<BdExamRecord> pageList,BdExamRecord condition);

    /**
     * 考试详情
     * @param examPaperId
     * @param trainTaskId
     * @param examClassify
     * @param id
     * @return
     */
    BdExamRecord readOne(@Param("examPaperId") String examPaperId,@Param("trainTaskId") String trainTaskId,@Param("examClassify") Integer examClassify,@Param("id") String id);


    /**
     * 提交
     * @param bdExamRecord
     * @return
     */
    void addBdQuestionCategory(BdExamRecord bdExamRecord);


    /**
     * 学员考试记录查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<BdExamRecord> lists(Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);

    /**
     * 讲师考试任务列表查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<BdExamRecord> lecturerList(Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);


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
     * 查询历史考试记录
     * @param condition
     * @param pageList
     * @return
     */
    Page<BdExamRecord> recordList (Page<BdExamRecord> pageList,@Param("condition") BdExamRecord condition);

    /**
     * 查询考试结果列表
     * @param id
     * @param examPaperId
     * @param examClassify
     * @return
     */
    List<BdTrainTaskUser> resultList (@Param("id") String id,@Param("examPaperId") String examPaperId ,@Param("examClassify") Integer examClassify);


    /**
     * 添加
     * @param bdAchievementDTO
     */
    void bdAchievementDTOList (@Param ("bdAchievementDTO") BdAchievementDTO bdAchievementDTO);


    /**
     * 修改
     * @param bdAchievementDTO
     */
    void updatedAchievementDOList(@Param ("bdAchievementDTO") BdAchievementDTO bdAchievementDTO);

    /**
     * 录入考试结果
     * @param bdAchievementDTOList
     */
    void addList(@Param ("bdAchievementDTOList") List<BdAchievementDTO> bdAchievementDTOList);


    /**
     * 考试详情
     * @param examPaperId
     * @param trainTaskId
     * @return
     */
    ExamDetailsDTO examDetails(String examPaperId,String trainTaskId);


    /**
     * 核对学员考试信息
     * @param examPaperId
     * @param trainTaskId
     * @return
     */
    BdExamRecord readOnes(@Param("examPaperId") String examPaperId,@Param("trainTaskId") String trainTaskId);

}
