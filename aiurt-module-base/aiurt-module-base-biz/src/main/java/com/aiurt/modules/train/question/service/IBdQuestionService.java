package com.aiurt.modules.train.question.service;

import com.aiurt.modules.train.question.dto.BdQuestionDTO;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdQuestionService extends IService<BdQuestion> {

    /**
     * 查询习题信息
     * @param condition
     * @param pageList
     * @return
     */
    Page<BdQuestion> queryPageList(Page<BdQuestion> pageList,BdQuestion condition);

    /**
     * 添加习题
     * @param bdQuestion
     */
    void addBdQuestion(BdQuestion bdQuestion);


    /**
     *
     * 修改习题
     * @param bdQuestion
     */
    void updateBdQuestion(BdQuestion bdQuestion);


    /**
     * 查询
     * @param id
     * @return
     */
    BdQuestion bdQuestion  (@Param("id") String id);

    /**
     *
     * 查看学习资料
     * @param id
     * @return
     */
    List<BdQuestion> getLearningMaterials(@Param("id") String id);
    /**
     * 随机抽取试题
     *
     * @param categoryIds 习题类别
     * @param choiceQuestionNum 选择题数量
     * @param shortAnswerQuestionNum 简答题数量
     * @return
     */
    List<BdQuestion> randomSelectionQuestion(String categoryIds, Integer choiceQuestionNum, Integer shortAnswerQuestionNum);

    /**
     * 获取题目数量
     *
     * @param categoryIds 习题类别
     * @return
     */
    BdQuestionDTO getQuestionNum(String categoryIds);

    /**
     * 下载导入模板
     *
     * @param request
     * @param response
     * @throws IOException
     */
    void downloadTemplateExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
