package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.EscalationPlan;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.EscalationPlanMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IEscalationPlanService;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.EscalationPlanExportVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 提报计划表
 * @Author: Mr.zhao
 * @Date: 2021-11-09
 * @Version: V1.0
 */
@Service
public class EscalationPlanServiceImpl extends ServiceImpl<EscalationPlanMapper, EscalationPlan> implements IEscalationPlanService {

	@Override
	public List<EscalationPlanExportVO> selectExportXls(EscalationPlan escalationPlan, List<String> systemCodes) {
		return this.baseMapper.selectExportXls(escalationPlan,systemCodes);
	}
}
