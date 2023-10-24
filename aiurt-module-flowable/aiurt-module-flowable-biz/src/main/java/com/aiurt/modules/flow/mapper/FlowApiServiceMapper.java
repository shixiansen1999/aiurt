package com.aiurt.modules.flow.mapper;

import com.aiurt.modules.flow.dto.FlowHisTaskDTO;
import com.aiurt.modules.flow.dto.HistoricTaskReqDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FlowApiServiceMapper {

    public List<FlowHisTaskDTO> listPage(@Param("pageList")Page<FlowHisTaskDTO> pageList, @Param("condition") HistoricTaskReqDTO historicTaskReqDTO);
}
