package com.aiurt.boot.strategy.service;

import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IInspectionStrategyService extends IService<InspectionStrategy> {
    /**
     * 分页查询
     * @param page
     * @param inspectionStrategyDTO
     * @return
     */
    IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 添加
     * @param inspectionStrategyDTO
     */
    void add(InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 编辑
     * @param inspectionStrategyDTO
     */
    void updateId(InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 删除关联表
     * @param id
     */
    void removeId(String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    InspectionStrategyDTO getId(String id);
    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualPlan(String id);
    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualNewPlan(String id);
}
