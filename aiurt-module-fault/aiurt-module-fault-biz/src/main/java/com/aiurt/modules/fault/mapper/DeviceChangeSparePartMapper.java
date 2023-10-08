package com.aiurt.modules.fault.mapper;


import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.common.result.SpareResult;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.faultanalysisreport.dto.SpareConsumeDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 备件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@EnableDataPerm
public interface DeviceChangeSparePartMapper extends BaseMapper<DeviceChangeSparePart> {

    /**
     * 根据故障编码以及维修记录id查询换件信息
     * @param faultCode 故障编码
     * @param recordId 维修记录id
     * @return
     */
    List<DeviceChangeSparePart> queryDeviceChangeByFaultCode(@Param("faultCode") String faultCode, @Param("recordId") String recordId);


    /**
     * 统计该时间段内的备件消耗品
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "mb.sys_org_code"),
            @DataColumn(key = "majorName",value = "mb.major_code"),
            @DataColumn(key = "systemName",value = "mb.system_code"),
    })
    List<SpareConsumeDTO> querySpareConsume(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
    List<SpareConsumeDTO> querySpareConsume(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("orgCodeList")List<String> orgCodeList);

    /**
     * @param faultCode
     * @param id
     */
    List<SpareResult> getSparePart(@Param("faultCode")String faultCode, @Param("recordId") String id);
    /**
     * 根据故障编码以及维修记录id查询换件信息，包含领用单中所有的新组件
     * @param faultCode 故障编码
     * @param recordId 维修记录id
     * @return
     */
    List<DeviceChangeSparePart> getAllDeviceChange(@Param("faultCode") String faultCode, @Param("recordId") String recordId);

}
