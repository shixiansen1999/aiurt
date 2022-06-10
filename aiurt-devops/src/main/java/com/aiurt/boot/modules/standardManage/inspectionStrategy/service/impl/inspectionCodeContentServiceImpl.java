package com.aiurt.boot.modules.standardManage.inspectionStrategy.service.impl;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.mapper.InspectionCodeContentMapper;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.service.IinspectionCodeContentService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 检修策略管理
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class inspectionCodeContentServiceImpl extends ServiceImpl<InspectionCodeContentMapper, InspectionCodeContent> implements IinspectionCodeContentService {

    @Override
    public Result setStrategyByIds(String ids, Integer tactics) {
        String[] split = ids.split(",");
        if (split.length == 1){
            this.updateTacticsById(ids,tactics);
            return Result.ok();
        }
        for (String id : split) {
            this.updateTacticsById(id,tactics);
        }
        return Result.ok();
    }

    //更新策略通过ID
    public void updateTacticsById(String id,Integer tactics){
        InspectionCodeContent select = this.baseMapper.selectById(id);
        select.setTactics(tactics);
        this.baseMapper.updateById(select);
    }
}
