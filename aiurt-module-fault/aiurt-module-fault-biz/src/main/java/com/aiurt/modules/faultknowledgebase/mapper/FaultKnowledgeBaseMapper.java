package com.aiurt.modules.faultknowledgebase.mapper;

import java.util.List;

import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.dto.DeviceTypeDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface FaultKnowledgeBaseMapper extends BaseMapper<FaultKnowledgeBase> {
    /**
     * 分页查询故障知识库
     * @param page
     * @param condition
     * @return List<FaultAnalysisReport>
     * */
    List<FaultKnowledgeBase> readAll(@Param("page")Page<FaultKnowledgeBase> page, @Param("condition")FaultKnowledgeBase condition,@Param("allSubSystem") List<String> allSubSystem);

    /**
     * 分页查询故障知识库
     * @param id
     * @return List<FaultAnalysisReport>
     * */
    FaultKnowledgeBase readOne(@Param("id")String id);

    /**
     * 设备分类查询
     * @param majorCode
     * @param systemCode
     * @return List<DeviceTypeDTO>
     * */
    List<DeviceTypeDTO> getDeviceType(@Param("majorCode") String majorCode, @Param("systemCode") String systemCode,@Param("name")String name);

    /**
     * 设备分类查询
     * @param deviceTypeCode
     * @return List<DeviceAssemblyDTO>
     * */
    List<DeviceAssemblyDTO> getDeviceAssembly(@Param("deviceTypeCode")String deviceTypeCode);


}
