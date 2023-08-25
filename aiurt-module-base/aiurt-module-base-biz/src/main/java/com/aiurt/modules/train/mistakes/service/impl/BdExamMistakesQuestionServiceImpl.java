package com.aiurt.modules.train.mistakes.service.impl;

import com.aiurt.modules.train.mistakes.entity.BdExamMistakesQuestion;
import com.aiurt.modules.train.mistakes.mapper.BdExamMistakesQuestionMapper;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 错题集-考题关联表的service的实现类
 *
 * @author 华宜威
 * @date 2023-08-25 08:59:13
 */
@Service
public class BdExamMistakesQuestionServiceImpl extends ServiceImpl<BdExamMistakesQuestionMapper, BdExamMistakesQuestion> implements IBdExamMistakesQuestionService {
}
