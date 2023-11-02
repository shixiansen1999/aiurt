package com.aiurt.modules.flow.mapper;

import com.aiurt.modules.flow.dto.FlowHisTaskDTO;
import com.aiurt.modules.flow.dto.HistoricProcessInstanceDTO;
import com.aiurt.modules.flow.dto.HistoricProcessInstanceReqDTO;
import com.aiurt.modules.flow.dto.HistoricTaskReqDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gaowei
 */
public interface FlowApiServiceMapper {

    /**
     * 查询已办任务
     * @param pageList
     * @param historicTaskReqDTO
     * @return
     */
    public List<FlowHisTaskDTO> listPage(@Param("pageList")Page<FlowHisTaskDTO> pageList, @Param("condition") HistoricTaskReqDTO historicTaskReqDTO);

    /**
     * 查询流程实例
     * @param pageList
     * @param reqDTO
     * @return
     */
    List<HistoricProcessInstanceDTO> listPageHistoricProcessInstance(@Param("pageList") Page<HistoricProcessInstanceDTO> pageList,
                                                                     @Param("condition") HistoricProcessInstanceReqDTO reqDTO);

}
