package com.aiurt.modules.train.feedback.mapper;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 问题反馈主表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackMapper extends BaseMapper<BdTrainQuestionFeedback> {

    /**
     * 问题反馈主表-分页列表查询
     * @param bdTrainQuestionFeedback
     * @param page
     * @return
     */
    List<BdTrainQuestionFeedback> selectPageList(@Param("page") Page<BdTrainQuestionFeedback> page, @Param("bdTrainQuestionFeedback") BdTrainQuestionFeedback bdTrainQuestionFeedback);
}
