package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.EscalationPlan;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.EscalationPlanExportVO;

import java.util.List;

/**
 * @Description: 提报计划表
 * @Author: Mr.zhao
 * @Date:   2021-11-09
 * @Version: V1.0
 */
public interface IEscalationPlanService extends IService<EscalationPlan> {

	List<EscalationPlanExportVO> selectExportXls(EscalationPlan escalationPlan,List<String> systemCodes);

}
