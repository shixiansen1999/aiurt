package com.aiurt.modules.train.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;

import java.util.List;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdQuestionMapper extends BaseMapper<BdQuestion> {
/*    *//**
     * 查询题目
     * @param questionId
     * @return
     *//*
    BdQuestion getQues(@Param("questionId") String questionId);*/

    /**
     * 查询考试习题信息
     * @param condition
     * @param pageList
     * @return
     */
    List<BdQuestion> list(@Param("pageList") Page<BdQuestion> pageList ,@Param("condition") BdQuestion condition);


    /**
     * 用试卷id查询所以试卷的题目
     *  @param id
     * @return
     */
    List<BdQuestion> contentList(String id);

    /**
     * 查询
     * @param id
     * @return
     */
    BdQuestion bdQuestion  (@Param("id") String id);

    /**
     * 查询
     * @param id
     * @return
     */
    List<BdQuestionOptions> lists (@Param("id") String id);

    /**
     * 查询
     * @param id
     * @return
     */
    List<BdQuestionOptionsAtt> listss (@Param("id") String id);

    /**
     * 删除
     * @param id
     */
    void deletequestionoptionsatt(@Param("id") String id);

    /**
     * 删除
     * @param id
     */
    void deletquestionoptions(@Param("id") String id);
    /**
     * 查询考试习题id
     * @param id
     * @return
     */
    List<String> getQuesId(String id);
    /**
     * 查询考试习题内容和习题答案
     * @param quesId
     * @return
     */
    List<BdQuestion> getOptions(@Param("quesId")List<String> quesId);
}
