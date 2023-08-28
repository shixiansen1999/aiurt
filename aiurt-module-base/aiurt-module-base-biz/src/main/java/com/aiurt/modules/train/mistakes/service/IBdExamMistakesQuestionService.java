package com.aiurt.modules.train.mistakes.service;

import com.aiurt.modules.train.mistakes.entity.BdExamMistakesQuestion;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 错题集-考题关联表的service
 *
 * @author 华宜威
 * @date 2023-08-25 08:58:19
 */
public interface IBdExamMistakesQuestionService extends IService<BdExamMistakesQuestion> {

    /**
     * 根据错题集id，获取错误的题目详情
     *
     * @param mistakesId 错题集id
     * @return
     */
    List<BdQuestion> getQuestionByMistakesId(String mistakesId);
}
