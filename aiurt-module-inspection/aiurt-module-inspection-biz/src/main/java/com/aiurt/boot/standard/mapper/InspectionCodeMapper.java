package com.aiurt.boot.standard.mapper;


import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.standard.dto.DeviceTypeDTO;
import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.DataScope;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.DictModel;

import java.util.List;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm
public interface InspectionCodeMapper extends BaseMapper<InspectionCode> {
    /**
     * 分页
     * @param inspectionCodeDTO
     * @return
     */
    List<InspectionCodeDTO> pageList(@Param("page")Page<InspectionCodeDTO> page,@Param("inspectionCodeDTO") InspectionCodeDTO inspectionCodeDTO);
    /**
     * 分页判断是否有关联
     * @param inspectionCodeDTO
     * @return
     */
    @DataPermission({
            @DataColumn(key = "majorName",value = "ta.major_code"),
            @DataColumn(key = "systemName",value = "ta.subsystem_code")
    })
    List<InspectionCodeDTO> pageLists(@Param("page")Page<InspectionCodeDTO> page,@Param("inspectionCodeDTO") InspectionCodeDTO inspectionCodeDTO);

    /**
     * 查询是否可以删除
     * @param code
     * @return
     */
    Integer number(@Param("code")String code);
    /**
     * 查询是否显示
     * @param id
     * @return
     */
    Integer number1(@Param("id") String id);

    /**
     * 查询所有子系统信息
     * @return
     */
    List<SubsystemDTO> getSubsystemCode();

    /**
     * 查询所有专业
     * @return
     */
    List<MajorDTO> getMajorCode();
    /**
     * 根据专业code,获取专业名称
     * @param majorCode
     * @return
     */
    String getMajorName(String majorCode);

    /**
     * 查询所有设备类型
     * @return
     */
    List<DeviceTypeDTO> getDeviceTypeInfo();
    /**
     * 设备类型名字
     * @param deviceTypeCode
     * @return
     */
    String deviceTypeCodeName(@Param("deviceTypeCode")String deviceTypeCode);

    /**
     * 查询检修表
     * @param inspectionCodeExcelDto
     * @return
     */
    List<InspectionCodeExcelDTO> getList(@Param("inspectionCodeExcelDto") InspectionCodeExcelDTO inspectionCodeExcelDto);

    /**
     * 查询检修模块字典值
     * @param modules
     * @return
     */
    List<DictModel> querySysDict(@Param("modules")Integer modules);

}
