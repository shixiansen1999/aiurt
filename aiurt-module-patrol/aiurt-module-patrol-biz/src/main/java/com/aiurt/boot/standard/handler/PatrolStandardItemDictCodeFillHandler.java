package com.aiurt.boot.standard.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.ICustomfillDataHandler;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author:wgp
 * @create: 2023-06-29 15:01
 * @Description: 数据填充，作用在关联字典字段上
 */
@Component("PatrolStandardItemDictCodeFillHandler")
public class PatrolStandardItemDictCodeFillHandler implements ICustomfillDataHandler {
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;

    @Override
    public List<String> fillData() {
        List<DictModel> modelList = patrolStandardMapper.querySysDict(PatrolConstant.MODULES_2);
        return Optional.ofNullable(modelList).orElse(CollUtil.newArrayList())
                .stream()
                .map(DictModel::getText)
                .collect(Collectors.toList());
    }
}
