package com.aiurt.modules.train.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;

import java.util.List;

/**
 * @Description: bd_question_options
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdQuestionOptionsMapper extends BaseMapper<BdQuestionOptions> {


    /**
     * 题目id-获取所有选项
     * @param optionId
     * @return
     */
    List<BdQuestionOptions> optionList(String optionId);
}
