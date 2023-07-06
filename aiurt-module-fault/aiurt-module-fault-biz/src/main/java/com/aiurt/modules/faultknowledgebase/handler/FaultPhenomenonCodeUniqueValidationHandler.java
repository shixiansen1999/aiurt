package com.aiurt.modules.faultknowledgebase.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.ValidationRule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-03 11:50
 * @Description:
 */
@Component("FaultPhenomenonCodeUniqueValidationHandler")
public class FaultPhenomenonCodeUniqueValidationHandler implements ValidationRule {
    @Resource
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    /**
     * 设备编号在数据库中唯一
     *
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Column column) {
        // 获取当前列的值
        Object currentValue = column.getData();
        if (ObjectUtil.isNotEmpty(currentValue)) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.selectOne(new QueryWrapper<FaultKnowledgeBase>().lambda().eq(FaultKnowledgeBase::getFaultPhenomenonCode, currentValue).eq(FaultKnowledgeBase::getDelFlag, 0));
            if (ObjectUtil.isNotEmpty(faultKnowledgeBase)) {
                return new ValidationResult(false, String.format("%s该字段值在数据库中已存在",  column.getName()));
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
