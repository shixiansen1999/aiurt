package com.aiurt.modules.train.mistakes.mapper;

import com.aiurt.modules.train.mistakes.entity.BdExamMistakesQuestion;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 错题集-考题关联表的mapper
 *
 * @author 华宜威
 * @date 2023-08-25 08:55:50
 */
public interface BdExamMistakesQuestionMapper extends BaseMapper<BdExamMistakesQuestion> {

    /**
     * 根据错题集id，获取错误的题目详情
     *
     * @param mistakesId 错题集id
     * @return
     */
    List<BdQuestion> getQuestionByMistakesId(@Param("mistakesId") String mistakesId);
}
