package com.aiurt.modules.train.mistakes.service.impl;

import com.aiurt.modules.train.mistakes.entity.BdExamMistakes;
import com.aiurt.modules.train.mistakes.mapper.BdExamMistakesMapper;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 错题集的service的实现类
 *
 * @author 华宜威
 * @date 2023-08-25 08:54:03
 */
@Service
public class BdExamMistakesServiceImpl extends ServiceImpl<BdExamMistakesMapper, BdExamMistakes> implements IBdExamMistakesService {
}
