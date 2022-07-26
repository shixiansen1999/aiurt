package com.aiurt.modules.train.task.mapper;

import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.task.entity.BdTrainPlan;
import com.aiurt.modules.train.task.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SysDepartModel;

import java.util.List;


/**
 * @Description: 年计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
public interface BdTrainPlanMapper extends BaseMapper<BdTrainPlan> {

    //查询报表数据
    List<ReportVO> report(@Param("page") Page<ReportVO> page, @Param("reportReqVO") ReportReqVO reportReqVO);

    //根据培训任务id查询年计划
    BdTrainPlan getPlanByPlanSubId(String trainTaskId);

    //培训复核管理
    List<ReCheckVO> getReCheckList(@Param("page")Page<ReCheckVO> page, @Param("reCheckReqVo") ReCheckReqVo reCheckReqVo);

    //查询试卷的简答题详情
    List<ShortAnswerVo> getShortAnswerQuestion(@Param("id")String id, @Param("date")String date,@Param("number")Integer number);

    //查询试卷关联的多媒体路径
    List<BdQuestionOptionsAtt> getMidea(String id);

    //培训报表管理-部门下拉数据
    List<String> getDept();

    //根据train_task_id查询考试记录
    List<BdExamRecord> getByTrainTaskId(String trainTaskId);

    //根据train_task_id查询总的考试记录
    List<BdExamRecord> getAllExamRecord(String trainTaskId);

    //根据班组节点查询子节点、父节点
    List<SysDepartModel> getDepartIdsByTeamId(String departId);

    //根据部门名查询机构id
    String getDepartIdByDeptName(String deptName);

    //根据组织机构id查询用户
    List<String> getUserByTeamId(String s);

}
