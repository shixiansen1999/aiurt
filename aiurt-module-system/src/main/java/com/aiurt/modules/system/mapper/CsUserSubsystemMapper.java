package com.aiurt.modules.system.mapper;

import java.util.List;

import com.aiurt.modules.subsystem.dto.ListDTO;
import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.dto.SystemByCodeDTO;
import com.aiurt.modules.subsystem.dto.YearFaultDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.system.vo.CsUserSubsystemModel;

/**
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserSubsystemMapper extends BaseMapper<CsUserSubsystem> {
    /**
     * 获取子系统
     * @param userId
     * @return
     */
    List<String> getSubsystemIds(@Param("userId") String userId);
    /**
     * 根据用户id获取站点
     * @param id
     * @return
     */
    List<CsUserSubsystemModel> getSubsystemByUserId(@Param("id") String id);

    /**
     * 根据用户Id获取子系统
     *
     * @param page
     * @param id
     * @return
     */
    List<SubsystemFaultDTO> selectByUserId(Page<SubsystemFaultDTO> page, @Param("id") String id);

    /**
     * 查询
     * @param time
     * @param subsystemCode
     * @return
     */
    SubsystemFaultDTO getSubsystemFaultDTO(@Param("time") String time, @Param("subsystemCode") String subsystemCode);

    /**
     * 查询
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    List<SubsystemFaultDTO> getSubsystemByDeviceTypeCode( @Param("subsystemCode") String subsystemCode,@Param("deviceTypeCode") List<String> deviceTypeCode);

    /**
     *  设备类型数据
     * @param time
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    SubsystemFaultDTO getSubsystemByDeviceType(@Param("time") String time,@Param("subsystemCode") String subsystemCode, @Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 查询次数
     * @param time
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    Long getNum(@Param("time") String time,@Param("subsystemCode") String subsystemCode, @Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 年次数
     * @param id
     * @return
     */
    List<YearFaultDTO> getYearNumFault(@Param("id") String id);

    /**
     * 设备类型年次数
     * @param code
     * @param deviceTypeCode
     * @return
     */
    YearFaultDTO getDeviceTypeYearFault(@Param("code") String code,@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 年分钟
     * @param code
     * @return
     */
    List<ListDTO> sysTemYearFault(@Param("code") String code);

    /**
     * 设备类型数据查询
     * @param code
     * @param deviceTypeCode
     * @return
     */
    List<ListDTO> deviceTypeFault(@Param("code")String code,@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 根据code查询
     * @param subsystemCode
     * @return
     */
    SystemByCodeDTO getSystemByCodeDTO(@Param("subsystemCode") String subsystemCode);

    /**
     * 查询备件跟换次数
     * @param subsystemCode
     * @return
     */
    Integer getReplacementNum(String subsystemCode);

    /**
     * 查询子系统名称code id
     * @param subsystemCode
     * @return
     */
    SubsystemFaultDTO selectSubSystem(@Param("subsystemCode")SubsystemFaultDTO subsystemCode);
}
