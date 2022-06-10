package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.EscalationPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.EscalationPlanExportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 提报计划表
 * @Author: swsc
 * @Date:   2021-11-09
 * @Version: V1.0
 */
public interface EscalationPlanMapper extends BaseMapper<EscalationPlan> {

	List<EscalationPlanExportVO> selectExportXls(@Param("param") EscalationPlan escalationPlan,@Param("systemCodes") List<String> systemCodes);
}
