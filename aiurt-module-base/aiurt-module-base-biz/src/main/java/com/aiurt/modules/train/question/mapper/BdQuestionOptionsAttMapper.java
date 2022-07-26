package com.aiurt.modules.train.question.mapper;

import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: bd_question_options_att
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdQuestionOptionsAttMapper extends BaseMapper<BdQuestionOptionsAtt> {

    /**
     * 查询习题附件
     * @param id
     * @return
     */
    List<BdQuestionOptionsAtt> getAtt(String id);

}
