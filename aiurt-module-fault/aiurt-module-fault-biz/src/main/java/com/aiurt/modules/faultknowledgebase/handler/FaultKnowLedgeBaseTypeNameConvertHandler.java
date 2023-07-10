package com.aiurt.modules.faultknowledgebase.handler;

import com.aiurt.modules.entity.Column;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.handler.IRowDataConvertHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lkj
 */
@Component("FaultKnowLedgeBaseTypeNameConvertHandler")
public class FaultKnowLedgeBaseTypeNameConvertHandler implements IRowDataConvertHandler {

    @Resource
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Override
    public String convert(Column value, Map<String, Column> row) {

        Column majorCodeColumn = row.get("major_code");

        Column subsystemCodeColumn = row.get("system_code");
        Object subsystem = subsystemCodeColumn.getData();

        FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeMapper.selectOne(new LambdaQueryWrapper<FaultKnowledgeBaseType>()
                .eq(FaultKnowledgeBaseType::getMajorCode,(String)majorCodeColumn.getData())
                .eq(FaultKnowledgeBaseType::getSystemCode,(String)subsystem)
                .eq(FaultKnowledgeBaseType::getName, (String) value.getData()));
        value.setData(faultKnowledgeBaseType.getCode());
        return null;
    }
}
