package com.aiurt.modules.floodpreventioninformation.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.floodpreventioninformation.entity.FloodPreventionInformation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-24
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface FloodPreventionInformationMapper extends BaseMapper<FloodPreventionInformation> {

}
