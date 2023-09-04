package com.aiurt.modules.train.mistakes.service.impl;

import com.aiurt.modules.train.mistakes.entity.BdExamMistakesAnswer;
import com.aiurt.modules.train.mistakes.mapper.BdExamMistakesAnswerMapper;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 错题集答题情况表的service的实现类
 *
 * @author 华宜威
 * @date 2023-08-25 09:05:44
 */
@Service
public class IBdExamMistakesAnswerServiceImpl extends ServiceImpl<BdExamMistakesAnswerMapper, BdExamMistakesAnswer> implements IBdExamMistakesAnswerService {
}
