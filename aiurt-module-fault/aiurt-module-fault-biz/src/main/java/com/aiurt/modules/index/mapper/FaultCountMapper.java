package com.aiurt.modules.index.mapper;

import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FaultCountMapper extends BaseMapper<FaultIndexDTO> {
    List<Fault> queryFaultCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
