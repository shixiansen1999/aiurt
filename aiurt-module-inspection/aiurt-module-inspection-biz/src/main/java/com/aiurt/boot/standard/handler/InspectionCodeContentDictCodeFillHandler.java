package com.aiurt.boot.standard.handler;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.modules.handler.ICustomfillDataHandler;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author:wgp
 * @create: 2023-07-07 09:51
 * @Description:
 */
@Component("InspectionCodeContentDictCodeFillHandler")
public class InspectionCodeContentDictCodeFillHandler implements ICustomfillDataHandler {
    @Resource
    private InspectionCodeMapper inspectionCodeMapper;

    @Override
    public List<String> fillData() {
        List<DictModel> modelList = inspectionCodeMapper.querySysDict(InspectionConstant.MODULES_1);
        return Optional.ofNullable(modelList).orElse(CollUtil.newArrayList())
                .stream()
                .map(DictModel::getText)
                .collect(Collectors.toList());
    }
}
