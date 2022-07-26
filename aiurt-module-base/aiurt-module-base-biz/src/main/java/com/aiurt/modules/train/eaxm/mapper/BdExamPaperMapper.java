package com.aiurt.modules.train.eaxm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.eaxm.entity.BdExamPaper;

import java.util.List;

/**
 * @Description: 试卷库表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdExamPaperMapper extends BaseMapper<BdExamPaper> {
    /**
     * 培训题库-查看详情
     *
     * @param id
     * @return
     */
    List<String> trainTask(String id);

    /**
     * 培训题库-列表显示
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<BdExamPaper> queryList(@Param("pageList") Page<BdExamPaper> pageList, @Param("condition") BdExamPaper condition);

    /**
     * 试卷答案
     *
     * @param condition
     * @return
     */
    String getExamineAnswer(@Param("condition") BdExamPaper condition, @Param("examRecordId") String examRecordId, @Param("queId") String queId,@Param("date") String date);

    /**
     * 试卷习题添加
     *
     * @param examPaperId
     * @param questionIds
     * @return
     */
    void addQuestion(@Param("examPaperId") String examPaperId, @Param("questionIds") List<Integer> questionIds);

    /**
     * 根据题目id获取是简单题的题目
     *
     * @param examPaperId
     * @return
     */
    Integer getShortAnswerNum(@Param("examPaperId") String examPaperId);

}