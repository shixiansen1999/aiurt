package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.ValidationRule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-29 21:49
 * @Description:
 */
@Component("PatrolStandardItemCodeUniqueValidationHandler")
public class PatrolStandardItemCodeUniqueValidationHandler implements ValidationRule {
    @Resource
    private PatrolStandardItemsMapper patrolStandardItemsMapper;

    /**
     * 巡视项编号在数据库中唯一
     *
     * @param currentColumn
     * @return
     */
    @Override
    public ValidationResult validate(Column currentColumn) {
        // 获取当前列的值
        Object currentValue = currentColumn.getData();
        if (ObjectUtil.isNotEmpty(currentValue)) {
            PatrolStandardItems patrolStandardItems = patrolStandardItemsMapper.selectOne(new QueryWrapper<PatrolStandardItems>().lambda().eq(PatrolStandardItems::getCode, currentValue).eq(PatrolStandardItems::getDelFlag, 0));
            if (ObjectUtil.isNotEmpty(patrolStandardItems)) {
                return new ValidationResult(false, String.format("%s该字段值在数据库中已存在", currentColumn.getName()));
            }
        }

        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
