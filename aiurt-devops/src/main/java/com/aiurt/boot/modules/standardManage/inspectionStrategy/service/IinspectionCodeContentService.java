package com.aiurt.boot.modules.standardManage.inspectionStrategy.service;

import com.aiurt.boot.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 检修策略管理
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IinspectionCodeContentService extends IService<InspectionCodeContent> {

    Result setStrategyByIds(String ids, Integer tactics);
}
