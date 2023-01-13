package com.aiurt.modules.faultknowledgebase.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@EnableDataPerm
public interface FaultKnowledgeBaseMapper extends BaseMapper<FaultKnowledgeBase> {

    /**
     * 分页查询故障知识库
     * @param page
     * @param condition
     * @param ids
     * @return
     */
    List<FaultKnowledgeBase> readAll(@Param("page")Page<FaultKnowledgeBase> page, @Param("condition")FaultKnowledgeBase condition,@Param("ids") List<String> ids);

    List<FaultKnowledgeBase> readAll2(@Param("page")Page<FaultKnowledgeBase> page, @Param("condition")FaultKnowledgeBase condition,@Param("ids") List<String> ids,@Param("userName")String userName);

    /**
     * 分页查询故障知识库
     * @param id
     * @return List<FaultAnalysisReport>
     * */
    FaultKnowledgeBase readOne(@Param("id")String id);

    /**
     * 设备分类查询
     * @param deviceTypeCode
     * @return List<DeviceAssemblyDTO>
     * */
    List<DeviceAssemblyDTO> getDeviceAssembly(@Param("deviceTypeCode")String deviceTypeCode);


    /**
     * 获取设备组件
     * @param collect
     * @return
     */
    List<DeviceAssemblyDTO> getAllDeviceAssembly(@Param("collect")List<String> collect);

    /**
     * 获取知识库被使用的次数
     * @param id
     * @return
     */
    int getNum(String id);

    /**
     * 根据设备类型名称获取code
     * @param deviceTypeName
     * @return
     */
    List<DeviceType> getDeviceCodeByName(String deviceTypeName);

    /**
     * 根据设备组件名称获取code
     * @param deviceAssemblyName
     * @return
     */
    List<DeviceAssemblyDTO> getDeviceAssemblyCode(String deviceAssemblyName);
}
