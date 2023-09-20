package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.ValidationRule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-29 18:01
 * @Description:
 */
@Component("InspectionCodeContentCodeUniqueValidationHandler")
public class InspectionCodeContentCodeUniqueValidationHandler implements ValidationRule {
    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;

    /**
     * 检查项编号在数据库中唯一
     *
     * @param currentColumn
     * @return
     */
    @Override
    public ValidationResult validate(Column currentColumn) {
        // 获取当前列的值
        Object currentValue = currentColumn.getData();
        if (ObjectUtil.isNotEmpty(currentValue)) {
            Long itemNums = inspectionCodeContentMapper.selectCount(new QueryWrapper<InspectionCodeContent>().lambda().eq(InspectionCodeContent::getCode, currentValue).eq(InspectionCodeContent::getDelFlag, 0));
            if (itemNums>0) {
                return new ValidationResult(false, String.format("%s该字段值在数据库中已存在", currentColumn.getName()));
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
